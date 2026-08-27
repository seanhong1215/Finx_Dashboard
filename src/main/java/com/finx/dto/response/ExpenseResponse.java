package com.finx.dto.response;

import com.finx.model.Expense;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ExpenseResponse {
    private final Long id;
    private final String category;
    private final String merchant;
    private final String note;
    private final BigDecimal amount;
    private final LocalDate spentOn;
    private final Long creditCardId;
    private final String creditCardLabel;

    public static ExpenseResponse from(Expense expense) {
        String label = null;
        Long cardId = null;
        if (expense.getCreditCard() != null) {
            cardId = expense.getCreditCard().getId();
            label = expense.getCreditCard().getBankName() + " " +
                    expense.getCreditCard().getCardName() + " • " +
                    expense.getCreditCard().getLastFourDigits();
        }
        return new ExpenseResponse(
                expense.getId(),
                expense.getCategory(),
                expense.getMerchant(),
                expense.getNote(),
                expense.getAmount(),
                expense.getSpentOn(),
                cardId,
                label
        );
    }
}
