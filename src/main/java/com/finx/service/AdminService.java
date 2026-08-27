package com.finx.service;

import com.finx.dto.request.CreateUserRequest;
import com.finx.dto.response.AdminSummaryResponse;
import com.finx.dto.response.UserResponse;
import com.finx.exception.BusinessException;
import com.finx.exception.ResourceNotFoundException;
import com.finx.model.User;
import com.finx.repository.CreditCardRepository;
import com.finx.repository.ExpenseRepository;
import com.finx.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final CreditCardRepository creditCardRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserResponse> findUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getTemporaryPassword()))
                .role(request.getRole())
                .isActive(true)
                .mustChangePassword(true)
                .build();
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateRole(Long userId, User.Role role) {
        User user = find(userId);
        user.setRole(role);
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateStatus(Long userId, boolean active) {
        User user = find(userId);
        user.setIsActive(active);
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public AdminSummaryResponse summary() {
        LocalDate now = LocalDate.now();
        LocalDate start = now.withDayOfMonth(1);
        LocalDate end = now.withDayOfMonth(now.lengthOfMonth());
        return new AdminSummaryResponse(
                userRepository.count(),
                userRepository.countByIsActiveTrue(),
                expenseRepository.count(),
                creditCardRepository.count(),
                expenseRepository.sumByPeriodForAll(start, end)
        );
    }

    private User find(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }
}
