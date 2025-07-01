package com.linkty.dto.response;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class UrlResponse {
    private String shortenUrl;
    private ZonedDateTime expireDate;
}
