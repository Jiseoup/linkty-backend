package com.linkty.jwt;

import java.util.Date;
import java.security.Key;
import java.util.Collections;
import java.nio.charset.StandardCharsets;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.linkty.exception.CustomException;
import com.linkty.exception.ErrorCode;

@Component
public class JwtProvider {

    private final Key key;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;
    private final long shortRefreshTokenValidity;

    // Constructor that initializes the secret key and token validity durations.
    public JwtProvider(@Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-token-validity}") long accessTokenValidity,
            @Value("${jwt.refresh-token-validity}") long refreshTokenValidity,
            @Value("${jwt.short-refresh-token-validity}") long shortRefreshTokenValidity) {
        this.key =
                Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
        this.shortRefreshTokenValidity = shortRefreshTokenValidity;
    }

    // Generate JWT access token.
    public String generateAccessToken(String email, Long userId) {
        return generateToken(email, userId, accessTokenValidity);
    }

    // Generate JWT refresh token.
    public String generateRefreshToken(String email, Long userId,
            Boolean rememberMe) {
        return generateToken(email, userId,
                rememberMe ? refreshTokenValidity : shortRefreshTokenValidity);
    }

    // Common method to generate JWT tokens.
    private String generateToken(String email, Long userId, long validity) {
        Date date = new Date();
        return Jwts.builder().setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(email).claim("userId", userId).setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + validity))
                .signWith(key).compact();
    }

    // Validate JWT token.
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build()
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
        String email = getClaimsFromToken(token).getSubject();
        return new UsernamePasswordAuthenticationToken(email, null,
                Collections.emptyList());
    }

    // Get claims from the JWT token.
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    // Extract and validate the bearerToken from the authorization header and return claims.
    public Claims getClaimsFromBearerToken(String bearerToken) {
        String token = resolveToken(bearerToken);
        if (token == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        return getClaimsFromToken(token);
    }
}
