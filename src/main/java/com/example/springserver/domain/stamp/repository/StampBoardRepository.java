package com.example.springserver.domain.stamp.repository;

import com.example.springserver.entity.Cafe;
import com.example.springserver.entity.Customer;
import com.example.springserver.entity.StampBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StampBoardRepository extends JpaRepository<StampBoard, Long> {
    Optional<StampBoard> findStampBoardByCafeIdAndCustomerId(Long cafeId, Long customerId);
}
