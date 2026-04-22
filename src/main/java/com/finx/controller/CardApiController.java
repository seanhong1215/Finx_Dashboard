package com.finx.controller;

import com.finx.dto.request.CardRequest;
import com.finx.dto.response.ApiResponse;
import com.finx.model.Card;
import com.finx.security.CustomUserDetails;
import com.finx.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cards", description = "Credit card management API")
@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardApiController {

    private final CardService cardService;

    @Operation(summary = "Add a new credit card")
    @PostMapping
    public ResponseEntity<ApiResponse<Card>> create(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody CardRequest request) {
        Card card = cardService.create(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(card));
    }

    @Operation(summary = "Delete a credit card")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long id) {
        cardService.delete(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Card deleted"));
    }
}
