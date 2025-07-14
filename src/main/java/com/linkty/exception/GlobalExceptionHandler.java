package com.linkty.exception;

import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Custom Exception Handler.
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(
            CustomException exception, HttpServletRequest request) {
        // Creates an custom exception error response.
        ErrorResponse errorResponse = new ErrorResponse(exception.getCode(),
                exception.getStatus().value(),
                exception.getStatus().getReasonPhrase(), null,
                exception.getMessage(), request.getRequestURI(),
                LocalDateTime.now());

        return new ResponseEntity<>(errorResponse, exception.getStatus());
    }

    // Unknown Exception Handler.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknownException(
            Exception exception, HttpServletRequest request) {
        // Set the default error code for unknown exceptions.
        ErrorCode errorCode = ErrorCode.UNKNOWN_ERROR;

        // Creates an unknown exception error response.
        ErrorResponse errorResponse = new ErrorResponse(errorCode.name(),
                errorCode.getStatus().value(),
                errorCode.getStatus().getReasonPhrase(), null,
                errorCode.getMessage(), request.getRequestURI(),
                LocalDateTime.now());

        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }

    // Validation Exception Handler.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        String code;
        String field;
        String message;

        // Set the default error code for validation exceptions.
        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;

        // Get the field error from the validation exception.
        FieldError fieldError = exception.getBindingResult().getFieldErrors()
                .stream().findFirst().orElse(null);

        // Set variable values for create an error response.
        if (fieldError != null) {
            code = fieldError.getDefaultMessage();
            field = fieldError.getField();
            message = "%s validation error at %s field.".formatted(code, field);
        } else {
            code = errorCode.name();
            field = null;
            message = errorCode.getMessage();
        }

        // Creates a validation exception error response.
        ErrorResponse errorResponse =
                new ErrorResponse(code, errorCode.getStatus().value(),
                        errorCode.getStatus().getReasonPhrase(), field, message,
                        request.getRequestURI(), LocalDateTime.now());

        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }
}
