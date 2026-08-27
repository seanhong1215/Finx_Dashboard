package com.finx.dto.request;

import com.finx.model.CreditCard;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class CreditCardRequest {
    @NotBlank
    @Size(max = 80)
    private String bankName;

    @NotBlank
    @Size(max = 80)
    private String cardName;

    @NotNull
    private CreditCard.CardNetwork network;

    @NotBlank
    @Pattern(regexp = "\\d{4}")
    private String lastFourDigits;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal creditLimit;

    @NotNull
    @Min(1)
    @Max(31)
    private Integer statementDay;

    @NotNull
    @Min(1)
    @Max(31)
    private Integer paymentDueDay;
}
