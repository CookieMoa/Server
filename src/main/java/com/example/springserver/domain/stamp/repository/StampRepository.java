package com.example.springserver.domain.stamp.repository;

import com.example.springserver.entity.Stamp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StampRepository extends JpaRepository<Stamp, Long> {
}
