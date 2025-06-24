package com.urlshortener.services;

import java.util.UUID;
import java.time.ZonedDateTime;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.urlshortener.entities.Url;
import com.urlshortener.repositories.UrlRepository;
import com.urlshortener.dto.request.UrlRequest;
import com.urlshortener.dto.response.UrlResponse;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;

    // Creates a new shorten url based on the original url.
    public UrlResponse createShortenUrl(UrlRequest request) {
        // Creates an 8-digit uuid for shorten url.
        String uuid = UUID.randomUUID().toString().substring(0, 8);

        // Build and save the Url entity.
        Url url = Url.builder()
                .originalUrl(request.getOriginalUrl())
                .shortenUrl(uuid)
                .expireDate(request.getExpireDate())
                .build();
        urlRepository.save(url);

        return new UrlResponse(uuid, request.getExpireDate());
    }

    // Retrieves the original url associated with a given shorten url.
    public String retrieveOriginalUrl(String shortenUrl) {
        // Retrieve the Url entity by shorten url.
        Url url = urlRepository.findByShortenUrl(shortenUrl)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "This URL does not exist."));

        // Check if the url has expired.
        ZonedDateTime expireDate = url.getExpireDate();
        if (expireDate != null && expireDate.isBefore(ZonedDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.GONE, "This URL has expired.");
        }

        // Increase the click count and save the entity.
        url.increaseClickCount();
        urlRepository.save(url);

        return url.getOriginalUrl();
    }
}
