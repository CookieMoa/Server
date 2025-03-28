package com.example.springserver.domain.user.repository;

import com.example.springserver.domain.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Boolean existsByUsername(String username);

    Optional<UserEntity> findByUsername(String username);
}
