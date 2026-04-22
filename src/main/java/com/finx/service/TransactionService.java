package com.finx.service;

import com.finx.dto.request.TransactionRequest;
import com.finx.exception.ResourceNotFoundException;
import com.finx.model.Account;
import com.finx.model.Transaction;
import com.finx.repository.AccountRepository;
import com.finx.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public List<Transaction> findAll(Long userId) {
        Account account = getActiveAccount(userId);
        return transactionRepository.findByAccountIdOrderByTransactedAtDesc(account.getId());
    }

    @Transactional(readOnly = true)
    public List<Transaction> findByCategory(Long userId, String category) {
        Account account = getActiveAccount(userId);
        return transactionRepository.findByAccountIdAndCategory(account.getId(), category);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findByDirection(Long userId, Transaction.Direction direction) {
        Account account = getActiveAccount(userId);
        return transactionRepository.findByAccountIdAndDirection(account.getId(), direction);
    }

    @Transactional
    public Transaction create(Long userId, TransactionRequest req) {
        Account account = getActiveAccount(userId);
        Transaction tx = Transaction.builder()
                .account(account)
                .type(req.getType())
                .category(req.getCategory())
                .description(req.getDescription())
                .amount(req.getAmount())
                .direction(req.getDirection())
                .balanceAfter(account.getBalance())
                .transactedAt(LocalDateTime.now())
                .build();
        Transaction saved = transactionRepository.save(tx);
        log.info("Created transaction id={} for userId={}", saved.getId(), userId);
        return saved;
    }

    @Transactional
    public void delete(Long userId, Long transactionId) {
        Account account = getActiveAccount(userId);
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", transactionId));
        if (!tx.getAccount().getId().equals(account.getId())) {
            throw new ResourceNotFoundException("Transaction", transactionId);
        }
        transactionRepository.delete(tx);
        log.info("Deleted transaction id={} for userId={}", transactionId, userId);
    }

    private Account getActiveAccount(Long userId) {
        return accountRepository.findFirstByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active account found for user id: " + userId));
    }
}
