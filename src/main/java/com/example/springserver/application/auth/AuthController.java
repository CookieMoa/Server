package com.example.springserver.application.auth;

import com.example.springserver.domain.auth.converter.AuthConverter;
import com.example.springserver.domain.auth.dto.AuthRequestDTO;
import com.example.springserver.domain.auth.dto.AuthResponseDTO;
import com.example.springserver.domain.auth.service.AuthService;
import com.example.springserver.domain.auth.service.EmailService;
import com.example.springserver.domain.auth.service.EmailVerificationService;
import com.example.springserver.domain.auth.service.ReissueService;
import com.example.springserver.entity.UserEntity;
import com.example.springserver.global.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "회원가입 및 토큰 재발급 API")
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final ReissueService reissueService;
    private final EmailService emailService;
    private final EmailVerificationService emailVerificationService;


    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ApiResponse<AuthResponseDTO.SignUpRes> signUp(@RequestBody @Valid AuthRequestDTO.SignUpReq request) {
        UserEntity newUser = authService.signUpProcess(request);
        return ApiResponse.onSuccess(AuthConverter.toSignUpRes(newUser));
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
        // todo : 회원가입 유무 검사
//        authService.checkEmailSignupStatus(request.getEmail());

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

    @Operation(summary = "비밀번호 변경")
    @PutMapping("/password")
    public ApiResponse<Void> editPassword
            (@RequestBody @Valid AuthRequestDTO.EditPasswordReq request) {
        authService.editPassword(request);
        return ApiResponse.onSuccess(null);
    }
}