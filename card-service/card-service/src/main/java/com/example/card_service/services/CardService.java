package com.example.card_service.services;

import com.example.card_service.models.Card;
import com.example.card_service.repositories.CardRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CardService {

    private final CardRepository repository;
    private final RestTemplate restTemplate;

    public CardService(CardRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    private boolean userExists(Long userId){
        try{
            String url = "http://userservice/api/users/" + userId;
            System.out.println("Llamando a: " + url);

            Object response = restTemplate.getForObject(url, Object.class);

            System.out.println("Usuario encontrado: " + response);

            return true;
        }catch (Exception e){
            System.out.println("ERROR: " + e.getMessage());
            return false;
        }
    }

    public Card addCard(Card card){

        if(!userExists(card.getUserId())){
            throw new RuntimeException("El usuario no existe");
        }

        return repository.save(card);
    }

    public List<Card> getCardsByUser(Long userId){
        return repository.findByUserId(userId);
    }

    public Card getCardById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Tarjeta no encontrada"));
    }

    public List<Card> getAllCards() {
        return repository.findAll();
    }

    public Card updateCard(Long id, Card updated){

        Card card = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarjeta no encontrada"));

        card.setCardName(updated.getCardName());
        card.setCardNumber(updated.getCardNumber());

        return repository.save(card);
    }

    public void deleteCard(Long id){
        repository.deleteById(id);
    }

    public Card addBalance(Long id, Double amount){

        Card card = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarjeta no encontrada"));

        card.setBalance(card.getBalance() + amount);

        return repository.save(card);
    }

    public Double getTotalMoney(Long userId){

        List<Card> cards = repository.findByUserId(userId);

        return cards.stream()
                .mapToDouble(Card::getBalance)
                .sum();
    }
}