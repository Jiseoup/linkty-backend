package com.urlshortener.services;

import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.urlshortener.entities.User;
import com.urlshortener.entities.RefreshToken;
import com.urlshortener.jwt.JwtProvider;
import com.urlshortener.repositories.UserRepository;
import com.urlshortener.repositories.RefreshTokenRepository;
import com.urlshortener.dto.response.RegisterResponse;
import com.urlshortener.dto.response.WithdrawResponse;
import com.urlshortener.dto.response.LoginResponse;
import com.urlshortener.dto.response.RefreshTokenResponse;

@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${redis.ttl}")
    private long timeToLive;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    // Creates a new user account.
    @Transactional
    public RegisterResponse createAccount(String email, String password) {
        // Check if the requested email already exists.
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This email is already in use.");
        }

        // Build and save the User entity.
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();
        userRepository.save(user);

        return new RegisterResponse(user.getEmail(), user.getJoinDate());
    }

    // Deletes a user account.
    @Transactional
    public WithdrawResponse deleteAccount(String email, String password) {
        // Retrieve the User entity by email.
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User email not found."));

        // Validate user password.
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect password.");
        }

        // Delete user.
        user.setDeleted(true);

        return new WithdrawResponse("User account deleted successfully.");
    }

    // Handles user login process.
    public LoginResponse userLogin(String email, String password) {
        // Retrieve the User entity by email.
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid email or password."));

        // Validate user password.
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
        }

        // Generate JWT tokens.
        String accessToken = jwtProvider.generateAccessToken(email);
        String refreshToken = jwtProvider.generateRefreshToken(email);

        // Build and save the RefreshToken in Redis.
        RefreshToken redisRefreshToken = RefreshToken.builder()
                .email(email)
                .token(refreshToken)
                .expire(timeToLive)
                .build();
        refreshTokenRepository.save(redisRefreshToken);

        return new LoginResponse(accessToken, refreshToken);
    }

    // Refresh access token using a valid refresh token.
    public RefreshTokenResponse refreshToken(String refreshToken) {
        // Get email from the refresh token.
        String email = jwtProvider.getEmailFromToken(refreshToken);

        // Retrieve the refresh token stored in Redis by email.
        RefreshToken redisRefreshToken = refreshTokenRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        // Validate refresh token.
        if (!redisRefreshToken.getToken().equals(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token mismatch.");
        }

        // Generate new access token.
        String accessToken = jwtProvider.generateAccessToken(email);

        return new RefreshTokenResponse(accessToken);
    }
}
