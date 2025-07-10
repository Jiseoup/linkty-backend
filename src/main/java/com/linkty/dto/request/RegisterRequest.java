package com.linkty.dto.request;

import lombok.Getter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Getter
public class RegisterRequest {
    @Email(message = "Must be valid email format.")
    @NotBlank(message = "Email is required.")
    private String email;

    @NotBlank(message = "Password is required.")
    private String password;

    @NotBlank(message = "Captcha token is required.")
    private String captchaToken;
}
