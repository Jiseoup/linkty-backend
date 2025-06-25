package com.urlshortener.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.urlshortener.entities.User;
import com.urlshortener.jwt.JwtProvider;
import com.urlshortener.jwt.RefreshToken;
import com.urlshortener.jwt.RefreshTokenRepository;
import com.urlshortener.repositories.UserRepository;
import com.urlshortener.dto.request.RegisterRequest;
import com.urlshortener.dto.response.RegisterResponse;
import com.urlshortener.dto.response.LoginResponse;

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
    public RegisterResponse createAccount(RegisterRequest request) {
        // Check if the requested email already exists.
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This email is already in use.");
        }

        // Build and save the User entity.
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);

        return new RegisterResponse(user.getEmail(), user.getJoinDate());
    }

    // Handles user login process.
    public LoginResponse userLogin(String email, String password) {
        // Retrieve the User entity by email.
        User user = userRepository.findByEmail(email)
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
}
