package com.example.transactions_service.repositories;
import com.example.transactions_service.models.TransactionModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface TransactionRepository extends JpaRepository<TransactionModel, Long> {

    
}
