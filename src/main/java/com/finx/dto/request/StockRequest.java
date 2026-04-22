package com.finx.dto.request;

import javax.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockRequest {

    @NotBlank(message = "Ticker symbol is required")
    @Size(max = 10, message = "Ticker must be at most 10 characters")
    private String ticker;

    @NotBlank(message = "Company name is required")
    @Size(max = 100, message = "Company name must be at most 100 characters")
    private String company;

    @NotNull(message = "Shares is required")
    @DecimalMin(value = "0.0001", message = "Shares must be greater than 0")
    private BigDecimal shares;

    @NotNull(message = "Average cost is required")
    @DecimalMin(value = "0.01", message = "Average cost must be greater than 0")
    private BigDecimal avgCost;

    @NotNull(message = "Current price is required")
    @DecimalMin(value = "0", message = "Current price cannot be negative")
    private BigDecimal currentPrice;

    private BigDecimal changePct;
}
