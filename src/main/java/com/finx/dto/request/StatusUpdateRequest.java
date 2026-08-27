package com.finx.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StatusUpdateRequest {
    @NotNull
    private Boolean active;
}
