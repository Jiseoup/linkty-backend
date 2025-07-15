package com.linkty.dto.request;

import java.time.ZonedDateTime;

import lombok.Getter;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

import com.linkty.exception.ValidationErrorCode;

@Getter
public class UrlRequest {
    @Size(message = ValidationErrorCode.LENGTH_UNDERFLOW, min = 2)
    @Size(message = ValidationErrorCode.LENGTH_OVERFLOW, max = 20)
    private String alias;

    @NotBlank(message = ValidationErrorCode.REQUIRED)
    private String originalUrl;

    private ZonedDateTime activeDate;

    private ZonedDateTime expireDate;
}
