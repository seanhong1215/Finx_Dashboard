package com.finx.controller;

import com.finx.dto.request.CreditCardRequest;
import com.finx.dto.request.PaymentStatusRequest;
import com.finx.dto.response.ApiResponse;
import com.finx.dto.response.CreditCardResponse;
import com.finx.security.CustomUserDetails;
import com.finx.service.CreditCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/credit-cards")
@RequiredArgsConstructor
public class CreditCardApiController {

    private final CreditCardService creditCardService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CreditCardResponse>>> list(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(ApiResponse.success(creditCardService.findCards(currentUser.getId())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CreditCardResponse>> create(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody CreditCardRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(creditCardService.create(currentUser.getId(), request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CreditCardResponse>> update(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long id,
            @Valid @RequestBody CreditCardRequest request) {
        return ResponseEntity.ok(ApiResponse.success(creditCardService.update(currentUser.getId(), id, request)));
    }

    @PatchMapping("/{id}/payment-status")
    public ResponseEntity<ApiResponse<CreditCardResponse>> updatePaymentStatus(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long id,
            @Valid @RequestBody PaymentStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                creditCardService.updatePaymentStatus(currentUser.getId(), id, request.getPaid())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long id) {
        creditCardService.delete(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Credit card deleted"));
    }
}
