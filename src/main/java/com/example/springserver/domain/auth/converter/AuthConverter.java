package com.example.securitywithredis.domain.auth.converter;

import com.example.securitywithredis.domain.auth.dto.AuthResponseDTO;
import com.example.securitywithredis.domain.user.UserEntity;
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
}
