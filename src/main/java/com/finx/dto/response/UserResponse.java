package com.finx.dto.response;

import com.finx.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private final Long id;
    private final String username;
    private final String email;
    private final String fullName;
    private final User.Role role;
    private final Boolean active;
    private final Boolean mustChangePassword;

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.getIsActive(),
                user.getMustChangePassword()
        );
    }
}
