package com.linkty.dto.request;

import lombok.Getter;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;

import com.linkty.exception.ValidationErrorCode;

@Getter
public class ChangePasswordRequest {
    @NotBlank(message = ValidationErrorCode.REQUIRED)
    private String currentPassword;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()]{8,16}$",
            message = ValidationErrorCode.INVALID_FORMAT)
    @NotBlank(message = ValidationErrorCode.REQUIRED)
    private String newPassword;
}
