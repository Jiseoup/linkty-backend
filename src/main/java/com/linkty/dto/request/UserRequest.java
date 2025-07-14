package com.linkty.dto.request;

import lombok.Getter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import com.linkty.exception.ValidationErrorCode;

@Getter
public class UserRequest {
    @Email(message = ValidationErrorCode.INVALID_FORMAT)
    @NotBlank(message = ValidationErrorCode.REQUIRED)
    private String email;

    @NotBlank(message = ValidationErrorCode.REQUIRED)
    private String password;
}
