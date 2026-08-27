package com.finx.dto.request;

import com.finx.model.User;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RoleUpdateRequest {
    @NotNull
    private User.Role role;
}
