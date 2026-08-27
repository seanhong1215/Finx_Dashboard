package com.finx.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class AdminSummaryResponse {
    private final long users;
    private final long activeUsers;
    private final long expenses;
    private final long creditCards;
    private final BigDecimal currentMonthExpense;
}
