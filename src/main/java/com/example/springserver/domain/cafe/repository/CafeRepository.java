package com.example.springserver.domain.cafe.repository;

import com.example.springserver.entity.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CafeRepository extends JpaRepository<Cafe, Long> {
}
