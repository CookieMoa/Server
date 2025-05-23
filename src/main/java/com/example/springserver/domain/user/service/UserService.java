package com.example.springserver.domain.user.service;

import com.example.springserver.entity.Customer;
import com.example.springserver.entity.UserEntity;
import com.example.springserver.domain.user.repository.UserRepository;
import com.example.springserver.global.common.api.status.ErrorStatus;
import com.example.springserver.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public boolean existsByUsername(String username) { return userRepository.existsByUsername(username);}
    public UserEntity getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
    }

    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
    }

    public UserEntity findUserEntityByUsername(String username) {
        return userRepository.findUserEntityByUsername(username);
    }

    public UserEntity saveUser(UserEntity user) {
        return userRepository.save(user);
    }

    public void deleteAccount(String userName) {
        UserEntity user = getUserByUsername(userName);
        userRepository.delete(user);
    }
}
