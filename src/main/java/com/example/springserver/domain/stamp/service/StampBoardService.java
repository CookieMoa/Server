package com.example.springserver.domain.stamp.service;

import com.example.springserver.domain.stamp.converter.StampConverter;
import com.example.springserver.domain.stamp.repository.StampBoardRepository;
import com.example.springserver.entity.Cafe;
import com.example.springserver.entity.Customer;
import com.example.springserver.entity.StampBoard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StampBoardService {

    private final StampBoardRepository stampBoardRepository;

    public StampBoard getStampBoard(Cafe cafe, Customer customer) {
        // 1. StampBoard 조회
        return stampBoardRepository.findStampBoardByCafeIdAndCustomerId(cafe.getId(), customer.getId())
                .orElseGet(() -> {
                    // 2. 없으면 StampBoard 새로 생성
                    StampBoard newBoard = StampConverter.toStampBoard(cafe, customer);
                    return stampBoardRepository.save(newBoard);
                });
    }
}
