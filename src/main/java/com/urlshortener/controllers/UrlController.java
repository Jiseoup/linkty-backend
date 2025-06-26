package com.urlshortener.controllers;

import java.time.ZonedDateTime;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.urlshortener.services.UrlService;
import com.urlshortener.dto.request.UrlRequest;
import com.urlshortener.dto.response.UrlResponse;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    // Creates a shorten url with the original url provided in the request.
    @PostMapping("/shorten")
    public ResponseEntity<UrlResponse> shorten(@RequestBody @Valid UrlRequest request) {
        String originalUrl = request.getOriginalUrl();
        ZonedDateTime expireDate = request.getExpireDate();

        UrlResponse response = urlService.createShortenUrl(originalUrl, expireDate);
        return ResponseEntity.ok(response);
    }

    // Redirects to the original url using the shorten url.
    @GetMapping("/{shortenUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortenUrl) {
        String originalUrl = urlService.retrieveOriginalUrl(shortenUrl);
        return ResponseEntity.status(302).header("Location", originalUrl).build();
    }
}
