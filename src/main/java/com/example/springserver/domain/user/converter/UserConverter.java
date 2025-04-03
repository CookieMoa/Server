package com.example.springserver.domain.user.converter;

import com.example.springserver.domain.auth.dto.AuthRequestDTO;
import com.example.springserver.domain.user.UserEntity;
import com.example.springserver.domain.user.enums.Gender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserConverter {

    //    UserEntity 객체를 만드는 작업 (클라이언트가 준 DTO to Entity)
    public static UserEntity toUser(AuthRequestDTO.SignUpDTO request, BCryptPasswordEncoder bCryptPasswordEncoder){

        return UserEntity.builder()
                .username(request.getUsername())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
//                .role("ROLE_USER")
//                .accountStatus(AccountStatus.ACTIVE)
                .build();
    }
}
