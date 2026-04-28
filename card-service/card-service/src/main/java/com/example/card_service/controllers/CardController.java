package com.example.card_service.controllers;

import com.example.card_service.models.Card;
import com.example.card_service.services.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService service;

    public CardController(CardService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Card> addCard(@RequestBody Card card) {
        return ResponseEntity.ok(service.addCard(card));
    }

    @GetMapping
    public ResponseEntity<java.util.List<Card>> getAllCards() {
        return ResponseEntity.ok(service.getAllCards());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Card> getCard(@PathVariable Long id) {
        return ResponseEntity.ok(service.getCardById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Card> updateCard(@PathVariable Long id, @RequestBody Card card) {
        return ResponseEntity.ok(service.updateCard(id, card));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>  deleteCard(@PathVariable Long id) {
        service.deleteCard(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/balance")
    public ResponseEntity<Card> addBalance(@PathVariable Long id, @RequestParam Double amount) {
        return ResponseEntity.ok(service.addBalance(id, amount));
    }

    @GetMapping("/user/{userId}/total")
    public ResponseEntity<Double> getTotalMoney(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getTotalMoney(userId));
    }

}