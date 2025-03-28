package com.example.securitywithredis.domain.auth.jwt;

import com.example.securitywithredis.global.common.api.status.ErrorStatus;
import com.example.securitywithredis.global.exception.GeneralException;
import com.example.securitywithredis.domain.auth.service.RefreshTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class CustomLogoutFilter extends GenericFilterBean {

    private final RefreshTokenService refreshTokenService;
    private final JWTUtil jwtUtil;

    public CustomLogoutFilter(RefreshTokenService refreshTokenService, JWTUtil jwtUtil) {
        this.refreshTokenService = refreshTokenService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {
            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        String refresh = refreshTokenService.extractRefreshToken(request);
        if (refresh == null) {
            throw new GeneralException(ErrorStatus.INVALID_REFRESH_TOKEN); // Refresh Token이 없는 경우 예외 처리
        }
        // 유효성 검사 및 예외 발생
        refreshTokenService.validateRefreshToken(refresh);

        String username = jwtUtil.getUsername(refresh);
        // 로그아웃 진행
        refreshTokenService.removeRefreshToken(username);
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}