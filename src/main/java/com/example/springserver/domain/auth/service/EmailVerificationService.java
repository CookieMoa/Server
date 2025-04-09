package com.example.springserver.domain.auth.service;

import com.example.springserver.domain.auth.converter.AuthConverter;
import com.example.springserver.domain.auth.dto.AuthResponseDTO;
import com.example.springserver.global.jwt.EmailJwtUtil;
import com.example.springserver.global.common.api.status.ErrorStatus;
import com.example.springserver.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailVerificationService.class);
    private final RedisTemplate<String, String> redisTemplate;
    private final EmailJwtUtil emailJwtUtil;

    private static final String EMAIL_VERIFICATION_PREFIX = "email_verification:";
    private static final long SIGNUP_TOKEN_EXPIRATION = 10 * 60 * 1000; // 10분
    private static final long PASSWORD_RESET_EXPIRATION = 5 * 60 * 1000; // 5분

    // 이메일 인증 코드 저장 (회원가입/비밀번호 변경 구분 없이 사용)
    public void saveVerificationCode(String email, String code, long expirationMillis) {
        redisTemplate.opsForValue().set(
                EMAIL_VERIFICATION_PREFIX + email, code, expirationMillis, TimeUnit.MILLISECONDS
        );
    }

    // 이메일 인증 코드 검증 후 JWT 반환 (회원가입/비밀번호 변경 용도 구분)
    public AuthResponseDTO.VerifyCodeRes verifyCode(String email, String code, String purpose) {
        String key = EMAIL_VERIFICATION_PREFIX + email;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            throw new GeneralException(ErrorStatus.EMAIL_CODE_NOT_EXIST);
        }
        if (!storedCode.equals(code)) {
            log.warn("[Email Verification] Invalid code attempt. Email: {}, Provided: {}, Expected: {}",
                    email, code, storedCode);
            throw new GeneralException(ErrorStatus.INVALID_EMAIL_CODE);
        }

        AuthResponseDTO.VerifyCodeRes response;

        // 인증 성공 후 JWT 발급 (회원가입 또는 비밀번호 변경)
        String token;
        if ("signup".equals(purpose)) {
            token = emailJwtUtil.createRegisterToken(email);
            response = AuthConverter.toVerifyCodeRes(token, emailJwtUtil.getCategory(token), SIGNUP_TOKEN_EXPIRATION);
        } else if ("reset".equals(purpose)) {
            token = emailJwtUtil.createPasswordResetToken(email);
            response = AuthConverter.toVerifyCodeRes(token, emailJwtUtil.getCategory(token), PASSWORD_RESET_EXPIRATION);
        } else {
            log.warn("[Email Verification] Invalid purpose attempt. Email: {}, Provided: {}, Expected: {}, Purpose: {}",
                    email, code, storedCode, purpose);
            throw new GeneralException(ErrorStatus.INVALID_PURPOSE);
        }

        // 인증 코드 사용 후 Redis에서 삭제
        redisTemplate.delete(key);
        return response;
    }
}

