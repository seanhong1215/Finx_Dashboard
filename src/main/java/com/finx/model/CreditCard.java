package com.finx.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 80)
    private String bankName;

    @Column(nullable = false, length = 80)
    private String cardName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardNetwork network;

    @Column(nullable = false, length = 4)
    private String lastFourDigits;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal creditLimit;

    @Column(nullable = false)
    private Integer statementDay;

    @Column(nullable = false)
    private Integer paymentDueDay;

    @Column(name = "current_cycle_paid", nullable = false)
    @Builder.Default
    private Boolean currentCyclePaid = false;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum CardNetwork { VISA, MASTERCARD, JCB, AMEX, OTHER }
}
