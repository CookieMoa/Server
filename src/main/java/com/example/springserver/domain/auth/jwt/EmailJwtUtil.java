package com.example.springserver.domain.auth.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class EmailJwtUtil {
    // jwt에서 이젠 string key 말고 SecretKey를 사용한다.
    private final SecretKey emailKey;
    private static final long SIGNUP_TOKEN_EXPIRATION = 10 * 60 * 1000; // 10분
    private static final long PASSWORD_RESET_EXPIRATION = 5 * 60 * 1000; // 5분

    public EmailJwtUtil(@Value("${spring.jwt.email}")String secret){
        this.emailKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // 회원가입용 JWT 생성
    public String createRegisterToken(String email) {
        return createJwt("signup", email, SIGNUP_TOKEN_EXPIRATION);
    }

    // 비밀번호 변경용 JWT 생성
    public String createPasswordResetToken(String email) {
        return createJwt("password_reset", email, PASSWORD_RESET_EXPIRATION);
    }

    // JWT 검증 후 category 가져오기
    public String getCategory(String token) {
        return Jwts.parser().verifyWith(emailKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    // JWT 검증 후 email 가져오기
    public String getEmail(String token) {
        return Jwts.parser().verifyWith(emailKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }

    // JWT 만료 여부 확인
    public boolean isExpired(String token) {
        return Jwts.parser().verifyWith(emailKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    // 특정 category 값과 일치하는지 확인
    public boolean isValidCategory(String token, String expectedCategory) {
        return getCategory(token).equals(expectedCategory);
    }

    // JWT 생성 (공통)
    private String createJwt(String category, String email, long expiredMs) {
        return Jwts.builder()
                .claim("category", category)
                .claim("email", email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(emailKey)
                .compact();
    }
}
