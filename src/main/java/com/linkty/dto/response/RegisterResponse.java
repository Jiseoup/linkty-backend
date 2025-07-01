package com.linkty.dto.response;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class RegisterResponse {
    private String email;
    private ZonedDateTime joinDate;
}
