package com.linkty.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.linkty.dto.request.UserRequest;
import com.linkty.dto.request.RefreshTokenRequest;
import com.linkty.dto.response.MessageResponse;
import com.linkty.dto.response.LoginResponse;
import com.linkty.dto.response.RegisterResponse;
import com.linkty.dto.response.RefreshTokenResponse;
import com.linkty.services.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    // User registration.
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @RequestBody @Valid UserRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

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
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid UserRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        LoginResponse response = userService.userLogin(email, password);
        return ResponseEntity.ok(response);
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
    public ResponseEntity<RefreshTokenResponse> refresh(
            @RequestBody @Valid RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        RefreshTokenResponse response = userService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }
}
