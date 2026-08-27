package com.finx.dto.request;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExpenseRequest {
    private Long creditCardId;

    @NotBlank
    @Size(max = 50)
    private String category;

    @NotBlank
    @Size(max = 120)
    private String merchant;

    @Size(max = 300)
    private String note;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotNull
    private LocalDate spentOn;
}
