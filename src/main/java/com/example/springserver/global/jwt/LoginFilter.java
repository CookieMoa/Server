package com.example.springserver.global.jwt;

import com.example.springserver.domain.auth.converter.AuthConverter;
import com.example.springserver.domain.auth.dto.AuthRequestDTO;
import com.example.springserver.domain.auth.dto.AuthResponseDTO;
import com.example.springserver.global.common.api.status.ErrorStatus;
import com.example.springserver.global.exception.GeneralException;
import com.example.springserver.global.security.CustomUserDetails;
import com.example.springserver.global.common.api.ApiResponse;
import com.example.springserver.global.common.util.RefreshUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshUtil refreshUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshUtil refreshUtil) {

        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshUtil = refreshUtil;
        setFilterProcessesUrl("/auth/login"); // 원하는 엔드포인트로 변경
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        try {
            // JSON 바디에서 username과 password 추출
            ObjectMapper objectMapper = new ObjectMapper();
            AuthRequestDTO.LoginReq loginRequest = null;
            loginRequest = objectMapper.readValue(req.getInputStream(), AuthRequestDTO.LoginReq.class);

            String username = loginRequest.getUsername();
            String password = loginRequest.getPassword();

            // 스프링 시큐리티에서 username과 password를 검증하기 위해 token에 담는다.
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

            // token에 담은 데이터를 검증을 위한 AuthenticationManager로 전달
            // 이 메서드는 CustomUserDetailsService를 통해 사용자의 정보를 조회하고 검증합니다.
            // CustomUserDetailsService와 CustomUserDetails를 통해 DB내의 사용자 정보를 조회하고 입력받은 authToken과 비교를 한다.
            // 인증이 성공적으로 이루어지면, CustomUserDetails를 통해 사용자 정보와 권한을 제공한다.
            return authenticationManager.authenticate(authToken);

        } catch (IOException e) {
            throw new AuthenticationException("입력 형식이 잘못됐습니다.", e) {};
        }
    }

    // 로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    // Authentication(getPrincipal(), getCredentials(), getAuthorities(), isAuthenticated(), getDetails(), getName())
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        // 유저 정보
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        // 토큰 생성
        String access = jwtUtil.createJwt("access", username, role, 86400000L);
        String refresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

        // Refresh 토큰을 Redis에 저장
        refreshUtil.addRefreshToken(username, refresh, 86400000L);

        try {
            // 응답 설정
            ApiResponse<AuthResponseDTO.LoginRes> apiResponse =
                    ApiResponse.onSuccess(AuthConverter.toLoginRes(userDetails, access, refresh));

            // JSON 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(apiResponse);

            // 응답 보내기
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Authorization", "Bearer " + access);
            response.setStatus(HttpStatus.OK.value());
            response.getWriter().write(jsonResponse);

        } catch (IOException e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().write("{\"isSuccess\": false, \"code\": \"501\", \"message\": \"로그인 성공 처리 중 서버 오류가 발생했습니다.\"}");
        }
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        int statusCode = HttpStatus.UNAUTHORIZED.value(); // 기본값
        String code = "AUTH401";
        String message = "로그인에 실패했습니다.";

        if (failed instanceof LockedException) {
            statusCode = HttpStatus.FORBIDDEN.value();
            code = "AUTH_LOCKED";
            message = "계정이 잠겨있습니다.";
        } else if (failed instanceof DisabledException) {
            statusCode = HttpStatus.FORBIDDEN.value();
            code = "AUTH_INACTIVE";
            message = "비활성화된 계정입니다.";
        } else if (failed instanceof BadCredentialsException) {
            statusCode = HttpStatus.UNAUTHORIZED.value();
            code = "AUTH_FAILED";
            message = "아이디 또는 비밀번호가 잘못되었습니다.";
        }

        response.setStatus(statusCode);
        response.getWriter().write(
                String.format("{\"isSuccess\":false, \"code\":\"%s\", \"message\":\"%s\"}", code, message)
        );
    }
}
