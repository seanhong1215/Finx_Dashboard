package com.finx.controller;

import com.finx.dto.response.ApiResponse;
import com.finx.dto.response.DashboardResponse;
import com.finx.security.CustomUserDetails;
import com.finx.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardApiController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> get(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                               @RequestParam(required = false) String month) {
        YearMonth selectedMonth = month == null || month.isBlank() ? YearMonth.now() : YearMonth.parse(month);
        return ResponseEntity.ok(ApiResponse.success(dashboardService.build(currentUser.getId(), selectedMonth)));
    }
}
