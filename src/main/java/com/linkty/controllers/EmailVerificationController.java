package com.linkty.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.linkty.dto.request.EmailVerificationRequest;
import com.linkty.dto.response.MessageResponse;
import com.linkty.services.EmailVerificationService;

@RestController
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    // Send email with verification code.
    @PostMapping("/verify-email")
    public ResponseEntity<MessageResponse> verify(
            @RequestBody @Valid EmailVerificationRequest request) {
        String email = request.getEmail();

        MessageResponse response = emailVerificationService.verifyEmail(email);
        return ResponseEntity.ok(response);
    }
}
