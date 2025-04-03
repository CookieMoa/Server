package com.example.springserver.domain.auth.converter;

import com.example.springserver.domain.auth.dto.AuthResponseDTO;
import com.example.springserver.domain.auth.security.CustomUserDetails;
import com.example.springserver.domain.user.UserEntity;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuthConverter {

    // 날짜를 포맷하는 메서드
    private static String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    public static AuthResponseDTO.SignUpResultDTO toSignUpResultDTO(UserEntity user){
        return AuthResponseDTO.SignUpResultDTO.builder()
                .memberId(user.getId())
                .createAt(formatDateTime(user.getCreatedAt()))
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
}
