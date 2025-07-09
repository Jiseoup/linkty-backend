package com.linkty.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.linkty.dto.request.EmailVerificationRequest;
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

        MessageResponse response = emailService.verifyEmail(email);
        return ResponseEntity.ok(response);
    }
}
