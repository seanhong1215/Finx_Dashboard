package com.finx.dto.request;

import com.finx.model.User;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CreateUserRequest {
    @NotBlank
    @Size(max = 50)
    private String username;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    @NotBlank
    @Size(max = 100)
    private String fullName;

    @NotBlank
    @Size(min = 8, max = 72)
    private String temporaryPassword;

    @NotNull
    private User.Role role;
}
