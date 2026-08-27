package com.finx.controller;

import com.finx.dto.request.CompleteFirstLoginRequest;
import com.finx.dto.request.LoginRequest;
import com.finx.dto.response.ApiResponse;
import com.finx.dto.response.AuthResponse;
import com.finx.dto.response.UserResponse;
import com.finx.security.CustomUserDetails;
import com.finx.service.AuthService;
import com.finx.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String REFRESH_COOKIE = "finx_refresh";

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request,
                                                           HttpServletResponse response) {
        AuthService.LoginResult result = authService.login(request.getUsername(), request.getPassword());
        setRefreshCookie(response, result.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(result.getAuthResponse()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken,
            HttpServletResponse response) {
        AuthService.LoginResult result = authService.refresh(refreshToken);
        setRefreshCookie(response, result.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(result.getAuthResponse()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken,
            HttpServletResponse response) {
        authService.logout(refreshToken);
        clearRefreshCookie(response);
        return ResponseEntity.ok(ApiResponse.success("Logged out"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(ApiResponse.success(UserResponse.from(currentUser.getUser())));
    }

    @PostMapping("/complete-first-login")
    public ResponseEntity<ApiResponse<UserResponse>> completeFirstLogin(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody CompleteFirstLoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(UserResponse.from(userService.completeFirstLogin(
                currentUser.getId(),
                request.getCurrentPassword(),
                request.getNewPassword(),
                request.getFullName(),
                request.getEmail()
        ))));
    }

    private void setRefreshCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE, refreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}
