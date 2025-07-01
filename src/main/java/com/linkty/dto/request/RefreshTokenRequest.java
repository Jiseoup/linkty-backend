package com.linkty.dto.request;

import lombok.Getter;
import jakarta.validation.constraints.NotBlank;

@Getter
public class RefreshTokenRequest {
    @NotBlank(message = "Refresh Token is required.")
    private String refreshToken;
}
