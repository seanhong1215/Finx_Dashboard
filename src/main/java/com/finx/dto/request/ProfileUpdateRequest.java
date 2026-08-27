package com.finx.dto.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ProfileUpdateRequest {
    @NotBlank
    @Size(max = 100)
    private String fullName;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;
}
