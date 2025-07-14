package com.linkty.dto.request;

import lombok.Getter;
import jakarta.validation.constraints.NotBlank;

import com.linkty.exception.ValidationErrorCode;

@Getter
public class RefreshTokenRequest {
    @NotBlank(message = ValidationErrorCode.REQUIRED)
    private String refreshToken;
}
