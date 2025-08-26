package com.linkty.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.linkty.dto.request.EmailRequest;
import com.linkty.dto.request.EmailVerificationConfirmRequest;
import com.linkty.dto.response.MessageResponse;
import com.linkty.services.EmailService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    // Send verification email.
    @PostMapping("/verification")
    public ResponseEntity<MessageResponse> verification(
            @RequestBody @Valid EmailRequest request) {
        String email = request.getEmail();

        MessageResponse response = emailService.sendVerificationEmail(email);
        return ResponseEntity.ok(response);
    }

    // Confirm an email verification code.
    @PostMapping("/verification/confirm")
    public ResponseEntity<MessageResponse> confirmVerification(
            @RequestBody @Valid EmailVerificationConfirmRequest request) {
        String email = request.getEmail();
        String code = request.getCode();

        MessageResponse response =
                emailService.confirmVerificationCode(email, code);
        return ResponseEntity.ok(response);
    }

    // Send reset password email.
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(
            @RequestBody @Valid EmailRequest request) {
        String email = request.getEmail();

        MessageResponse response = emailService.sendResetPasswordEmail(email);
        return ResponseEntity.ok(response);
    }
}
