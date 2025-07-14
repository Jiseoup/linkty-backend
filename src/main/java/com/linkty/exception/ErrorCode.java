// @formatter:off
package com.linkty.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Unexpected Error Codes.
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error has occurred."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Unknown validation error has occurred."),

    // Common Error Codes.
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Token is invalid or has been expired."),

    // Url Service Error Codes.
    URL_NOT_FOUND(HttpStatus.NOT_FOUND, "This URL does not exist."),
    URL_NOT_ACTIVATED(HttpStatus.FORBIDDEN, "This URL has not activated yet."),
    URL_EXPIRED(HttpStatus.GONE, "This URL has expired."),

    // User Service Error Codes.
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "This User does not exist."),
    EMAIL_CONFLICTED(HttpStatus.CONFLICT, "This email is already in use."),
    INVALID_EMAIL_OR_PASSWORD(HttpStatus.UNAUTHORIZED, "Invalid email or password."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "Invalid password."),

    // Email Service Error Codes.
    SEND_EMAIL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send email."),
    INVALID_CODE(HttpStatus.BAD_REQUEST, "Code is invalid or has been expired."),

    // Captcha Service Error Codes.
    CAPTCHA_VERIFICATION_FAILED(HttpStatus.FORBIDDEN, "Failed to verify the captcha.");

    private final HttpStatus status;
    private final String message;
}
