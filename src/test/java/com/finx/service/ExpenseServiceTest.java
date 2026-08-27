package com.finx.service;

import com.finx.dto.request.ExpenseRequest;
import com.finx.dto.response.ExpenseResponse;
import com.finx.exception.ResourceNotFoundException;
import com.finx.model.Expense;
import com.finx.model.User;
import com.finx.repository.ExpenseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExpenseService unit tests")
class ExpenseServiceTest {

    @Mock ExpenseRepository expenseRepository;
    @Mock UserService userService;
    @Mock CreditCardService creditCardService;

    @InjectMocks ExpenseService expenseService;

    @Test
    @DisplayName("create should persist an expense for the current user")
    void create_shouldPersistExpense() {
        User user = User.builder().id(1L).username("james").email("james@example.com").fullName("James").build();
        ExpenseRequest request = new ExpenseRequest();
        request.setCategory("餐飲");
        request.setMerchant("星巴克");
        request.setAmount(new BigDecimal("195"));
        request.setSpentOn(LocalDate.of(2026, 8, 4));

        Expense saved = Expense.builder()
                .id(10L)
                .user(user)
                .category(request.getCategory())
                .merchant(request.getMerchant())
                .amount(request.getAmount())
                .spentOn(request.getSpentOn())
                .build();

        given(userService.findActiveById(1L)).willReturn(user);
        given(expenseRepository.save(any(Expense.class))).willReturn(saved);

        ExpenseResponse response = expenseService.create(1L, request);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getMerchant()).isEqualTo("星巴克");
        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    @DisplayName("delete should reject expenses owned by another user")
    void delete_shouldRejectOtherUsersExpense() {
        User otherUser = User.builder().id(99L).username("other").email("other@example.com").fullName("Other").build();
        Expense expense = Expense.builder().id(5L).user(otherUser).build();
        given(expenseRepository.findById(5L)).willReturn(Optional.of(expense));

        assertThatThrownBy(() -> expenseService.delete(1L, 5L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
