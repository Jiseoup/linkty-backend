package com.linkty.controllers;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.linkty.dto.request.UserRequest;
import com.linkty.dto.request.RegisterRequest;
import com.linkty.dto.response.MessageResponse;
import com.linkty.dto.response.TokenResponse;
import com.linkty.services.UserService;
import com.linkty.services.CaptchaService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final CaptchaService captchaService;

    // User registration.
    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(
            @RequestBody @Valid RegisterRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        String captchaToken = request.getCaptchaToken();

        // Verify the captcha token.
        captchaService.verifyToken(captchaToken);

        MessageResponse response = userService.createAccount(email, password);
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

        TokenResponse tokenResponse =
                userService.userLogin(email, password, response);
        return ResponseEntity.ok(tokenResponse);
    }

    // User logout.
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(
            @RequestHeader("Authorization") String authToken,
            HttpServletResponse response) {
        MessageResponse messageResponse =
                userService.userLogout(authToken, response);
        return ResponseEntity.ok(messageResponse);
    }

    // Reissue access token.
    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponse> refresh(@CookieValue(
            name = "refreshToken", required = false) String refreshToken) {
        TokenResponse response = userService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }
}
