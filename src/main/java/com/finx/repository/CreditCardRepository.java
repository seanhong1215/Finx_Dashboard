package com.finx.repository;

import com.finx.model.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {

    List<CreditCard> findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(Long userId);

    Optional<CreditCard> findByIdAndUserId(Long id, Long userId);

    long countByUserIdAndIsActiveTrue(Long userId);
}
