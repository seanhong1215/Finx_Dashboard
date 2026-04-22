package com.finx.service;

import com.finx.model.Transaction;
import com.finx.model.User;
import com.finx.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;

    // ── DTO 類別（Java 8 相容，改用一般 class）────────────────────────────

    public static class UserSummary {
        public final String fullName;
        public final String email;
        public final String avatarInitials;
        public UserSummary(String fullName, String email, String avatarInitials) {
            this.fullName = fullName;
            this.email = email;
            this.avatarInitials = avatarInitials;
        }
    }

    public static class BalanceSummary {
        public final BigDecimal totalBalance;
        public final BigDecimal monthlyIncome;
        public final BigDecimal monthlyExpense;
        public final BigDecimal savingsRate;
        public final String maskedCard;
        public final String cardExpiry;
        public BalanceSummary(BigDecimal totalBalance, BigDecimal monthlyIncome,
                              BigDecimal monthlyExpense, BigDecimal savingsRate,
                              String maskedCard, String cardExpiry) {
            this.totalBalance = totalBalance;
            this.monthlyIncome = monthlyIncome;
            this.monthlyExpense = monthlyExpense;
            this.savingsRate = savingsRate;
            this.maskedCard = maskedCard;
            this.cardExpiry = cardExpiry;
        }
    }

    public static class MonthlyChart {
        public final String month;
        public final long income;
        public final long expense;
        public MonthlyChart(String month, long income, long expense) {
            this.month = month;
            this.income = income;
            this.expense = expense;
        }
    }

    public static class QuickStat {
        public final String label;
        public final String value;
        public final String change;
        public final boolean positive;
        public final String icon;
        public QuickStat(String label, String value, String change, boolean positive, String icon) {
            this.label = label;
            this.value = value;
            this.change = change;
            this.positive = positive;
            this.icon = icon;
        }
    }

    public static class DashboardData {
        public final UserSummary userSummary;
        public final BalanceSummary balanceSummary;
        public final List<Transaction> recentTransactions;
        public final List<MonthlyChart> chartData;
        public final List<QuickStat> quickStats;
        public DashboardData(UserSummary userSummary, BalanceSummary balanceSummary,
                             List<Transaction> recentTransactions,
                             List<MonthlyChart> chartData, List<QuickStat> quickStats) {
            this.userSummary = userSummary;
            this.balanceSummary = balanceSummary;
            this.recentTransactions = recentTransactions;
            this.chartData = chartData;
            this.quickStats = quickStats;
        }
    }

    // ── 組合 Dashboard 資料 ───────────────────────────────────────────────────

    public DashboardData buildDashboard(Long accountId, User user) {
        LocalDate now = LocalDate.now();
        int year  = now.getYear();
        int month = now.getMonthValue();

        BigDecimal income  = transactionRepository.sumIncomeByMonth(accountId, year, month);
        BigDecimal expense = transactionRepository.sumExpenseByMonth(accountId, year, month);

        BigDecimal savingsRate = BigDecimal.ZERO;
        if (income.compareTo(BigDecimal.ZERO) > 0) {
            savingsRate = income.subtract(expense)
                .divide(income, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(1, RoundingMode.HALF_UP);
        }

        List<Transaction> recent = transactionRepository
            .findTop10ByAccountIdOrderByTransactedAtDesc(accountId);

        List<MonthlyChart> chart = Arrays.asList(
            new MonthlyChart("Oct", 80000, 38000),
            new MonthlyChart("Nov", 80000, 42000),
            new MonthlyChart("Dec", 80000, 55000),
            new MonthlyChart("Jan", 80000, 36000),
            new MonthlyChart("Feb", 85000, 41000),
            new MonthlyChart("Mar", income.longValue(), expense.longValue())
        );

        List<QuickStat> stats = Arrays.asList(
            new QuickStat("本月收入", "NTD " + fmt(income),  "+5.2%", true,  "💼"),
            new QuickStat("本月支出", "NTD " + fmt(expense), "-12%",  true,  "💳"),
            new QuickStat("儲蓄率",   savingsRate + "%",      "+3%",   true,  "🏦"),
            new QuickStat("投資報酬", "NTD 9,874",           "+1.9%", true,  "📈")
        );

        UserSummary userSummary = new UserSummary(
            user.getFullName(), user.getEmail(), initials(user.getFullName())
        );

        BalanceSummary balance = new BalanceSummary(
            new BigDecimal("156500"), income, expense, savingsRate,
            "**** **** **** 4521", "08/27"
        );

        return new DashboardData(userSummary, balance, recent, chart, stats);
    }

    private String fmt(BigDecimal v) {
        return String.format("%,.0f", v);
    }

    private String initials(String name) {
        if (name == null || name.trim().isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }
}
