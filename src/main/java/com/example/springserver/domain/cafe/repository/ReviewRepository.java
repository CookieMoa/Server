package com.example.springserver.domain.cafe.repository;

import com.example.springserver.entity.Customer;
import com.example.springserver.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByCafeId(Long cafeId, Pageable pageable);
    Page<Review> findByCafeIdOrderByCreatedAtDesc(Long cafeId, Pageable pageable);
    Page<Review> findByCustomerIdOrderByCreatedAtDesc(Long customerId, Pageable pageable);
    Page<Review> findByCustomerIdAndMalicious(Long customerId, Boolean isMalicious, Pageable pageable);
    List<Review> findAllBy();
}
