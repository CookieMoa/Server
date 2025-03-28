package com.example.securitywithredis.domain.user.service;

import com.example.securitywithredis.domain.user.UserEntity;
import com.example.securitywithredis.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public UserEntity saveUser(UserEntity user) {
        return userRepository.save(user);
    }
}