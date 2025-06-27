package com.urlshortener.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.urlshortener.services.UserService;
import com.urlshortener.dto.request.RegisterRequest;
import com.urlshortener.dto.request.WithdrawRequest;
import com.urlshortener.dto.request.LoginRequest;
import com.urlshortener.dto.request.RefreshTokenRequest;
import com.urlshortener.dto.response.RegisterResponse;
import com.urlshortener.dto.response.WithdrawResponse;
import com.urlshortener.dto.response.LoginResponse;
import com.urlshortener.dto.response.RefreshTokenResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    // User registration.
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        RegisterResponse response = userService.createAccount(email, password);
        return ResponseEntity.ok(response);
    }

    // User withdrawal.
    @DeleteMapping("/withdraw")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WithdrawResponse> withdraw(@RequestBody @Valid WithdrawRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        WithdrawResponse response = userService.deleteAccount(email, password);
        return ResponseEntity.ok(response);
    }

    // User login.
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        LoginResponse response = userService.userLogin(email, password);
        return ResponseEntity.ok(response);
    }

    // Reissue access token.
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        RefreshTokenResponse response = userService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }
}
