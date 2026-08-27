package com.finx.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PaymentStatusRequest {
    @NotNull
    private Boolean paid;
}
