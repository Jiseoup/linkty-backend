package com.linkty.jwt;

import java.util.Date;
import java.security.Key;
import java.util.Collections;
import java.nio.charset.StandardCharsets;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Component
public class JwtProvider {

    private final Key key;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    // Constructor that initializes the secret key and token validity durations.
    public JwtProvider(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-token-validity}") long accessTokenValidity,
            @Value("${jwt.refresh-token-validity}") long refreshTokenValidity) {
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

    // Validate JWT token.
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Extract bearer token from the authorization header.
    public String resolveToken(String token) {
        // Validate and extract the token.
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    // Get authentication object from token.
    public Authentication getAuthentication(String token) {
        String email = getEmailFromToken(token);
        return new UsernamePasswordAuthenticationToken(
                email,
                null,
                Collections.emptyList());
    }

    // Get email from the JWT token.
    public String getEmailFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT token has expired.");
        } catch (JwtException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT token.");
        }
    }
}
