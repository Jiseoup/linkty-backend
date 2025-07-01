package com.linkty.dto.response;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class RefreshTokenResponse {
    private String accessToken;
}
