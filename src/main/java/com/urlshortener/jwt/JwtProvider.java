package com.urlshortener.jwt;

import java.util.Date;
import java.security.Key;
import java.nio.charset.StandardCharsets;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
public class JwtProvider {

    private final Key key;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    // Constructor that initializes the secret key and token validity durations.
    public JwtProvider(
        @Value("${jwt.secret-key}") String secretKey,
        @Value("${jwt.access-token-validity}") long accessTokenValidity,
        @Value("${jwt.refresh-token-validity}") long refreshTokenValidity
    ) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    // Generate JWT access token.
    public String generateAccessToken(String email) {
        return generateToken(email, accessTokenValidity);
    }

    // Generate JWT refresh token.
    public String generateRefreshToken(String email) {
        return generateToken(email, refreshTokenValidity);
    }

    // Common method to generate JWT tokens.
    private String generateToken(String email, long validity) {
        Date date = new Date();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(email)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + validity))
                .signWith(key)
                .compact();
    }
}
