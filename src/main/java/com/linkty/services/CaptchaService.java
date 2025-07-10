package com.linkty.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;

import com.linkty.dto.response.CaptchaResponse;

@Service
public class CaptchaService {

    @Value("${google.captcha.secret-key}")
    private String captchaSecretKey;

    private static final String CAPTCHA_VERIFY_URL =
            "https://www.google.com/recaptcha/api/siteverify";

    // Verify the captcha token.
    public void verifyToken(String token) {
        RestTemplate restTemplate = new RestTemplate();

        // Set http headers.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Set http body parameters.
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", captchaSecretKey);
        params.add("response", token);

        // Create an HttpEntity with headers and body parameters.
        HttpEntity<MultiValueMap<String, String>> requestEntity =
                new HttpEntity<>(params, headers);

        // Send a POST request to the captcha verify url.
        CaptchaResponse response = restTemplate.postForObject(
                CAPTCHA_VERIFY_URL, requestEntity, CaptchaResponse.class);

        // Check if the captcha verification is successful.
        if (response == null || !response.isSuccess()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "reCAPTCHA verification failed.");
        }
    }
}
