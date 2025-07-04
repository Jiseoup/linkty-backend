package com.linkty.services;

import java.time.ZonedDateTime;

import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.linkty.utils.CodeGenerator;
import com.linkty.dto.response.UrlResponse;
import com.linkty.entities.Url;
import com.linkty.repositories.UrlRepository;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;

    // Creates a new shorten url based on the original url.
    @Transactional
    public UrlResponse createShortenUrl(String alias, String originalUrl, ZonedDateTime activeDate, ZonedDateTime expireDate) {
        // Creates an 6-character shorten url.
        String shortenUrl = CodeGenerator.generate(6);

        // Build and save the Url entity.
        Url url = Url.builder()
                .alias(alias)
                .originalUrl(originalUrl)
                .shortenUrl(shortenUrl)
                .activeDate(activeDate)
                .expireDate(expireDate)
                .build();
        urlRepository.save(url);

        return new UrlResponse(shortenUrl, activeDate, expireDate);
    }

    // Retrieves the original url associated with a given shorten url.
    @Transactional
    public String retrieveOriginalUrl(String shortenUrl) {
        // Retrieve the Url entity by shorten url.
        Url url = urlRepository.findByShortenUrl(shortenUrl)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "This URL does not exist."));

        // Check if the url has activated.
        ZonedDateTime activeDate = url.getActiveDate();
        if (activeDate != null && activeDate.isAfter(ZonedDateTime.now())) {
            // TODO: 추후 예외 처리 페이지로 이동하도록 처리 필요.
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This URL has not activated yet.");
        }

        // Check if the url has expired.
        ZonedDateTime expireDate = url.getExpireDate();
        if (expireDate != null && expireDate.isBefore(ZonedDateTime.now())) {
            // TODO: 추후 예외 처리 페이지로 이동하도록 처리 필요.
            throw new ResponseStatusException(HttpStatus.GONE, "This URL has expired.");
        }

        // Increase the click count.
        url.increaseClickCount();

        return url.getOriginalUrl();
    }
}
