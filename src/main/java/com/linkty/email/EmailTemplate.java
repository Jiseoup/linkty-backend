// @formatter:off
package com.linkty.email;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailTemplate {
    // Define email template enum.
    VERIFICATION("[Linkty] 회원가입 이메일 인증번호 안내", "email/verification"),
    RESET_PASSWORD("[Linkty] 비밀번호 재설정 안내", "email/reset-password");

    private final String subject;
    private final String templatePath;
}
