package com.finx.controller;

import com.finx.dto.request.StockRequest;
import com.finx.dto.response.ApiResponse;
import com.finx.model.StockHolding;
import com.finx.security.CustomUserDetails;
import com.finx.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Stocks", description = "Stock portfolio management API")
@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockApiController {

    private final StockService stockService;

    @Operation(summary = "Add a new stock holding")
    @PostMapping
    public ResponseEntity<ApiResponse<StockHolding>> create(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody StockRequest request) {
        StockHolding holding = stockService.create(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(holding));
    }

    @Operation(summary = "Update a stock holding")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StockHolding>> update(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long id,
            @Valid @RequestBody StockRequest request) {
        StockHolding holding = stockService.update(currentUser.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success(holding));
    }

    @Operation(summary = "Delete a stock holding")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long id) {
        stockService.delete(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Stock holding deleted"));
    }
}
