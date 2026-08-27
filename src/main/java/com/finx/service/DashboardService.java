package com.finx.service;

import com.finx.dto.response.DashboardResponse;
import com.finx.dto.response.ExpenseResponse;
import com.finx.model.CreditCard;
import com.finx.model.Expense;
import com.finx.repository.CreditCardRepository;
import com.finx.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ExpenseRepository expenseRepository;
    private final CreditCardRepository creditCardRepository;

    @Transactional(readOnly = true)
    public DashboardResponse build(Long userId, YearMonth selectedMonth) {
        LocalDate start = selectedMonth.atDay(1);
        LocalDate end = selectedMonth.atEndOfMonth();
        List<Expense> monthExpenses = expenseRepository.search(userId, start, end, null, null);
        BigDecimal monthTotal = expenseRepository.sumByPeriod(userId, start, end);
        Map<String, BigDecimal> categoryTotals = new LinkedHashMap<>();
        for (Expense expense : monthExpenses) {
            categoryTotals.put(expense.getCategory(),
                    categoryTotals.getOrDefault(expense.getCategory(), BigDecimal.ZERO).add(expense.getAmount()));
        }
        List<Expense> allExpenses = expenseRepository.findByUserIdOrderBySpentOnDescCreatedAtDesc(userId);
        YearMonth currentMonth = selectedMonth;
        Map<String, BigDecimal> monthlyTotals = new LinkedHashMap<>();
        for (int offset = 5; offset >= 0; offset--) {
            YearMonth month = currentMonth.minusMonths(offset);
            monthlyTotals.put(month.getYear() + "-" + String.format("%02d", month.getMonthValue()), BigDecimal.ZERO);
        }
        Map<String, BigDecimal> cardTotals = new LinkedHashMap<>();
        for (Expense expense : allExpenses) {
            YearMonth expenseMonth = YearMonth.from(expense.getSpentOn());
            if (monthlyTotals.containsKey(expenseMonth.toString())) {
                monthlyTotals.put(expenseMonth.toString(), monthlyTotals.get(expenseMonth.toString()).add(expense.getAmount()));
            }
            if (expense.getCreditCard() != null && expenseMonth.equals(currentMonth)) {
                CreditCard card = expense.getCreditCard();
                String label = card.getBankName() + " " + card.getCardName();
                cardTotals.put(label, cardTotals.getOrDefault(label, BigDecimal.ZERO).add(expense.getAmount()));
            }
        }
        BigDecimal unpaid = BigDecimal.ZERO;
        for (CreditCard card : creditCardRepository.findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(userId)) {
            if (!Boolean.TRUE.equals(card.getCurrentCyclePaid())) {
                unpaid = unpaid.add(expenseRepository.sumByCardAndPeriod(card.getId(), start, end));
            }
        }
        List<ExpenseResponse> recent = expenseRepository.findTop8ByUserIdOrderBySpentOnDescCreatedAtDesc(userId)
                .stream()
                .map(ExpenseResponse::from)
                .collect(Collectors.toList());
        BigDecimal previousMonthExpense = expenseRepository.sumByPeriod(userId,
                selectedMonth.minusMonths(1).atDay(1), selectedMonth.minusMonths(1).atEndOfMonth());
        return new DashboardResponse(monthTotal, unpaid, expenseRepository.countByUserId(userId),
                creditCardRepository.countByUserIdAndIsActiveTrue(userId), categoryTotals, recent, monthlyTotals, cardTotals,
                selectedMonth.toString(), previousMonthExpense);
    }
}
