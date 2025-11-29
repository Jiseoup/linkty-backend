package com.linkty.services;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.linkty.jwt.JwtProvider;
import com.linkty.utils.CodeGenerator;
import com.linkty.exception.CustomException;
import com.linkty.exception.ErrorCode;
import com.linkty.entities.postgresql.User;
import com.linkty.entities.redis.RefreshToken;
import com.linkty.entities.redis.ResetPassword;
import com.linkty.dto.response.MessageResponse;
import com.linkty.dto.response.RegisterResponse;
import com.linkty.dto.response.TokenResponse;
import com.linkty.repositories.UserRepository;
import com.linkty.repositories.RefreshTokenRepository;
import com.linkty.repositories.ResetPasswordRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    // Redis TTL for refresh tokens when rememberMe is enabled.
    @Value("${redis.refresh-token-ttl}")
    private long redisTtl;

    // Short Redis TTL for refresh tokens when rememberMe is disabled.
    @Value("${redis.short-refresh-token-ttl}")
    private long shortRedisTtl;

    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ResetPasswordRepository resetPasswordRepository;

    // Clear refresh token from HttpOnly Cookie.
    private void clearRefreshTokenCookie(HttpServletResponse response) {
        String cookie =
                "refreshToken=; Max-Age=0; Path=/; HttpOnly; Secure; SameSite=Lax";
        response.setHeader("Set-Cookie", cookie);
    }

    // Delete refresh token from Redis and HttpOnly Cookie.
    private void processLogout(Long userId, HttpServletResponse response) {
        refreshTokenRepository.deleteById(userId);
        clearRefreshTokenCookie(response);
    }

    // Creates an user account.
    @Transactional
    public RegisterResponse createAccount(String email, String password) {
        // Check whether a user with the given email already exists.
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.EMAIL_CONFLICTED);
        }

        // Build and save the User entity.
        User user = User.builder().email(email)
                .password(passwordEncoder.encode(password)).build();
        userRepository.save(user);

        return new RegisterResponse(user.getId(), email, user.getJoinDate());
    }

    // Deletes an user account.
    @Transactional
    public MessageResponse deleteAccount(String authToken, String password,
            HttpServletResponse response) {
        // Get user id from the provided authToken.
        Long userId = jwtProvider.getClaimsFromBearerToken(authToken)
                .get("userId", Long.class);

        // Retrieve the User entity by id.
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // Validate user password.
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // Delete user. (CASCADE delete will remove all associated Urls and Logs)
        userRepository.delete(user);

        // Process Logout: Delete refresh token from redis and cookie.
        processLogout(userId, response);

        return new MessageResponse("User account deleted successfully.");
    }

    // Handles user login process.
    @Transactional
    public TokenResponse userLogin(String email, String password,
            Boolean rememberMe, HttpServletResponse response) {
        // Retrieve the User entity by email.
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_EMAIL_OR_PASSWORD));

        // Validate user password.
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_EMAIL_OR_PASSWORD);
        }

        // Update last login timestamp.
        user.updateLastLogin();

        // Generate JWT tokens.
        Long userId = user.getId();
        String accessToken = jwtProvider.generateAccessToken(email, userId);
        String refreshToken =
                jwtProvider.generateRefreshToken(email, userId, rememberMe);

        // Set HttpOnly refresh token cookie.
        String cookie;
        long timeToLive;
        if (Boolean.TRUE.equals(rememberMe)) {
            timeToLive = redisTtl;
            cookie = String.format(
                    "refreshToken=%s; Max-Age=%d; Path=/; HttpOnly; Secure; SameSite=Lax",
                    refreshToken, (int) timeToLive);
        } else {
            timeToLive = shortRedisTtl;
            cookie = String.format(
                    "refreshToken=%s; Path=/; HttpOnly; Secure; SameSite=Lax",
                    refreshToken);
        }

        // Build and save the RefreshToken in Redis.
        RefreshToken redisRefreshToken = RefreshToken.builder().userId(userId)
                .token(refreshToken).expire(timeToLive).build();
        refreshTokenRepository.save(redisRefreshToken);

        response.setHeader("Set-Cookie", cookie);

        return new TokenResponse(accessToken);
    }

    // Handles user logout process.
    public MessageResponse userLogout(String authToken,
            HttpServletResponse response) {
        // Get user id from the provided authToken.
        Long userId = jwtProvider.getClaimsFromBearerToken(authToken)
                .get("userId", Long.class);

        // Process Logout: Delete refresh token from redis and cookie.
        processLogout(userId, response);

        return new MessageResponse("User logged out successfully.");
    }

    // Validate reset password URL token.
    public MessageResponse validateResetPasswordToken(String token) {
        // Convert the raw token into the hash token.
        String hashToken = CodeGenerator.generateHashToken(token);

        // Throw exception if the hash token is invalid.
        if (!resetPasswordRepository.existsByHashToken(hashToken)) {
            throw new CustomException(ErrorCode.RESET_PASSWORD_EXPIRED);
        }

        return new MessageResponse("Valid reset password token.");
    }

    // Handles user reset password process.
    @Transactional
    public MessageResponse resetUserPassword(String token, String password) {
        // Convert the raw token into the hash token.
        String hashToken = CodeGenerator.generateHashToken(token);

        // Retrieve the reset password entitiy stored in Redis by hashToken.
        ResetPassword resetPassword =
                resetPasswordRepository.findByHashToken(hashToken).orElseThrow(
                        () -> new CustomException(ErrorCode.INVALID_TOKEN));

        // Retrieve the user by email and change the password.
        String email = resetPassword.getEmail();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.changePassword(passwordEncoder.encode(password));

        // Delete the reset password entitiy from Redis.
        resetPasswordRepository.deleteById(email);

        return new MessageResponse("Reset user password successfully.");
    }

    // Handles user change password process.
    @Transactional
    public MessageResponse changeUserPassword(String authToken,
            String currentPassword, String newPassword,
            HttpServletResponse response) {
        // Get user id from the provided authToken.
        Long userId = jwtProvider.getClaimsFromBearerToken(authToken)
                .get("userId", Long.class);

        // Retrieve the User entity by email.
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // Validate current user password.
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CURRENT_PASSWORD);
        }

        // Change password with encoded new password.
        user.changePassword(passwordEncoder.encode(newPassword));

        // Process Logout: Delete refresh token from redis and cookie.
        processLogout(userId, response);

        return new MessageResponse("Change user password successfully.");
    }

    // Refresh access token using a valid refresh token.
    public TokenResponse refreshToken(String refreshToken) {
        // Get email and user id from the refresh token.
        Claims claims = jwtProvider.getClaimsFromToken(refreshToken);
        String email = claims.getSubject();
        Long userId = claims.get("userId", Long.class);

        // Retrieve the refresh token stored in Redis by email.
        RefreshToken redisRefreshToken =
                refreshTokenRepository.findById(userId).orElseThrow(
                        () -> new CustomException(ErrorCode.INVALID_TOKEN));

        // Validate refresh token.
        if (!redisRefreshToken.getToken().equals(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // Generate new access token.
        String accessToken = jwtProvider.generateAccessToken(email, userId);

        return new TokenResponse(accessToken);
    }
}
