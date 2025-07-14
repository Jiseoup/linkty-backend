package com.linkty.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import com.linkty.utils.CodeGenerator;
import com.linkty.email.EmailSender;
import com.linkty.exception.CustomException;
import com.linkty.exception.ErrorCode;
import com.linkty.entities.redis.EmailVerification;
import com.linkty.dto.response.MessageResponse;
import com.linkty.repositories.EmailVerificationRepository;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${redis.email-verification-ttl}")
    private long timeToLive;

    private final EmailSender emailSender;
    private final EmailVerificationRepository emailVerificationRepository;

    // Sends a verification email to the receiver.
    public MessageResponse sendVerificationEmail(String receiver) {
        // Creates an 6-digits verification code.
        String code = CodeGenerator.generateNumeric(6);

        // Build and save the EmailVerification in Redis.
        EmailVerification emailVerification = EmailVerification.builder()
                .email(receiver).code(code).expire(timeToLive).build();
        emailVerificationRepository.save(emailVerification);

        // Send verification email.
        emailSender.sendVerificationEmail(receiver, code, timeToLive);

        return new MessageResponse("Verification email sent successfully.");
    }

    // Confirms an email verification code.
    public MessageResponse confirmVerificationCode(String receiver,
            String code) {
        // Retrieve the email verification stored in Redis by email.
        EmailVerification emailVerification =
                emailVerificationRepository.findById(receiver).orElseThrow(
                        () -> new CustomException(ErrorCode.INVALID_CODE));

        // Check if the verification code is correct.
        if (!emailVerification.getCode().equals(code)) {
            throw new CustomException(ErrorCode.INVALID_CODE);
        }

        // Delete the email verification from Redis
        emailVerificationRepository.deleteById(receiver);

        return new MessageResponse("Verification code confirmed successfully.");
    }
}
