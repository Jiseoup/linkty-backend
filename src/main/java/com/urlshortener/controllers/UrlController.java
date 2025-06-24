package com.urlshortener.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.urlshortener.dto.request.UrlRequest;
import com.urlshortener.dto.response.UrlResponse;
import com.urlshortener.services.UrlService;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    // Creates a shorten url with the original url provided in the request.
    @PostMapping("/shorten")
    public ResponseEntity<UrlResponse> shorten(@RequestBody UrlRequest request) {
        UrlResponse response = urlService.createShortenUrl(request);
        return ResponseEntity.ok(response);
    }

    // Redirects to the original url using the shorten url.
    @GetMapping("/{shortenUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortenUrl) {
        String originalUrl = urlService.retrieveOriginalUrl(shortenUrl);
        return ResponseEntity.status(302).header("Location", originalUrl).build();
    }
}
