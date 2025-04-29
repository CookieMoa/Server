package com.example.springserver.domain.stamp.repository;

import com.example.springserver.entity.StampBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface StampBoardRepository extends JpaRepository<StampBoard, Long> {
    Optional<StampBoard> findStampBoardByCafeIdAndCustomerId(Long cafeId, Long customerId);

    Page<StampBoard> findByCustomerId(Long customerId, Pageable pageable);

    @Query("SELECT SUM(s.stampsCount), SUM(s.usedStamps) FROM StampBoard s WHERE s.cafe.id = :cafeId")
    Object[] findTotalStampsByCafeId(@Param("cafeId") Long cafeId);

}
