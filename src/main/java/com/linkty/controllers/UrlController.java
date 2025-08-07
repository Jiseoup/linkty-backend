package com.linkty.controllers;

import java.time.ZonedDateTime;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
        String alias = request.getAlias();
        String originalUrl = request.getOriginalUrl();
        ZonedDateTime activeDate = request.getActiveDate();
        ZonedDateTime expireDate = request.getExpireDate();

        UrlResponse response = urlService.createShortenUrl(alias, originalUrl,
                activeDate, expireDate, authToken);
        return ResponseEntity.ok(response);
    }

    // Redirects to the original url using the shorten url.
    @GetMapping("/{shortenUrl:[a-zA-Z0-9]{6}}")
    // @GetMapping("/{shortenUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortenUrl) {
        String originalUrl = urlService.retrieveOriginalUrl(shortenUrl);

        return ResponseEntity.status(302).header("Location", originalUrl)
                .build();
    }
}
