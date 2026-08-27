package com.finx.dto.response;

import com.finx.model.CreditCard;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CreditCardResponse {
    private final Long id;
    private final String bankName;
    private final String cardName;
    private final CreditCard.CardNetwork network;
    private final String lastFourDigits;
    private final BigDecimal creditLimit;
    private final Integer statementDay;
    private final Integer paymentDueDay;
    private final Boolean currentCyclePaid;
    private final BigDecimal currentMonthExpense;

    public static CreditCardResponse from(CreditCard card, BigDecimal currentMonthExpense) {
        return new CreditCardResponse(
                card.getId(),
                card.getBankName(),
                card.getCardName(),
                card.getNetwork(),
                card.getLastFourDigits(),
                card.getCreditLimit(),
                card.getStatementDay(),
                card.getPaymentDueDay(),
                card.getCurrentCyclePaid(),
                currentMonthExpense
        );
    }
}
