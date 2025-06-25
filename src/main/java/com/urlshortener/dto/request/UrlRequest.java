package com.urlshortener.dto.request;

import java.time.ZonedDateTime;

import lombok.Getter;
import jakarta.validation.constraints.NotBlank;

@Getter
public class UrlRequest {
    @NotBlank(message = "Original URL is required.")
    private String originalUrl;

    private ZonedDateTime expireDate;
}
