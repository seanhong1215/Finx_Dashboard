package com.finx.service;

import com.finx.dto.request.ExpenseRequest;
import com.finx.dto.response.ExpenseResponse;
import com.finx.exception.ResourceNotFoundException;
import com.finx.model.CreditCard;
import com.finx.model.Expense;
import com.finx.model.User;
import com.finx.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserService userService;
    private final CreditCardService creditCardService;

    @Transactional(readOnly = true)
    public List<ExpenseResponse> search(Long userId, LocalDate from, LocalDate to, String category, Long creditCardId) {
        LocalDate start = from != null ? from : LocalDate.now().withDayOfMonth(1);
        LocalDate end = to != null ? to : LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        return expenseRepository.search(userId, start, end, blankToNull(category), creditCardId).stream()
                .map(ExpenseResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ExpenseResponse create(Long userId, ExpenseRequest request) {
        User user = userService.findActiveById(userId);
        Expense expense = Expense.builder()
                .user(user)
                .creditCard(resolveCard(userId, request.getCreditCardId()))
                .category(request.getCategory())
                .merchant(request.getMerchant())
                .note(request.getNote())
                .amount(request.getAmount())
                .spentOn(request.getSpentOn())
                .build();
        return ExpenseResponse.from(expenseRepository.save(expense));
    }

    @Transactional
    public ExpenseResponse update(Long userId, Long id, ExpenseRequest request) {
        Expense expense = findOwnedExpense(userId, id);
        expense.setCreditCard(resolveCard(userId, request.getCreditCardId()));
        expense.setCategory(request.getCategory());
        expense.setMerchant(request.getMerchant());
        expense.setNote(request.getNote());
        expense.setAmount(request.getAmount());
        expense.setSpentOn(request.getSpentOn());
        return ExpenseResponse.from(expenseRepository.save(expense));
    }

    @Transactional
    public void delete(Long userId, Long id) {
        Expense expense = findOwnedExpense(userId, id);
        expenseRepository.delete(expense);
    }

    private Expense findOwnedExpense(Long userId, Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", id));
        if (!expense.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Expense", id);
        }
        return expense;
    }

    private CreditCard resolveCard(Long userId, Long creditCardId) {
        if (creditCardId == null) {
            return null;
        }
        return creditCardService.findOwnedCard(userId, creditCardId);
    }

    private String blankToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value;
    }
}
