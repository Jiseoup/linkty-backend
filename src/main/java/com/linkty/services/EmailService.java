package com.linkty.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import com.linkty.utils.CodeGenerator;
import com.linkty.email.EmailSender;
import com.linkty.email.EmailTemplate;
import com.linkty.exception.CustomException;
import com.linkty.exception.ErrorCode;
import com.linkty.entities.redis.EmailVerification;
import com.linkty.entities.redis.ResetPassword;
import com.linkty.dto.response.MessageResponse;
import com.linkty.repositories.UserRepository;
import com.linkty.repositories.EmailVerificationRepository;
import com.linkty.repositories.ResetPasswordRepository;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${redis.email-verification-ttl}")
    private long verificationTtl;

    @Value("${redis.reset-password-ttl}")
    private long resetPasswordTtl;

    private final EmailSender emailSender;
    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final ResetPasswordRepository resetPasswordRepository;

    // Sends a verification email to the receiver.
    public MessageResponse sendVerificationEmail(String receiver) {
        // Check whether a user with the given email already exists.
        if (userRepository.existsByEmailAndDeletedFalse(receiver)) {
            throw new CustomException(ErrorCode.EMAIL_CONFLICTED);
        }

        // Creates an 6-digits verification code.
        String code = CodeGenerator.generateNumeric(6);

        // Build and save the EmailVerification in Redis.
        EmailVerification emailVerification = EmailVerification.builder()
                .email(receiver).code(code).expire(verificationTtl).build();
        emailVerificationRepository.save(emailVerification);

        // Send verification email.
        emailSender.sendEmailWithTemplate(EmailTemplate.VERIFICATION, receiver,
                code, verificationTtl);

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

        // Delete the email verification from Redis.
        emailVerificationRepository.deleteById(receiver);

        return new MessageResponse("Verification code confirmed successfully.");
    }

    // Sends a reset password email to the receiver.
    public MessageResponse sendResetPasswordEmail(String receiver) {
        // Check whether a user with the given email does not exists.
        if (!userRepository.existsByEmailAndDeletedFalse(receiver)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        // Generate raw token and hash token.
        String rawToken = CodeGenerator.generateToken();
        String hashToken = CodeGenerator.generateHashToken(rawToken);

        // Build and save the ResetPassword in Redis.
        ResetPassword resetPassword = ResetPassword.builder().email(receiver)
                .hashToken(hashToken).expire(resetPasswordTtl).build();
        resetPasswordRepository.save(resetPassword);

        // Send reset password email.
        emailSender.sendEmailWithTemplate(EmailTemplate.RESET_PASSWORD,
                receiver, rawToken, resetPasswordTtl);

        return new MessageResponse("Reset password email sent successfully.");
    }
}
