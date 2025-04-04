package com.example.springserver.application.auth;

import com.example.springserver.domain.auth.converter.AuthConverter;
import com.example.springserver.domain.auth.dto.AuthRequestDTO;
import com.example.springserver.domain.auth.dto.AuthResponseDTO;
import com.example.springserver.domain.auth.service.EmailService;
import com.example.springserver.domain.auth.service.EmailVerificationService;
import com.example.springserver.domain.auth.service.SignUpService;
import com.example.springserver.domain.auth.service.ReissueService;
import com.example.springserver.domain.user.UserEntity;
import com.example.springserver.global.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "회원가입 및 토큰 재발급 API")
@RequestMapping("/auth")
public class AuthController {

    private final SignUpService signUpService;
    private final ReissueService reissueService;
    private final EmailService emailService;
    private final EmailVerificationService emailVerificationService;


    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ApiResponse<AuthResponseDTO.SignUpResultDTO> signUpProcess(@RequestBody @Valid AuthRequestDTO.SignUpDTO request) {
        UserEntity newUser = signUpService.signUpProcess(request);
        return ApiResponse.onSuccess(AuthConverter.toSignUpResultDTO(newUser));
    }

    @Operation(summary = "토큰 재발행")
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        return reissueService.reissue(request, response);
    }

    @Operation(summary = "이메일 인증 코드 요청")
    @PostMapping("/verify-email")
    public ApiResponse<AuthResponseDTO.VerifyEmailRes> verifyEmail
            (@RequestBody @Valid AuthRequestDTO.VerifyEmailReq request) {
        long authCodeExpirationMillis = emailService.sendSimpleMessage(request.getEmail());
        return ApiResponse.onSuccess(AuthConverter.toVerifyEmailRes(authCodeExpirationMillis));
    }

    @Operation(summary = "이메일 인증 코드 검사")
    @PostMapping("/verify-code")
    public ApiResponse<AuthResponseDTO.VerifyCodeRes> verifyCode
            (@RequestBody @Valid AuthRequestDTO.VerifyCodeReq request) {
        AuthResponseDTO.VerifyCodeRes tokenDetail = emailVerificationService.verifyCode(request.getEmail(), request.getCode(), request.getPurpose());
        return ApiResponse.onSuccess(tokenDetail);
    }
}