package com.example.springserver.domain.auth.service;

import com.example.springserver.domain.auth.dto.AuthRequestDTO;
import com.example.springserver.domain.user.enums.AccountStatus;
import com.example.springserver.global.jwt.EmailJwtUtil;
import com.example.springserver.domain.user.service.UserService;
import com.example.springserver.global.common.api.status.ErrorStatus;
import com.example.springserver.global.exception.GeneralException;
import com.example.springserver.domain.user.converter.UserConverter;
import com.example.springserver.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailJwtUtil emailJwtUtil;

    public UserEntity signUpProcess(AuthRequestDTO.SignUpReq request){
        // 이메일 토큰 검증
        String token = request.getEmailToken();

        if (emailJwtUtil.isExpired(token)) {
            throw new GeneralException(ErrorStatus.EMAIL_TOKEN_EXPIRED);
        }

        if (!emailJwtUtil.isValidCategory(token, "signup")) {
            throw new GeneralException(ErrorStatus.INVALID_EMAIL_TOKEN_CATEGORY);
        }

        String emailFromToken = emailJwtUtil.getEmail(token);
        if (!emailFromToken.equals(request.getUsername())) {
            throw new GeneralException(ErrorStatus.EMAIL_TOKEN_MISMATCH);
        }

        boolean isExist = userService.existsByUsername(request.getUsername());

        if(isExist){
            throw new GeneralException(ErrorStatus.MEMBER_IS_EXIST);
        }

        String role = request.getRole();
        if(!"ROLE_CAFE".equals(role) && !"ROLE_CUSTOMER".equals(role)){
            throw new GeneralException(ErrorStatus.INVALID_ROLE);
        }

        // UserEntity 객체 converter를 통해 생성
        UserEntity newUser = UserConverter.toUser(request, bCryptPasswordEncoder, role);

        return userService.saveUser(newUser);
    }

    public void checkEmailSignupStatus(String email) {
        UserEntity user = userService.findUserEntityByUsername(email);

        if (user == null) {
            return;
        }

        if (user.getAccountStatus() == AccountStatus.ACTIVE) {
            throw new GeneralException(ErrorStatus.MEMBER_IS_EXIST);
        }

        if (user.getAccountStatus() == AccountStatus.INACTIVE) {
            if (user.getRole().equals("ROLE_CAFE")) {
                throw new GeneralException(ErrorStatus.SIGNUP_IN_PROGRESS_CAFE);
            } else {
                throw new GeneralException(ErrorStatus.SIGNUP_IN_PROGRESS_CUSTOMER);
            }
        }
    }

    public void editPassword(AuthRequestDTO.EditPasswordReq request) {
        // 이메일 토큰 검증
        String token = request.getEmailToken();

        if (emailJwtUtil.isExpired(token)) {
            throw new GeneralException(ErrorStatus.EMAIL_TOKEN_EXPIRED);
        }

        if (!emailJwtUtil.isValidCategory(token, "password_reset")) {
            throw new GeneralException(ErrorStatus.INVALID_EMAIL_TOKEN_CATEGORY);
        }

        UserEntity user = userService.getUserByUsername(emailJwtUtil.getEmail(token));

        // 비밀번호 변경
        user.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        userService.saveUser(user);
    }
}
