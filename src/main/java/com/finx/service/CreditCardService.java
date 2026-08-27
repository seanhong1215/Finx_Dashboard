package com.finx.service;

import com.finx.dto.request.CreditCardRequest;
import com.finx.dto.response.CreditCardResponse;
import com.finx.exception.ResourceNotFoundException;
import com.finx.model.CreditCard;
import com.finx.model.User;
import com.finx.repository.CreditCardRepository;
import com.finx.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreditCardService {

    private final CreditCardRepository creditCardRepository;
    private final ExpenseRepository expenseRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<CreditCardResponse> findCards(Long userId) {
        LocalDate now = LocalDate.now();
        LocalDate start = now.withDayOfMonth(1);
        LocalDate end = now.withDayOfMonth(now.lengthOfMonth());
        return creditCardRepository.findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(userId).stream()
                .map(card -> CreditCardResponse.from(card, expenseRepository.sumByCardAndPeriod(card.getId(), start, end)))
                .collect(Collectors.toList());
    }

    @Transactional
    public CreditCardResponse create(Long userId, CreditCardRequest request) {
        User user = userService.findActiveById(userId);
        CreditCard card = CreditCard.builder()
                .user(user)
                .bankName(request.getBankName())
                .cardName(request.getCardName())
                .network(request.getNetwork())
                .lastFourDigits(request.getLastFourDigits())
                .creditLimit(request.getCreditLimit())
                .statementDay(request.getStatementDay())
                .paymentDueDay(request.getPaymentDueDay())
                .currentCyclePaid(false)
                .isActive(true)
                .build();
        return CreditCardResponse.from(creditCardRepository.save(card), BigDecimal.ZERO);
    }

    @Transactional
    public CreditCardResponse update(Long userId, Long id, CreditCardRequest request) {
        CreditCard card = findOwnedCard(userId, id);
        card.setBankName(request.getBankName());
        card.setCardName(request.getCardName());
        card.setNetwork(request.getNetwork());
        card.setLastFourDigits(request.getLastFourDigits());
        card.setCreditLimit(request.getCreditLimit());
        card.setStatementDay(request.getStatementDay());
        card.setPaymentDueDay(request.getPaymentDueDay());
        CreditCard saved = creditCardRepository.save(card);
        LocalDate now = LocalDate.now();
        return CreditCardResponse.from(saved, expenseRepository.sumByCardAndPeriod(
                saved.getId(), now.withDayOfMonth(1), now.withDayOfMonth(now.lengthOfMonth())));
    }

    @Transactional
    public CreditCardResponse updatePaymentStatus(Long userId, Long id, boolean paid) {
        CreditCard card = findOwnedCard(userId, id);
        card.setCurrentCyclePaid(paid);
        CreditCard saved = creditCardRepository.save(card);
        LocalDate now = LocalDate.now();
        return CreditCardResponse.from(saved, expenseRepository.sumByCardAndPeriod(
                saved.getId(), now.withDayOfMonth(1), now.withDayOfMonth(now.lengthOfMonth())));
    }

    @Transactional
    public void delete(Long userId, Long id) {
        CreditCard card = findOwnedCard(userId, id);
        card.setIsActive(false);
        creditCardRepository.save(card);
    }

    @Transactional(readOnly = true)
    public CreditCard findOwnedCard(Long userId, Long id) {
        return creditCardRepository.findByIdAndUserId(id, userId)
                .filter(card -> Boolean.TRUE.equals(card.getIsActive()))
                .orElseThrow(() -> new ResourceNotFoundException("CreditCard", id));
    }
}
