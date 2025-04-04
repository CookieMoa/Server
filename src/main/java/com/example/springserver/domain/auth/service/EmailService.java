package com.example.springserver.domain.auth.service;

import com.example.springserver.global.common.api.status.ErrorStatus;
import com.example.springserver.global.exception.GeneralException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Random;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final EmailVerificationService emailVerificationService;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    @Value("${spring.mail.username}")
    private String senderEmail;

    // 랜덤으로 숫자 생성
    public String createNumber() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 8; i++) { // 인증 코드 8자리
            int index = random.nextInt(3); // 0~2까지 랜덤, 랜덤값으로 switch문 실행

            switch (index) {
                case 0 -> key.append((char) (random.nextInt(26) + 97)); // 소문자
                case 1 -> key.append((char) (random.nextInt(26) + 65)); // 대문자
                case 2 -> key.append(random.nextInt(10)); // 숫자
            }
        }
        return key.toString();
    }

    public MimeMessage createMail(String mail, String number) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("[CUKIMOA 서비스] 이메일 인증 코드 안내");

        String body = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>" +
                "<h2 style='color: #333;'>CUKIMOA 서비스 이메일 인증</h2>" +
                "<p style='font-size: 16px; color: #555;'>" +
                "안녕하세요, <b>CUKIMOA 서비스</b>를 이용해 주셔서 감사합니다.<br>" +
                "보안을 위해 아래의 인증 코드를 입력하여 이메일 인증을 완료해 주세요." +
                "</p>" +
                "<div style='text-align: center; margin: 20px 0;'>" +
                "<span style='font-size: 24px; font-weight: bold; color: #007BFF;'>" + number + "</span>" +
                "</div>" +
                "<p style='font-size: 14px; color: #777;'>" +
                "인증 코드는 " + (authCodeExpirationMillis / 1000 / 60) + "분 동안 유효합니다.<br>" +
                "만약 요청한 적이 없다면 이 이메일을 무시해 주세요." +
                "</p>" +
                "<hr style='border: none; border-top: 1px solid #ddd;'>" +
                "<p style='font-size: 12px; color: #aaa; text-align: center;'>" +
                "본 이메일은 발신 전용입니다.<br>" +
                "ⓒ 2025 CUKIMOA Team. All rights reserved." +
                "</p>" +
                "</div>";

        message.setText(body, "UTF-8", "html");

        return message;
    }

    // 메일 발송
    public long sendSimpleMessage(String email){
        try {
            String number = createNumber(); // 랜덤 인증번호 생성

            MimeMessage message = createMail(email, number); // 메일 생성

            javaMailSender.send(message); // 메일 발송

            emailVerificationService.saveVerificationCode(email, number, authCodeExpirationMillis); // Redis 저장

            return authCodeExpirationMillis; // 만료기간 반환
        } catch (MessagingException e) {
            log.error("이메일 메시지 생성 실패: {}", email, e);
            throw new GeneralException(ErrorStatus.EMAIL_SEND_FAILED);
        } catch (MailException e) {
            log.error("이메일 전송 실패 (SMTP 문제): {}", email, e);
            throw new GeneralException(ErrorStatus.EMAIL_NOT_FOUND);
        }
    }
}