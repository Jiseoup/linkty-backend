package com.linkty.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import com.linkty.utils.CodeGenerator;
import com.linkty.utils.KeyGenerator;
import com.linkty.email.EmailSender;
import com.linkty.email.EmailPurposeEnum;
import com.linkty.exception.CustomException;
import com.linkty.exception.ErrorCode;
import com.linkty.entities.redis.EmailVerification;
import com.linkty.dto.response.MessageResponse;
import com.linkty.repositories.UserRepository;
import com.linkty.repositories.EmailVerificationRepository;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${redis.email-verification-ttl}")
    private long timeToLive;

    private final EmailSender emailSender;
    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationRepository;

    // Sends a verification email to the receiver.
    public MessageResponse sendVerificationEmail(String receiver,
            EmailPurposeEnum purpose) {
        // Check whether a user with the given email already exists.
        boolean userExists =
                userRepository.existsByEmailAndDeletedFalse(receiver);

        // Validate request based on the purpose of email verification.
        switch (purpose) {
            // CASE 1. Register: The email must not exist.
            case EmailPurposeEnum.REGISTER -> {
                if (userExists)
                    throw new CustomException(ErrorCode.EMAIL_CONFLICTED);
            }
            // CASE 2. Find Password: The email must exist.
            case EmailPurposeEnum.FIND_PASSWORD -> {
                if (!userExists)
                    throw new CustomException(ErrorCode.USER_NOT_FOUND);
            }
            default -> throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        // Creates an 6-digits verification code.
        String code = CodeGenerator.generateNumeric(6);

        // Build and save the EmailVerification in Redis.
        EmailVerification emailVerification = EmailVerification
                .builder().id(KeyGenerator
                        .generateEmailVerificationKey(receiver, purpose))
                .code(code).expire(timeToLive).build();
        emailVerificationRepository.save(emailVerification);

        // Send verification email.
        emailSender.sendVerificationEmail(receiver, code, timeToLive, purpose);

        return new MessageResponse("Verification email sent successfully.");
    }

    // Confirms an email verification code.
    public MessageResponse confirmVerificationCode(String receiver, String code,
            EmailPurposeEnum purpose) {
        // Generates a redis email verification key by email and purpose.
        String id =
                KeyGenerator.generateEmailVerificationKey(receiver, purpose);

        // Retrieve the email verification stored in Redis by generated id.
        EmailVerification emailVerification =
                emailVerificationRepository.findById(id).orElseThrow(
                        () -> new CustomException(ErrorCode.INVALID_CODE));

        // Check if the verification code is correct.
        if (!emailVerification.getCode().equals(code)) {
            throw new CustomException(ErrorCode.INVALID_CODE);
        }

        // Delete the email verification from Redis.
        emailVerificationRepository.deleteById(id);

        return new MessageResponse("Verification code confirmed successfully.");
    }
}
