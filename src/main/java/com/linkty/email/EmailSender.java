package com.linkty.email;

import java.util.Map;
import java.util.HashMap;
import java.io.UnsupportedEncodingException;

import lombok.RequiredArgsConstructor;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Async;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.linkty.exception.CustomException;
import com.linkty.exception.ErrorCode;

@Component
@RequiredArgsConstructor
public class EmailSender {

    private static final String SENDER_EMAIL = "noreply@linkty.kr";
    private static final String SENDER_NAME = "Linkty";

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    // Sends a verification email.
    @Async
    public void sendVerificationEmail(String receiver, String code,
            long expire) {
        // Set subject and template.
        // String subject = "[Linkty] 회원가입 이메일 인증번호 안내";
        String subject = "[Linkty] 깃허브 액션 테스트";
        String template = "email/verification";

        // Set variables for the email template.
        Map<String, String> variables = new HashMap<>();
        variables.put("code", code);
        variables.put("expire", String.valueOf(expire / 60));

        // Send email.
        sendEmail(receiver, subject, template, variables);
    }

    // Sends an email with the given subject, template, and variables.
    private void sendEmail(String receiver, String subject, String template,
            Map<String, String> variables) {
        try {
            // Creates a MimeMessage.
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, false, "UTF-8");

            // Create and set variables in the Context.
            Context context = new Context();
            variables.forEach(context::setVariable);

            // Process the template and set the email body.
            String emailBody = templateEngine.process(template, context);

            // Set the email details.
            helper.setTo(receiver);
            helper.setFrom(SENDER_EMAIL, SENDER_NAME);
            helper.setSubject(subject);
            helper.setText(emailBody, true);

            // Send email.
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new CustomException(ErrorCode.SEND_EMAIL_FAILED);
        }
    }
}
