// @formatter:off
package com.linkty.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Global Error Codes.
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error has occurred."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Unknown validation error has occurred.");

    private final HttpStatus status;
    private final String message;
}