package com.linkty.dto.request;

import lombok.Getter;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Getter
public class EmailVerificationConfirmRequest {
    @Email(message = "Must be valid email format.")
    @NotBlank(message = "Email is required.")
    private String email;

    @Size(min = 6, max = 6)
    @NotBlank(message = "Code is required.")
    private String code;
}
