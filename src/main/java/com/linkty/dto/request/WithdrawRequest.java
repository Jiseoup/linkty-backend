package com.linkty.dto.request;

import lombok.Getter;
import jakarta.validation.constraints.NotBlank;

import com.linkty.exception.ValidationErrorCode;

@Getter
public class WithdrawRequest {
    @NotBlank(message = ValidationErrorCode.REQUIRED)
    private String password;
}
