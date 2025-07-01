package com.linkty.services;

import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.linkty.dto.response.LoginResponse;
import com.linkty.dto.response.LogoutResponse;
import com.linkty.dto.response.RefreshTokenResponse;
import com.linkty.dto.response.RegisterResponse;
import com.linkty.dto.response.WithdrawResponse;
import com.linkty.entities.RefreshToken;
import com.linkty.entities.User;
import com.linkty.jwt.JwtProvider;
import com.linkty.repositories.RefreshTokenRepository;
import com.linkty.repositories.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

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
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "This email is already in use.");
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

    // Handles user logout process.
    public LogoutResponse userLogout(String authorizationHeader) {
        // Extract and validate the token from the authorization header.
        String token = jwtProvider.resolveToken(authorizationHeader);
        if (token == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Token is invalid or missing.");
        }

        // Get email from the provided token.
        String email = jwtProvider.getEmailFromToken(token);

        // Delete refresh token from Redis.
        refreshTokenRepository.deleteById(email);

        return new LogoutResponse("User logged out successfully.");
    }

    // Refresh access token using a valid refresh token.
    public RefreshTokenResponse refreshToken(String refreshToken) {
        // Get email from the refresh token.
        String email = jwtProvider.getEmailFromToken(refreshToken);

        // Retrieve the refresh token stored in Redis by email.
        RefreshToken redisRefreshToken = refreshTokenRepository.findById(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        // Validate refresh token.
        if (!redisRefreshToken.getToken().equals(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token mismatch.");
        }

        // Generate new access token.
        String accessToken = jwtProvider.generateAccessToken(email);

        return new RefreshTokenResponse(accessToken);
    }
}
