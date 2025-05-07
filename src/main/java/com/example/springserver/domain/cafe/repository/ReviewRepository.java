package com.example.springserver.domain.cafe.repository;

import com.example.springserver.entity.Customer;
import com.example.springserver.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByCafeId(Long cafeId, Pageable pageable);
    Page<Review> findByCustomerId(Long customerId, Pageable pageable);
}
