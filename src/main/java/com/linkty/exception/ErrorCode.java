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
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Invalid request."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Token is invalid or has been expired."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "This User does not exist."),
    EMAIL_CONFLICTED(HttpStatus.CONFLICT, "This email is already in use."),

    // Url Service Error Codes.
    URL_NOT_FOUND(HttpStatus.NOT_FOUND, "This URL does not exist."),
    INVALID_URL_SCHEME(HttpStatus.BAD_REQUEST, "URL must start with http:// or https://."),
    INVALID_URL_FORMAT(HttpStatus.BAD_REQUEST, "Invalid URL format."),
    URL_NOT_ACTIVATED(HttpStatus.FORBIDDEN, "This URL has not activated yet."),
    URL_EXPIRED(HttpStatus.GONE, "This URL has expired."),
    ADVANCED_SETTINGS_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Advanced settings are for members only."),

    // User Service Error Codes.
    INVALID_EMAIL_OR_PASSWORD(HttpStatus.UNAUTHORIZED, "Invalid email or password."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "Invalid password."),
    INVALID_CURRENT_PASSWORD(HttpStatus.UNAUTHORIZED, "Invalid current password."),
    RESET_PASSWORD_EXPIRED(HttpStatus.GONE, "Reset password URL has expired."),

    // Email Service Error Codes.
    SEND_EMAIL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send email."),
    INVALID_CODE(HttpStatus.BAD_REQUEST, "Code is invalid or has been expired."),

    // Captcha Service Error Codes.
    CAPTCHA_VERIFICATION_FAILED(HttpStatus.FORBIDDEN, "Failed to verify the captcha.");

    private final HttpStatus status;
    private final String message;
}
