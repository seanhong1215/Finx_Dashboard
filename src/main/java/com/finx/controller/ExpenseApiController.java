package com.finx.controller;

import com.finx.dto.request.ExpenseRequest;
import com.finx.dto.response.ApiResponse;
import com.finx.dto.response.ExpenseResponse;
import com.finx.security.CustomUserDetails;
import com.finx.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseApiController {

    private final ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> list(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long creditCardId) {
        return ResponseEntity.ok(ApiResponse.success(
                expenseService.search(currentUser.getId(), from, to, category, creditCardId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> create(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(expenseService.create(currentUser.getId(), request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> update(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(ApiResponse.success(expenseService.update(currentUser.getId(), id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long id) {
        expenseService.delete(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Expense deleted"));
    }
}
