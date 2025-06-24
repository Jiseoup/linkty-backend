package com.urlshortener.dto.request;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UrlRequest {
    private String originalUrl;
    private ZonedDateTime expireDate;
}
