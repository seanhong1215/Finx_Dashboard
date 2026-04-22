package com.finx.dto.request;

import javax.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CryptoRequest {

    @NotBlank(message = "Symbol is required")
    @Size(max = 10, message = "Symbol must be at most 10 characters")
    private String symbol;

    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name must be at most 50 characters")
    private String name;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.00000001", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Average cost is required")
    @DecimalMin(value = "0.01", message = "Average cost must be greater than 0")
    private BigDecimal avgCost;
}
