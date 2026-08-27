package com.finx.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class DashboardResponse {
    private final BigDecimal monthExpense;
    private final BigDecimal unpaidCreditCardAmount;
    private final long expenseCount;
    private final long creditCardCount;
    private final Map<String, BigDecimal> categoryTotals;
    private final List<ExpenseResponse> recentExpenses;
    private final Map<String, BigDecimal> monthlyTotals;
    private final Map<String, BigDecimal> cardTotals;
    private final String selectedMonth;
    private final BigDecimal previousMonthExpense;
}
