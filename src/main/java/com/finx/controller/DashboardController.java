package com.finx.controller;

import com.finx.security.CustomUserDetails;
import com.finx.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(@AuthenticationPrincipal CustomUserDetails currentUser,
                            Model model) {
        Long userId = currentUser.getId();
        Long accountId = userId; // account ID mirrors user ID in seed data; service resolves the real one

        DashboardService.DashboardData data =
                dashboardService.buildDashboard(accountId, currentUser.getUser());

        model.addAttribute("user",         data.userSummary);
        model.addAttribute("balance",      data.balanceSummary);
        model.addAttribute("transactions", data.recentTransactions);
        model.addAttribute("chart",        data.chartData);
        model.addAttribute("stats",        data.quickStats);

        return "dashboard/index";
    }
}
