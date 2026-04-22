package com.finx.dto.request;

import com.finx.model.Card;
import javax.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardRequest {

    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "\\d{4}[- ]\\d{4}[- ]\\d{4}[- ]\\d{4}",
             message = "Card number must follow format: XXXX-XXXX-XXXX-XXXX")
    private String cardNumber;

    @NotNull(message = "Card type is required")
    private Card.CardType cardType;

    @NotBlank(message = "Card holder name is required")
    @Size(max = 100, message = "Card holder name must be at most 100 characters")
    private String cardHolder;

    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;

    @DecimalMin(value = "0", message = "Credit limit cannot be negative")
    private BigDecimal creditLimit;
}
