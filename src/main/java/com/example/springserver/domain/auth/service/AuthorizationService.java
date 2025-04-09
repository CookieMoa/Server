package com.example.springserver.domain.auth.service;

import com.example.springserver.domain.user.service.UserService;
import com.example.springserver.entity.UserEntity;
import com.example.springserver.global.common.api.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.springserver.global.exception.GeneralException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final UserService userService;

    public void validateCustomerAuthorization(String username, Long customerId) {
        UserEntity user = userService.getUserByUsername(username);
        if (!user.getId().equals(customerId)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN_USER_ACCESS);
        }
    }
}
