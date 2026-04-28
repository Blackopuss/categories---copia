package com.example.transactions_service.services;
import com.example.transactions_service.models.*;
import com.example.transactions_service.repositories.*;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository; 
    
    @Autowired
    private org.springframework.web.client.RestTemplate restTemplate;

    public List<TransactionModel> getAll(){
        return transactionRepository.findAll(); 
    }
    
    public TransactionModel saveTransaction(TransactionModel transaction){ 
        try {
            java.util.Map<String, Object> card = restTemplate.getForObject("http://card-service/api/cards/" + transaction.getCardId(), java.util.Map.class);
            if (card == null) {
                throw new RuntimeException("La tarjeta no existe");
            }
            
            Number cardUserId = (Number) card.get("userId");
            if (cardUserId == null || cardUserId.longValue() != transaction.getUserId()) {
                throw new RuntimeException("La tarjeta no pertenece al usuario especificado");
            }
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            throw new RuntimeException("Error al validar la tarjeta: " + e.getMessage());
        }

        return transactionRepository.save(transaction);
    }
    
    public TransactionModel getById(Long id){
      return  transactionRepository.findById(id).orElse(null); 

     


        }     

        public TransactionModel update(Long id, TransactionModel newDetails){ 
            
            TransactionModel tr = transactionRepository.findById(id).orElse(null); 
            if(tr!=null){ 
                tr.setUserId(newDetails.getUserId());
                tr.setCardId(newDetails.getCardId());
                tr.setAmount(newDetails.getAmount());
                tr.setType(newDetails.getType());
                tr.setCategory(newDetails.getCategory());
                tr.setDescription(newDetails.getDescription());
                tr.setTransactionDate(newDetails.getTransactionDate());
                tr.setUpdatedAt(newDetails.getUpdatedAt());
                return transactionRepository.save(tr);

            }
            return null;
        }

        public void delete(Long id) {
            transactionRepository.deleteById(id);
        }
    
}
