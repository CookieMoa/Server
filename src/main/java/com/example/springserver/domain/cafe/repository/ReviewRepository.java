package com.example.springserver.domain.cafe.repository;

import com.example.springserver.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
