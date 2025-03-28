package com.example.securitywithredis.domain.auth.service;

import com.example.securitywithredis.global.common.api.status.ErrorStatus;
import com.example.securitywithredis.global.exception.GeneralException;
import com.example.securitywithredis.domain.auth.jwt.JWTUtil;
import com.example.securitywithredis.global.common.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ReissueService {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public ReissueService(JWTUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        String refresh = refreshTokenService.extractRefreshToken(request);
        if (refresh == null) {
            throw new GeneralException(ErrorStatus.INVALID_REFRESH_TOKEN); // Refresh Token이 없는 경우 예외 처리
        }

        // 유효성 검사 및 예외 발생
        refreshTokenService.validateRefreshToken(refresh);

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        // New JWT 생성
        String newAccess = jwtUtil.createJwt("access", username, role, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

        // Redis에서 기존 Refresh Token 삭제 및 새로 저장
        refreshTokenService.removeRefreshToken(username); // Redis에서 기존 Refresh 토큰 제거
        refreshTokenService.addRefreshToken(username, newRefresh, 86400000L); // 새 Refresh 토큰 저장

        response.setHeader("access", newAccess);
        response.addCookie(CookieUtil.createCookie("refresh", newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);
    }
}