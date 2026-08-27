package com.finx.service;

import com.finx.dto.response.AuthResponse;
import com.finx.dto.response.UserResponse;
import com.finx.exception.BusinessException;
import com.finx.model.RefreshToken;
import com.finx.model.User;
import com.finx.repository.RefreshTokenRepository;
import com.finx.repository.UserRepository;
import com.finx.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.jwt.refresh-token-days}")
    private long refreshTokenDays;

    @Transactional
    public LoginResult login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Invalid username or password"));
        if (!Boolean.TRUE.equals(user.getIsActive()) || !passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("Invalid username or password");
        }
        refreshTokenRepository.deleteByUserId(user.getId());
        RefreshToken refreshToken = createRefreshToken(user);
        return new LoginResult(new AuthResponse(jwtService.generateAccessToken(user), "Bearer", UserResponse.from(user)),
                refreshToken.getToken());
    }

    @Transactional
    public LoginResult refresh(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new BusinessException("Refresh token is invalid");
        }
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Refresh token is invalid"));
        if (refreshToken.isExpired() || !Boolean.TRUE.equals(refreshToken.getUser().getIsActive())) {
            refreshTokenRepository.delete(refreshToken);
            throw new BusinessException("Refresh token is invalid");
        }
        User user = refreshToken.getUser();
        refreshTokenRepository.delete(refreshToken);
        RefreshToken next = createRefreshToken(user);
        return new LoginResult(new AuthResponse(jwtService.generateAccessToken(user), "Bearer", UserResponse.from(user)),
                next.getToken());
    }

    @Transactional
    public void logout(String token) {
        if (token != null && !token.trim().isEmpty()) {
            refreshTokenRepository.deleteByToken(token);
        }
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiresAt(LocalDateTime.now().plusDays(refreshTokenDays))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public static class LoginResult {
        private final AuthResponse authResponse;
        private final String refreshToken;

        public LoginResult(AuthResponse authResponse, String refreshToken) {
            this.authResponse = authResponse;
            this.refreshToken = refreshToken;
        }

        public AuthResponse getAuthResponse() {
            return authResponse;
        }

        public String getRefreshToken() {
            return refreshToken;
        }
    }
}
