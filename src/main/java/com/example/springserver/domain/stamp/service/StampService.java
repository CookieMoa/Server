package com.example.springserver.domain.stamp.service;

import com.example.springserver.domain.cafe.service.CafeService;
import com.example.springserver.domain.customer.service.CustomerService;
import com.example.springserver.domain.stamp.converter.StampConverter;
import com.example.springserver.domain.stamp.dto.StampRequestDTO;
import com.example.springserver.domain.stamp.dto.StampResponseDTO;
import com.example.springserver.domain.stamp.repository.StampRepository;
import com.example.springserver.entity.Cafe;
import com.example.springserver.entity.Customer;
import com.example.springserver.entity.Stamp;
import com.example.springserver.entity.StampBoard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StampService {

    private final CafeService cafeService;
    private final StampBoardService stampBoardService;
    private final CustomerService customerService;
    private final StampRepository stampRepository;

    public StampResponseDTO.PostStampRes postStamp(StampRequestDTO.PostStampReq request) {

        Cafe cafe = cafeService.getCafeByUserId(request.getCafeId());
        Customer customer = customerService.getCustomerByUserId(request.getCustomerId());

        // 1. 스탬프 보드 조회 또는 생성
        StampBoard stampBoard = stampBoardService.getStampBoard(cafe, customer);

        // 2. 스탬프 생성
        List<Stamp> stamps = new ArrayList<>();
        for (int i = 0; i < request.getStampCount(); i++) {
            Stamp stamp = StampConverter.toStamp(stampBoard);
            stamps.add(stamp);
        }
        stampRepository.saveAll(stamps); // 배치 저장

        // 3. 스탬프보드 통계 업데이트
        stampBoard.increaseStampCount(request.getStampCount());

        return StampConverter.toPostStampRes(stampBoard);
    }
}
