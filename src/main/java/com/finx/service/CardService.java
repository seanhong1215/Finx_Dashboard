package com.finx.service;

import com.finx.dto.request.CardRequest;
import com.finx.exception.BusinessException;
import com.finx.exception.ResourceNotFoundException;
import com.finx.model.Account;
import com.finx.model.Card;
import com.finx.repository.AccountRepository;
import com.finx.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public List<Card> findActiveCards(Long userId) {
        Account account = getActiveAccount(userId);
        return cardRepository.findByAccountIdAndIsActiveTrue(account.getId());
    }

    @Transactional
    public Card create(Long userId, CardRequest req) {
        Account account = getActiveAccount(userId);
        if (cardRepository.existsByCardNumber(req.getCardNumber())) {
            throw new BusinessException("Card number already exists");
        }
        Card card = Card.builder()
                .account(account)
                .cardNumber(req.getCardNumber())
                .cardType(req.getCardType())
                .cardHolder(req.getCardHolder().toUpperCase())
                .expiryDate(req.getExpiryDate())
                .creditLimit(req.getCreditLimit())
                .isActive(true)
                .build();
        Card saved = cardRepository.save(card);
        log.info("Created card id={} for userId={}", saved.getId(), userId);
        return saved;
    }

    @Transactional
    public void delete(Long userId, Long cardId) {
        Account account = getActiveAccount(userId);
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card", cardId));
        if (!card.getAccount().getId().equals(account.getId())) {
            throw new ResourceNotFoundException("Card", cardId);
        }
        cardRepository.delete(card);
        log.info("Deleted card id={} for userId={}", cardId, userId);
    }

    private Account getActiveAccount(Long userId) {
        return accountRepository.findFirstByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active account found for user id: " + userId));
    }
}
