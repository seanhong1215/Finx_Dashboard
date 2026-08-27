package com.finx.service;

import com.finx.exception.ResourceNotFoundException;
import com.finx.model.User;
import com.finx.repository.UserRepository;
import com.finx.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        log.debug("Loaded user: {}", username);
        return new CustomUserDetails(user);
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    @Transactional(readOnly = true)
    public User findActiveById(Long id) {
        User user = findById(id);
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new ResourceNotFoundException("User", id);
        }
        return user;
    }

    @Transactional
    public User updateProfile(Long userId, String fullName, String email) {
        User user = findActiveById(userId);
        userRepository.findByEmail(email)
                .filter(existing -> !existing.getId().equals(userId))
                .ifPresent(existing -> {
                    throw new com.finx.exception.BusinessException("Email already exists");
                });
        user.setFullName(fullName);
        user.setEmail(email);
        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = findActiveById(userId);
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new com.finx.exception.BusinessException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setMustChangePassword(false);
        userRepository.save(user);
    }

    @Transactional
    public User completeFirstLogin(Long userId, String currentPassword, String newPassword, String fullName, String email) {
        User user = findActiveById(userId);
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new com.finx.exception.BusinessException("Current password is incorrect");
        }
        userRepository.findByEmail(email)
                .filter(existing -> !existing.getId().equals(userId))
                .ifPresent(existing -> {
                    throw new com.finx.exception.BusinessException("Email already exists");
                });
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setMustChangePassword(false);
        return userRepository.save(user);
    }
}
