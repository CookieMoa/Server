package com.example.securitywithredis.domain.auth.service;

import com.example.securitywithredis.global.common.api.status.ErrorStatus;
import com.example.securitywithredis.global.exception.GeneralException;
import com.example.securitywithredis.global.common.util.RefreshUtil;
import com.example.securitywithredis.domain.auth.jwt.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {

    private final JWTUtil jwtUtil;
    private final RefreshUtil refreshUtil;

    public RefreshTokenService(JWTUtil jwtUtil, RefreshUtil refreshUtil) {
        this.jwtUtil = jwtUtil;
        this.refreshUtil = refreshUtil;
    }

    // Refresh Token을 요청에서 추출
    public String extractRefreshToken(HttpServletRequest request) {
        String refresh = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("refresh")) {
                    refresh = cookie.getValue();
                    break;
                }
            }
        }else{
            throw new GeneralException(ErrorStatus.REFRESH_TOKEN_IS_NULL); // Refresh 토큰이 없으면 예외 처리
        }
        return refresh;
    }

    // Refresh Token 유효성 검사
    public void validateRefreshToken(String refresh) {
        try {
            jwtUtil.isExpired(refresh); // Expired check
            String category = jwtUtil.getCategory(refresh);
            if (!category.equals("refresh")) {
                throw new GeneralException(ErrorStatus.INVALID_REFRESH_TOKEN); // Refresh 토큰이 아닌 경우
            }

            String username = jwtUtil.getUsername(refresh);

            // Redis에서 존재 여부 확인
            String storedRefreshToken = refreshUtil.getRefreshToken(username);
            if (storedRefreshToken == null || !storedRefreshToken.equals(refresh)) {
                throw new GeneralException(ErrorStatus.REFRESH_TOKEN_NOT_EXIST); // Redis에서 없거나 일치하지 않으면
            }
        } catch (ExpiredJwtException e) {
            throw new GeneralException(ErrorStatus.REFRESH_TOKEN_EXPIRED); // Expired Token 예외 처리
        }
    }

    // Refresh Token을 Redis에서 제거
    public void removeRefreshToken(String username) {
        refreshUtil.removeRefreshToken(username); // Redis에서 제거
    }

    // Refresh Token을 Redis에 저장
    public void addRefreshToken(String username, String refreshToken, long expirationTimeInMillis) {
        refreshUtil.addRefreshToken(username, refreshToken, expirationTimeInMillis);
    }
}