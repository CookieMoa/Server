package com.example.springserver.domain.cafe.repository;

import com.example.springserver.entity.Cafe;
import com.example.springserver.entity.StampReward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StampRewardRepository extends JpaRepository<StampReward, Long> {
    Optional<StampReward> findById(Long rewardId);
}
