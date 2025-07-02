package com.linkty.dto.request;

import java.time.ZonedDateTime;

import lombok.Getter;
import jakarta.validation.constraints.NotBlank;

@Getter
public class UrlRequest {
    private String alias;

    @NotBlank(message = "Original URL is required.")
    private String originalUrl;

    private ZonedDateTime activeDate;

    private ZonedDateTime expireDate;
}
