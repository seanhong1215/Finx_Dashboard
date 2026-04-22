package com.finx.service;

import com.finx.dto.request.TransactionRequest;
import com.finx.exception.ResourceNotFoundException;
import com.finx.model.Account;
import com.finx.model.Transaction;
import com.finx.repository.AccountRepository;
import com.finx.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService unit tests")
class TransactionServiceTest {

    @Mock TransactionRepository transactionRepository;
    @Mock AccountRepository accountRepository;

    @InjectMocks TransactionService transactionService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
                .id(1L)
                .accountNumber("ACC-001")
                .balance(new BigDecimal("100000"))
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("findAll should return transactions for user's account")
    void findAll_shouldReturnTransactions() {
        given(accountRepository.findFirstByUserIdAndIsActiveTrue(1L))
                .willReturn(Optional.of(testAccount));
        given(transactionRepository.findByAccountIdOrderByTransactedAtDesc(1L))
                .willReturn(Arrays.asList(new Transaction(), new Transaction()));

        List<Transaction> result = transactionService.findAll(1L);

        assertThat(result).hasSize(2);
        verify(transactionRepository).findByAccountIdOrderByTransactedAtDesc(1L);
    }

    @Test
    @DisplayName("findAll should throw ResourceNotFoundException when no active account")
    void findAll_shouldThrow_whenNoActiveAccount() {
        given(accountRepository.findFirstByUserIdAndIsActiveTrue(99L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.findAll(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No active account");
    }

    @Test
    @DisplayName("create should save and return new transaction")
    void create_shouldSaveTransaction() {
        TransactionRequest req = new TransactionRequest();
        req.setType(Transaction.TransactionType.EXPENSE);
        req.setCategory("Food");
        req.setDescription("Lunch");
        req.setAmount(new BigDecimal("250"));
        req.setDirection(Transaction.Direction.OUT);

        Transaction saved = Transaction.builder()
                .id(10L)
                .account(testAccount)
                .type(req.getType())
                .category(req.getCategory())
                .description(req.getDescription())
                .amount(req.getAmount())
                .direction(req.getDirection())
                .balanceAfter(new BigDecimal("99750"))
                .build();

        given(accountRepository.findFirstByUserIdAndIsActiveTrue(1L))
                .willReturn(Optional.of(testAccount));
        given(transactionRepository.save(any(Transaction.class))).willReturn(saved);

        Transaction result = transactionService.create(1L, req);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getCategory()).isEqualTo("Food");
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("delete should remove transaction owned by user")
    void delete_shouldRemoveTransaction() {
        Transaction tx = Transaction.builder().id(5L).account(testAccount).build();

        given(accountRepository.findFirstByUserIdAndIsActiveTrue(1L))
                .willReturn(Optional.of(testAccount));
        given(transactionRepository.findById(5L)).willReturn(Optional.of(tx));

        transactionService.delete(1L, 5L);

        verify(transactionRepository).delete(tx);
    }

    @Test
    @DisplayName("delete should throw when transaction does not belong to user")
    void delete_shouldThrow_whenNotOwned() {
        Account otherAccount = Account.builder().id(99L).isActive(true).build();
        Transaction tx = Transaction.builder().id(5L).account(otherAccount).build();

        given(accountRepository.findFirstByUserIdAndIsActiveTrue(1L))
                .willReturn(Optional.of(testAccount));
        given(transactionRepository.findById(5L)).willReturn(Optional.of(tx));

        assertThatThrownBy(() -> transactionService.delete(1L, 5L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
