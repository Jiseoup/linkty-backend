package com.linkty.services;

import java.time.ZonedDateTime;

import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.linkty.utils.UrlValidator;
import com.linkty.utils.CodeGenerator;
import com.linkty.exception.CustomException;
import com.linkty.exception.ErrorCode;
import com.linkty.entities.postgresql.Url;
import com.linkty.entities.postgresql.User;
import com.linkty.dto.response.UrlResponse;
import com.linkty.repositories.UrlRepository;
import com.linkty.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final UserRepository userRepository;

    // Creates a new shorten url based on the original url.
    @Transactional
    public UrlResponse createShortenUrl(String alias, String originalUrl,
            ZonedDateTime activeDate, ZonedDateTime expireDate, Long userId) {
        // Validate the original url.
        UrlValidator.validate(originalUrl);

        // Check if unauthorized user tries to use advanced settings.
        if ((alias != null || activeDate != null || expireDate != null)
                && userId == null) {
            throw new CustomException(ErrorCode.ADVANCED_SETTINGS_UNAUTHORIZED);
        }

        // Get User entity if userId request exists.
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElseThrow(
                    () -> new CustomException(ErrorCode.USER_NOT_FOUND));
        }

        // Remove whitespaces from the alias.
        String cleanAlias = alias != null ? alias.strip() : null;
        if (cleanAlias != null && cleanAlias.isEmpty()) {
            cleanAlias = null;
        }

        // Creates an 6-character shorten url.
        String shortenUrl = CodeGenerator.generateAlphanumeric(6);

        // Build and save the Url entity.
        Url url = Url.builder().alias(cleanAlias).originalUrl(originalUrl)
                .shortenUrl(shortenUrl).activeDate(activeDate)
                .expireDate(expireDate).user(user).build();
        urlRepository.save(url);

        return new UrlResponse(shortenUrl, cleanAlias, activeDate, expireDate);
    }

    // Retrieves the original url associated with a given shorten url.
    @Transactional
    public String retrieveOriginalUrl(String shortenUrl) {
        // Retrieve the Url entity by shorten url.
        Url url = urlRepository.findByShortenUrl(shortenUrl).orElseThrow(
                () -> new CustomException(ErrorCode.URL_NOT_FOUND));

        // Check if the url has activated.
        ZonedDateTime activeDate = url.getActiveDate();
        if (activeDate != null && activeDate.isAfter(ZonedDateTime.now())) {
            throw new CustomException(ErrorCode.URL_NOT_ACTIVATED);
        }

        // Check if the url has expired.
        ZonedDateTime expireDate = url.getExpireDate();
        if (expireDate != null && expireDate.isBefore(ZonedDateTime.now())) {
            throw new CustomException(ErrorCode.URL_EXPIRED);
        }

        // Increase the click count.
        url.increaseClickCount();

        return url.getOriginalUrl();
    }
}
