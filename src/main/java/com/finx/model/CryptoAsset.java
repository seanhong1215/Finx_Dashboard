package com.finx.model;

import javax.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "crypto_assets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CryptoAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 10)
    private String symbol;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, precision = 20, scale = 8)
    @Builder.Default
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "avg_cost", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal avgCost = BigDecimal.ZERO;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    @Transient
    public String getColor() {
        switch (symbol) {
            case "BTC": return "#F7931A";
            case "ETH": return "#627EEA";
            case "SOL": return "#9945FF";
            case "BNB": return "#F3BA2F";
            default:    return "#7C6FF7";
        }
    }
}
