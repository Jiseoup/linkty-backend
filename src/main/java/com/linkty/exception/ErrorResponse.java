package com.linkty.exception;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final String code;
    private final int status;
    private final String error;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String field;

    @JsonIgnore
    private final String message;

    @JsonIgnore
    private final String path;

    @JsonIgnore
    private final LocalDateTime timestamp;
}
