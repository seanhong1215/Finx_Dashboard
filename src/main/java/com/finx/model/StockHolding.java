package com.finx.model;

import javax.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_holdings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockHolding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 10)
    private String ticker;

    @Column(nullable = false, length = 100)
    private String company;

    @Column(nullable = false, precision = 10, scale = 4)
    @Builder.Default
    private BigDecimal shares = BigDecimal.ZERO;

    @Column(name = "avg_cost", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal avgCost = BigDecimal.ZERO;

    @Column(name = "current_price", precision = 15, scale = 2)
    private BigDecimal currentPrice;

    @Column(name = "change_pct", precision = 6, scale = 2)
    private BigDecimal changePct;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    @Transient
    public BigDecimal getMarketValue() {
        if (currentPrice == null) return BigDecimal.ZERO;
        return currentPrice.multiply(shares).setScale(2, RoundingMode.HALF_UP);
    }

    @Transient
    public BigDecimal getProfitLoss() {
        if (currentPrice == null) return BigDecimal.ZERO;
        return currentPrice.subtract(avgCost).multiply(shares).setScale(2, RoundingMode.HALF_UP);
    }

    @Transient
    public boolean isProfit() {
        return getProfitLoss().compareTo(BigDecimal.ZERO) >= 0;
    }
}
