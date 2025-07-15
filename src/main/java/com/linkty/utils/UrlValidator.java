package com.linkty.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.MalformedURLException;

import com.linkty.exception.CustomException;
import com.linkty.exception.ErrorCode;

public class UrlValidator {
    public static void validate(String url) {
        // Check if the URL starts with a valid scheme. (http or https)
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new CustomException(ErrorCode.INVALID_URL_SCHEME);
        }

        try {
            // Check if the URI is valid.
            URI uri = new URI(url);
            uri.toURL();

            // Check if the host is valid.
            String host = uri.getHost();
            if (host == null || !host.contains(".")) {
                throw new CustomException(ErrorCode.INVALID_URL_FORMAT);
            }
        } catch (URISyntaxException | MalformedURLException e) {
            throw new CustomException(ErrorCode.INVALID_URL_FORMAT);
        }
    }
}
