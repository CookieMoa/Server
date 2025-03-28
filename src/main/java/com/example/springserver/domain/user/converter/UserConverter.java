package com.example.securitywithredis.domain.user.converter;

import com.example.securitywithredis.domain.auth.dto.AuthRequestDTO;
import com.example.securitywithredis.domain.user.UserEntity;
import com.example.securitywithredis.domain.user.enums.Gender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserConverter {

    //    UserEntity 객체를 만드는 작업 (클라이언트가 준 DTO to Entity)
    public static UserEntity toUser(AuthRequestDTO.SignUpDTO request, BCryptPasswordEncoder bCryptPasswordEncoder){

        return UserEntity.builder()
                .username(request.getUsername())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
//                .role("ROLE_USER")
//                .accountStatus(AccountStatus.ACTIVE)
                .nickname(request.getNickname())
                .gender(Gender.valueOf(request.getGender()))
                .build();
    }
}
