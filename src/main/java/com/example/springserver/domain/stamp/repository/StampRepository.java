package com.example.springserver.domain.stamp.repository;

import com.example.springserver.entity.Stamp;
import com.example.springserver.entity.StampBoard;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StampRepository extends JpaRepository<Stamp, Long> {

    // 사용되지 않은 스탬프 중에서 오래된 순서대로 N개 가져오기
    @Query("SELECT s FROM Stamp s WHERE s.stampBoard = :stampBoard AND s.isUsed = false ORDER BY s.createdAt ASC")
    List<Stamp> findByStampBoardAndIsUsedOrderByCreatedAtAsc(@Param("stampBoard") StampBoard stampBoard, Pageable pageable);

}
