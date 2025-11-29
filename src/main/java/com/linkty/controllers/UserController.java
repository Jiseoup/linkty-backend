package com.linkty.controllers;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.linkty.dto.request.RegisterRequest;
import com.linkty.dto.request.WithdrawRequest;
import com.linkty.dto.request.LoginRequest;
import com.linkty.dto.request.ResetPasswordRequest;
import com.linkty.dto.request.ChangePasswordRequest;
import com.linkty.dto.response.MessageResponse;
import com.linkty.dto.response.RegisterResponse;
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
    public ResponseEntity<RegisterResponse> register(
            @RequestBody @Valid RegisterRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        String captchaToken = request.getCaptchaToken();

        // Verify the captcha token.
        captchaService.verifyToken(captchaToken);

        RegisterResponse response = userService.createAccount(email, password);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // User withdrawal.
    @DeleteMapping("/withdraw")
    public ResponseEntity<MessageResponse> withdraw(
            @RequestHeader("Authorization") String authToken,
            @RequestBody @Valid WithdrawRequest request,
            HttpServletResponse response) {
        String password = request.getPassword();

        MessageResponse messageResponse =
                userService.deleteAccount(authToken, password, response);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    // User login.
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletResponse response) {
        String email = request.getEmail();
        String password = request.getPassword();
        Boolean rememberMe = request.getRememberMe();

        TokenResponse tokenResponse =
                userService.userLogin(email, password, rememberMe, response);
        return ResponseEntity.status(HttpStatus.OK).body(tokenResponse);
    }

    // User logout.
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(
            @RequestHeader("Authorization") String authToken,
            HttpServletResponse response) {

        MessageResponse messageResponse =
                userService.userLogout(authToken, response);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    // Validate reset password token.
    @GetMapping("/reset-password")
    public ResponseEntity<MessageResponse> validateResetPassword(
            @RequestParam String token) {

        MessageResponse response =
                userService.validateResetPasswordToken(token);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Reset user password.
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(
            @RequestBody @Valid ResetPasswordRequest request) {
        String token = request.getToken();
        String password = request.getPassword();

        MessageResponse response =
                userService.resetUserPassword(token, password);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Change user password.
    @PatchMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(
            @RequestHeader("Authorization") String authToken,
            @RequestBody @Valid ChangePasswordRequest request,
            HttpServletResponse response) {
        String currentPassword = request.getCurrentPassword();
        String newPassword = request.getNewPassword();

        MessageResponse messageResponse = userService.changeUserPassword(
                authToken, currentPassword, newPassword, response);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    // Reissue access token.
    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponse> refresh(
            @CookieValue(required = false) String refreshToken) {
        TokenResponse response = userService.refreshToken(refreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
