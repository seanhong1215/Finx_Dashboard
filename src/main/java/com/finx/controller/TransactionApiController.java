package com.finx.controller;

import com.finx.dto.request.TransactionRequest;
import com.finx.dto.response.ApiResponse;
import com.finx.model.Transaction;
import com.finx.security.CustomUserDetails;
import com.finx.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Transactions", description = "Transaction management API")
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionApiController {

    private final TransactionService transactionService;

    @Operation(summary = "Create a new transaction")
    @PostMapping
    public ResponseEntity<ApiResponse<Transaction>> create(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody TransactionRequest request) {
        Transaction tx = transactionService.create(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(tx));
    }

    @Operation(summary = "Delete a transaction")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long id) {
        transactionService.delete(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Transaction deleted"));
    }
}
