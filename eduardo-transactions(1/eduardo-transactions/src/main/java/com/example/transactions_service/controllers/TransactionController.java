package com.example.transactions_service.controllers;
import com.example.transactions_service.models.TransactionModel;
import com.example.transactions_service.repositories.TransactionRepository;
import com.example.transactions_service.services.*;

import jakarta.websocket.server.PathParam;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;




@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    
    @Autowired    
    public TransactionService transactionService;

    

@GetMapping
public List<TransactionModel> get() {
    return transactionService.getAll(); 
}
@GetMapping("/{id}")
public TransactionModel getById(@PathVariable Long id){
    return transactionService.getById(id); 
}


@PostMapping()
public TransactionModel postMethodName(@RequestBody TransactionModel  transaction) {

    
    return transactionService.saveTransaction(transaction);
}
@PutMapping("path/{id}")
public TransactionModel update(@PathVariable Long id, @RequestBody TransactionModel newDetTransactionModel) {
    
    
    return transactionService.update(id, newDetTransactionModel);
}

@org.springframework.web.bind.annotation.DeleteMapping("/{id}")
public org.springframework.http.ResponseEntity<Void> delete(@PathVariable Long id) {
    transactionService.delete(id);
    return org.springframework.http.ResponseEntity.noContent().build();
}




    



    
}
