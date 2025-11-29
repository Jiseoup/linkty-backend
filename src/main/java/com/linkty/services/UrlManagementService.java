package com.linkty.services;

import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.linkty.jwt.JwtProvider;
import com.linkty.exception.CustomException;
import com.linkty.exception.ErrorCode;
import com.linkty.entities.postgresql.Url;
import com.linkty.dto.response.MessageResponse;
import com.linkty.repositories.UrlRepository;

@Service
@RequiredArgsConstructor
public class UrlManagementService {

    private final JwtProvider jwtProvider;
    private final UrlRepository urlRepository;

    // Toggle URL active status.
    @Transactional
    public MessageResponse toggleUrlActive(String authToken, Long urlId) {
        // Get user id from the provided authToken.
        Long userId = jwtProvider.getClaimsFromBearerToken(authToken)
                .get("userId", Long.class);

        // Retrieve the Url entity by urlId and userId.
        Url url = urlRepository.findByIdAndUserId(urlId, userId).orElseThrow(
                () -> new CustomException(ErrorCode.URL_NOT_FOUND));

        // Toggle the URL active status.
        url.toggleActive();

        return new MessageResponse("URL active status toggled successfully.");
    }
}
