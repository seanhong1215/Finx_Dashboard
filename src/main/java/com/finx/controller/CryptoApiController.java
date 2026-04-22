package com.finx.controller;

import com.finx.dto.request.CryptoRequest;
import com.finx.dto.response.ApiResponse;
import com.finx.model.CryptoAsset;
import com.finx.security.CustomUserDetails;
import com.finx.service.CryptoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Crypto", description = "Cryptocurrency portfolio management API")
@RestController
@RequestMapping("/api/crypto")
@RequiredArgsConstructor
public class CryptoApiController {

    private final CryptoService cryptoService;

    @Operation(summary = "Add a new crypto asset")
    @PostMapping
    public ResponseEntity<ApiResponse<CryptoAsset>> create(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody CryptoRequest request) {
        CryptoAsset asset = cryptoService.create(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(asset));
    }

    @Operation(summary = "Update a crypto asset")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CryptoAsset>> update(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long id,
            @Valid @RequestBody CryptoRequest request) {
        CryptoAsset asset = cryptoService.update(currentUser.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success(asset));
    }

    @Operation(summary = "Delete a crypto asset")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long id) {
        cryptoService.delete(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Crypto asset deleted"));
    }
}
