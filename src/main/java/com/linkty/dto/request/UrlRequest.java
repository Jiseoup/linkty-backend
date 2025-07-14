package com.linkty.dto.request;

import java.time.ZonedDateTime;

import lombok.Getter;
import jakarta.validation.constraints.NotBlank;

import com.linkty.exception.ValidationErrorCode;

@Getter
public class UrlRequest {
    private String alias;

    @NotBlank(message = ValidationErrorCode.REQUIRED)
    private String originalUrl;

    private ZonedDateTime activeDate;

    private ZonedDateTime expireDate;
}
