package com.example.springserver.domain.auth.converter;

import com.example.springserver.domain.auth.dto.AuthResponseDTO;
import com.example.springserver.global.security.CustomUserDetails;
import com.example.springserver.entity.UserEntity;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuthConverter {

    // 날짜를 포맷하는 메서드
    private static String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    public static AuthResponseDTO.SignUpRes toSignUpRes(UserEntity user){
        return AuthResponseDTO.SignUpRes.builder()
                .userId(user.getId())
                .build();
    }

    public static AuthResponseDTO.LoginRes toLoginRes(CustomUserDetails userDetail, String accessToken, String refreshToken){
        return AuthResponseDTO.LoginRes.builder()
                .userId(userDetail.getUserId())
                .role(userDetail.getRole())
                .access(accessToken)
                .refresh(refreshToken)
                .build();
    }

    public static AuthResponseDTO.VerifyEmailRes toVerifyEmailRes(long expiresIn){
        return AuthResponseDTO.VerifyEmailRes.builder()
                .expiresIn(expiresIn)
                .build();
    }

    public static AuthResponseDTO.VerifyCodeRes toVerifyCodeRes(String token, String purpose, long expiresIn){
        return AuthResponseDTO.VerifyCodeRes.builder()
                .verificationToken(token)
                .category(purpose)
                .expiresIn(expiresIn)
                .build();
    }
}
