package com.example.springserver.domain.cafe.repository;

import com.example.springserver.entity.Cafe;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface CafeRepository extends JpaRepository<Cafe, Long> {

    Optional<Cafe> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    List<Cafe> findAllByOrderByCreatedAtDesc(Pageable pageable);
    List<Cafe> findByNameContainingIgnoreCaseOrderByCreatedAtDesc(String keyword, Pageable pageable);

    @Query("SELECT c FROM Cafe c WHERE c.id IN :ids")
    List<Cafe> findAllByIdIn(@Param("ids") List<Long> ids);
}
