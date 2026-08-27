package com.finx.controller;

import com.finx.dto.request.PasswordChangeRequest;
import com.finx.dto.request.ProfileUpdateRequest;
import com.finx.dto.response.ApiResponse;
import com.finx.dto.response.UserResponse;
import com.finx.security.CustomUserDetails;
import com.finx.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(ApiResponse.success(UserResponse.from(currentUser.getUser())));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody ProfileUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(UserResponse.from(
                userService.updateProfile(currentUser.getId(), request.getFullName(), request.getEmail()))));
    }

    @PostMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(currentUser.getId(), request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success("Password changed"));
    }
}
