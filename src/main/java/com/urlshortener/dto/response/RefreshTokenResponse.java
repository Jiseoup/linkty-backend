package com.urlshortener.dto.response;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class RefreshTokenResponse {
    private String accessToken;
}
