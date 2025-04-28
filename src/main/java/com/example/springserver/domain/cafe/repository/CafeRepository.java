package com.example.springserver.domain.cafe.repository;

import com.example.springserver.entity.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CafeRepository extends JpaRepository<Cafe, Long> {

    Optional<Cafe> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
