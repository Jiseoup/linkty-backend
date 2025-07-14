package com.linkty.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public String getCode() {
        return errorCode.name();
    }

    public HttpStatus getStatus() {
        return errorCode.getStatus();
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}
