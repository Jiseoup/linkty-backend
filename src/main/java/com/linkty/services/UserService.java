package com.linkty.services;

import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.linkty.jwt.JwtProvider;
import com.linkty.exception.CustomException;
import com.linkty.exception.ErrorCode;
import com.linkty.entities.postgresql.User;
import com.linkty.entities.redis.RefreshToken;
import com.linkty.dto.response.MessageResponse;
import com.linkty.dto.response.RegisterResponse;
import com.linkty.dto.response.TokenResponse;
import com.linkty.repositories.UserRepository;
import com.linkty.repositories.RefreshTokenRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${redis.refresh-token-ttl}")
    private long timeToLive;

    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    // Creates a new user account.
    @Transactional
    public RegisterResponse createAccount(String email, String password) {
        // Check if the requested email already exists.
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.EMAIL_CONFLICTED);
        }

        // Build and save the User entity.
        User user = User.builder().email(email)
                .password(passwordEncoder.encode(password)).build();
        userRepository.save(user);

        return new RegisterResponse(user.getEmail(), user.getJoinDate());
    }

    // Deletes a user account.
    @Transactional
    public MessageResponse deleteAccount(String email, String password) {
        // Retrieve the User entity by email.
        User user =
                userRepository.findByEmailAndDeletedFalse(email).orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // Validate user password.
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // Delete user.
        user.setDeleted(true);

        return new MessageResponse("User account deleted successfully.");
    }

    // Handles user login process.
    public TokenResponse userLogin(String email, String password,
            HttpServletResponse response) {
        // Retrieve the User entity by email.
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.INVALID_EMAIL_OR_PASSWORD));

        // Validate user password.
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_EMAIL_OR_PASSWORD);
        }

        // Generate JWT tokens.
        String accessToken = jwtProvider.generateAccessToken(email);
        String refreshToken = jwtProvider.generateRefreshToken(email);

        // Build and save the RefreshToken in Redis.
        RefreshToken redisRefreshToken = RefreshToken.builder().email(email)
                .token(refreshToken).expire(timeToLive).build();
        refreshTokenRepository.save(redisRefreshToken);

        // // Set HttpOnly refresh token cookie.
        // Cookie cookie = new Cookie("refreshToken", refreshToken);
        // cookie.setHttpOnly(true);
        // cookie.setSecure(true);
        // cookie.setPath("/");
        // cookie.setMaxAge((int) timeToLive);
        // response.addCookie(cookie);

        // Temp function: Set refresh token cookie for development.
        String cookie = String.format(
                "refreshToken=%s; Max-Age=%d; Path=/; HttpOnly; Secure; SameSite=None",
                refreshToken, (int) timeToLive);
        response.setHeader("Set-Cookie", cookie);

        return new TokenResponse(accessToken);
    }

    // Handles user logout process.
    public MessageResponse userLogout(String authorizationHeader,
            HttpServletResponse response) {
        // Extract and validate the token from the authorization header.
        String token = jwtProvider.resolveToken(authorizationHeader);
        if (token == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // Get email from the provided token.
        String email = jwtProvider.getEmailFromToken(token);

        // Delete refresh token from Redis.
        refreshTokenRepository.deleteById(email);

        // // Remove HttpOnly refresh token cookie.
        // Cookie cookie = new Cookie("refreshToken", null);
        // cookie.setHttpOnly(true);
        // cookie.setSecure(true);
        // cookie.setPath("/");
        // cookie.setMaxAge(0);
        // response.addCookie(cookie);

        // Temp function: Remove refresh token cookie for development.
        String cookie =
                "refreshToken=; Max-Age=0; Path=/; HttpOnly; Secure; SameSite=None";
        response.setHeader("Set-Cookie", cookie);

        return new MessageResponse("User logged out successfully.");
    }

    // Refresh access token using a valid refresh token.
    public TokenResponse refreshToken(String refreshToken) {
        // Get email from the refresh token.
        String email = jwtProvider.getEmailFromToken(refreshToken);

        // Retrieve the refresh token stored in Redis by email.
        RefreshToken redisRefreshToken =
                refreshTokenRepository.findById(email).orElseThrow(
                        () -> new CustomException(ErrorCode.INVALID_TOKEN));

        // Validate refresh token.
        if (!redisRefreshToken.getToken().equals(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // Generate new access token.
        String accessToken = jwtProvider.generateAccessToken(email);

        return new TokenResponse(accessToken);
    }
}
