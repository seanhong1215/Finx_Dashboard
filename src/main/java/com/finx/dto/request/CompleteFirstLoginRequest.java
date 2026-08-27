package com.finx.dto.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CompleteFirstLoginRequest {
    @NotBlank
    private String currentPassword;

    @NotBlank
    @Size(min = 8, max = 72)
    private String newPassword;

    @NotBlank
    @Size(max = 100)
    private String fullName;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;
}
