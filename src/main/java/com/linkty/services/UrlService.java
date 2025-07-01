package com.linkty.services;

import java.util.UUID;
import java.time.ZonedDateTime;

import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.linkty.dto.response.UrlResponse;
import com.linkty.entities.Url;
import com.linkty.repositories.UrlRepository;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;

    // Creates a new shorten url based on the original url.
    @Transactional
    public UrlResponse createShortenUrl(String originalUrl, ZonedDateTime expireDate) {
        // Creates an 8-digit uuid for shorten url.
        String uuid = UUID.randomUUID().toString().substring(0, 8);

        // Build and save the Url entity.
        Url url = Url.builder()
                .originalUrl(originalUrl)
                .shortenUrl(uuid)
                .expireDate(expireDate)
                .build();
        urlRepository.save(url);

        return new UrlResponse(uuid, expireDate);
    }

    // Retrieves the original url associated with a given shorten url.
    @Transactional
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

        // Increase the click count.
        url.increaseClickCount();

        return url.getOriginalUrl();
    }
}
