package com.finx.repository;

import com.finx.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByAccountIdAndIsActiveTrue(Long accountId);

    boolean existsByCardNumber(String cardNumber);
}
