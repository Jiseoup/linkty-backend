package com.linkty.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.linkty.email.EmailPurposeEnum;
import com.linkty.dto.request.EmailVerificationRequest;
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
            @RequestBody @Valid EmailVerificationRequest request) {
        String email = request.getEmail();
        EmailPurposeEnum purpose = request.getPurpose();

        MessageResponse response =
                emailService.sendVerificationEmail(email, purpose);
        return ResponseEntity.ok(response);
    }

    // Confirm an email verification code.
    @PostMapping("/verification/confirm")
    public ResponseEntity<MessageResponse> confirmVerification(
            @RequestBody @Valid EmailVerificationConfirmRequest request) {
        String email = request.getEmail();
        String code = request.getCode();
        EmailPurposeEnum purpose = request.getPurpose();

        MessageResponse response =
                emailService.confirmVerificationCode(email, code, purpose);
        return ResponseEntity.ok(response);
    }
}
