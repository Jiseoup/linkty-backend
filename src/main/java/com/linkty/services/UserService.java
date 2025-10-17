package com.linkty.services;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
// import jakarta.servlet.http.Cookie;
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

    // Creates an user account.
    @Transactional
    public MessageResponse createAccount(String email, String password) {
        // Retrieve a not deleted user with the given email.
        Optional<User> activeUser =
                userRepository.findByEmailAndDeletedFalse(email);

        // If not deleted user exists, throws an exception.
        if (activeUser.isPresent()) {
            throw new CustomException(ErrorCode.EMAIL_CONFLICTED);
        }

        // Retrieve a deleted user with the given email.
        Optional<User> inactiveUser = userRepository.findByEmail(email);

        User user;
        // If deleted user exists, restore an user.
        if (inactiveUser.isPresent()) {
            User userToRestore = inactiveUser.get();
            user = userToRestore.toBuilder().deleted(false)
                    .password(passwordEncoder.encode(password)).build();
        }
        // If deleted user not exists, create a new user.
        else {
            user = User.builder().email(email)
                    .password(passwordEncoder.encode(password)).build();
        }
        // Save the User entity.
        userRepository.save(user);

        return new MessageResponse("User account created successfully.");
    }

    // Deletes an user account.
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
            Boolean rememberMe, HttpServletResponse response) {
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
        String refreshToken =
                jwtProvider.generateRefreshToken(email, rememberMe);

        // // Set HttpOnly refresh token cookie.
        // Cookie cookie = new Cookie("refreshToken", refreshToken);
        // cookie.setHttpOnly(true);
        // cookie.setSecure(true);
        // cookie.setPath("/");
        // cookie.setMaxAge((int) timeToLive);
        // response.addCookie(cookie);

        // Temp function: Set refresh token cookie for development.
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
        RefreshToken redisRefreshToken = RefreshToken.builder().email(email)
                .token(refreshToken).expire(timeToLive).build();
        refreshTokenRepository.save(redisRefreshToken);

        response.setHeader("Set-Cookie", cookie);

        return new TokenResponse(accessToken);
    }

    // Handles user logout process.
    public MessageResponse userLogout(String authToken,
            HttpServletResponse response) {
        // Extract and validate the token from the authorization header.
        String token = jwtProvider.resolveToken(authToken);
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
                "refreshToken=; Max-Age=0; Path=/; HttpOnly; Secure; SameSite=Lax";
        response.setHeader("Set-Cookie", cookie);

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
        User user =
                userRepository.findByEmailAndDeletedFalse(email).orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.changePassword(passwordEncoder.encode(password));

        // Delete the reset password entitiy from Redis.
        resetPasswordRepository.deleteById(email);

        return new MessageResponse("Reset user password successfully.");
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
