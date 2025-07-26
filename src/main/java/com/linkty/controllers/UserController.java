package com.linkty.controllers;

import jakarta.validation.Valid;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import com.linkty.dto.request.UserRequest;
import com.linkty.dto.request.RegisterRequest;
import com.linkty.dto.response.MessageResponse;
import com.linkty.dto.response.LoginResponse;
import com.linkty.dto.response.RegisterResponse;
import com.linkty.dto.response.TokenResponse;
import com.linkty.services.UserService;
import com.linkty.services.CaptchaService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    @Value("${redis.refresh-token-ttl}")
    private long timeToLive;

    private final UserService userService;
    private final CaptchaService captchaService;

    // User registration.
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @RequestBody @Valid RegisterRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        String captchaToken = request.getCaptchaToken();

        // Verify the captcha token.
        captchaService.verifyToken(captchaToken);

        RegisterResponse response = userService.createAccount(email, password);
        return ResponseEntity.ok(response);
    }

    // User withdrawal.
    @DeleteMapping("/withdraw")
    public ResponseEntity<MessageResponse> withdraw(
            @RequestBody @Valid UserRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        MessageResponse response = userService.deleteAccount(email, password);
        return ResponseEntity.ok(response);
    }

    // User login.
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @RequestBody @Valid UserRequest request,
            HttpServletResponse response) {
        String email = request.getEmail();
        String password = request.getPassword();

        // Authenticate user and get access token and refresh token.
        LoginResponse loginResponse = userService.userLogin(email, password);
        String accessToken = loginResponse.getAccessToken();
        String refreshToken = loginResponse.getRefreshToken();

        // Create HttpOnly refresh token cookie.
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) timeToLive);
        response.addCookie(cookie);

        TokenResponse tokenResponse = new TokenResponse(accessToken);
        return ResponseEntity.ok(tokenResponse);
    }

    // User logout.
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(
            @RequestHeader("Authorization") String authorizationHeader) {
        MessageResponse response = userService.userLogout(authorizationHeader);
        return ResponseEntity.ok(response);
    }

    // Reissue access token.
    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponse> refresh(@CookieValue(
            name = "refreshToken", required = false) String refreshToken) {
        TokenResponse response = userService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }
}
