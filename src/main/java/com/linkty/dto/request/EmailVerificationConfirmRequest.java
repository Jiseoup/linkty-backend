package com.linkty.dto.request;

import lombok.Getter;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import com.linkty.exception.ValidationErrorCode;

@Getter
public class EmailVerificationConfirmRequest {
    @Email(message = ValidationErrorCode.INVALID_FORMAT)
    @NotBlank(message = ValidationErrorCode.REQUIRED)
    private String email;

    @Size(message = ValidationErrorCode.INVALID_LENGTH, min = 6, max = 6)
    @NotBlank(message = ValidationErrorCode.REQUIRED)
    private String code;
}
