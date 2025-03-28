package com.example.securitywithredis.domain.auth.service;

import com.example.securitywithredis.domain.auth.dto.AuthRequestDTO;
import com.example.securitywithredis.domain.user.service.UserService;
import com.example.securitywithredis.global.common.api.status.ErrorStatus;
import com.example.securitywithredis.global.exception.GeneralException;
import com.example.securitywithredis.domain.user.converter.UserConverter;
import com.example.securitywithredis.domain.user.UserEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SignUpService {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public SignUpService(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public UserEntity signUpProcess(AuthRequestDTO.SignUpDTO request){

        boolean isExist = userService.existsByUsername(request.getUsername());

        if(isExist){
            throw new GeneralException(ErrorStatus.MEMBER_IS_EXIST);
        }

        // UserEntity 객체 converter를 통해 생성
        UserEntity newUser = UserConverter.toUser(request, bCryptPasswordEncoder);

        return userService.saveUser(newUser);
    }
}
