package com.example.springserver.domain.log.repository;

import com.example.springserver.domain.log.enums.StampLogStatus;
import com.example.springserver.entity.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StampLogRepository extends JpaRepository<StampLog, Long> {

    // 전체 개수
    @Query("SELECT COALESCE(SUM(s.count), 0) FROM StampLog s WHERE s.stampLogStatus = :stampLogStatus")
    Long sum(StampLogStatus stampLogStatus);

    // 고객별 개수
    @Query("SELECT COALESCE(SUM(s.count), 0)  FROM StampLog s WHERE s.stampBoard.customer = :customer AND s.stampLogStatus = :stampLogStatus")
    Long sumByCustomer(Customer customer, StampLogStatus stampLogStatus);

    // 카페별 개수
    @Query("SELECT COALESCE(SUM(s.count), 0)  FROM StampLog s WHERE s.stampBoard.cafe = :cafe AND s.stampLogStatus = :stampLogStatus")
    Long sumByCafe(Cafe cafe, StampLogStatus stampLogStatus);

    @Query("""
SELECT FUNCTION('HOUR', s.createdAt) AS hour,
       COUNT(s)
FROM StampLog s
WHERE s.createdAt BETWEEN :start AND :end
GROUP BY FUNCTION('HOUR', s.createdAt)
ORDER BY FUNCTION('HOUR', s.createdAt) ASC
""")
    List<Object[]> sumByHourOnDate(@Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);


    // 10일 이내의 작성 가능한 리뷰 조회
    @Query("""
    SELECT s FROM StampLog s
    WHERE s.pendingReview = true
      AND s.createdAt > :tenDaysAgo
      AND s.stampBoard.customer.id = :customerId
    ORDER BY s.createdAt DESC
    """)
    List<StampLog> findValidPendingReviewsByCustomer(
            @Param("customerId") Long customerId,
            @Param("tenDaysAgo") LocalDateTime tenDaysAgo
    );
}
