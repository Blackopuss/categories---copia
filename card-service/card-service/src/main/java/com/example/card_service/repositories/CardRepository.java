package com.example.card_service.repositories;

import com.example.card_service.models.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository  extends JpaRepository<Card, Long> {
    List<Card> findByUserId(Long userId);
}
