package com.finx.model;

import javax.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, length = 200)
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Direction direction;

    @Column(name = "balance_after", nullable = false, precision = 15, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "ref_number", length = 50)
    private String refNumber;

    @Column(name = "transacted_at", nullable = false)
    private LocalDateTime transactedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (transactedAt == null) transactedAt = LocalDateTime.now();
    }

    public enum TransactionType { INCOME, EXPENSE, TRANSFER }
    public enum Direction { IN, OUT }

    @Transient
    public boolean isIncome() {
        return direction == Direction.IN;
    }

    @Transient
    public String getSignedAmount() {
        return (direction == Direction.IN ? "+" : "-") +
               String.format("%,.0f", amount);
    }
}
