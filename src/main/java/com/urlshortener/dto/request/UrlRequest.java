package com.urlshortener.dto.request;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class UrlRequest {
    @NotBlank(message = "Original URL is required.")
    private String originalUrl;

    private ZonedDateTime expireDate;
}
