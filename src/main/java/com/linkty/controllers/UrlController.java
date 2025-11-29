package com.linkty.controllers;

import java.net.URI;
import java.time.ZonedDateTime;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.linkty.dto.request.UrlRequest;
import com.linkty.dto.response.UrlResponse;
import com.linkty.services.UrlService;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    // Creates a shorten url with the original url provided in the request.
    @PostMapping("/shorten-url")
    public ResponseEntity<UrlResponse> shorten(
            @RequestBody @Valid UrlRequest request,
            @RequestHeader(value = "Authorization",
                    required = false) String authToken) {
        String originalUrl = request.getOriginalUrl();
        ZonedDateTime activeDate = request.getActiveDate();
        ZonedDateTime expireDate = request.getExpireDate();
        String alias = request.getAlias();
        boolean starred = request.isStarred();
        boolean nonMemberCreation = request.isNonMemberCreation();

        UrlResponse response =
                urlService.createShortenUrl(originalUrl, activeDate, expireDate,
                        alias, starred, nonMemberCreation, authToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Redirects to the original url using the shorten url.
    @GetMapping("/{shortenUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortenUrl) {
        String originalUrl = urlService.retrieveOriginalUrl(shortenUrl);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl)).build();
    }
}
