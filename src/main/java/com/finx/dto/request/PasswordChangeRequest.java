package com.finx.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class PasswordChangeRequest {
    @NotBlank
    private String currentPassword;

    @NotBlank
    @Size(min = 8, max = 72)
    private String newPassword;
}
