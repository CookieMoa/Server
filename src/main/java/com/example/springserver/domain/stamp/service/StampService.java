package com.example.springserver.domain.stamp.service;

import com.example.springserver.domain.cafe.service.CafeService;
import com.example.springserver.domain.customer.service.CustomerService;
import com.example.springserver.domain.log.enums.StampLogStatus;
import com.example.springserver.domain.log.service.StampLogService;
import com.example.springserver.domain.stamp.converter.StampConverter;
import com.example.springserver.domain.stamp.dto.StampRequestDTO;
import com.example.springserver.domain.stamp.dto.StampResponseDTO;
import com.example.springserver.domain.stamp.repository.StampRepository;
import com.example.springserver.entity.Cafe;
import com.example.springserver.entity.Customer;
import com.example.springserver.entity.Stamp;
import com.example.springserver.entity.StampBoard;
import com.example.springserver.global.common.api.status.ErrorStatus;
import com.example.springserver.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final StampLogService stampLogService;

    public StampResponseDTO.PostStampRes postStamp(StampRequestDTO.PostStampReq request) {

        Cafe cafe = cafeService.getCafeByUserId(request.getCafeId());
        Customer customer = customerService.getCustomerByUserId(request.getCustomerId());

        // 1. 스탬프 보드 조회 또는 생성
        StampBoard stampBoard = stampBoardService.getStampBoardOrPost(cafe, customer);

        // 2. 스탬프 생성
        List<Stamp> stamps = new ArrayList<>();
        for (int i = 0; i < request.getStampCount(); i++) {
            Stamp stamp = StampConverter.toStamp(stampBoard);
            stamps.add(stamp);
        }
        stampRepository.saveAll(stamps); // 배치 저장

        // 3. 스탬프보드 통계 업데이트
        stampBoard.increaseStampCount(request.getStampCount());
        stampLogService.addLog(stampBoard, StampLogStatus.ISSUED, request.getStampCount());

        // 4. 카페 총 발급 스탬프 수 증가
        cafe.increaseTotalStampCount(request.getStampCount());

        return StampConverter.toPostStampRes(stampBoard);
    }

    public StampResponseDTO.PostStampRes useStamp(StampRequestDTO.PostStampReq request) {

        Cafe cafe = cafeService.getCafeByUserId(request.getCafeId());
        Customer customer = customerService.getCustomerByUserId(request.getCustomerId());

        // 1. 스탬프 보드 조회
        StampBoard stampBoard = stampBoardService.getStampBoard(cafe, customer);

        // 2. 사용하지 않은 스탬프 가져오기 (createdAt 오름차순)
        Pageable pageable = PageRequest.of(0, request.getStampCount());
        List<Stamp> availableStamps = stampRepository.findByStampBoardAndIsUsedOrderByCreatedAtAsc(stampBoard, pageable);

        // 도장 부족
        if (availableStamps.size() < request.getStampCount()) {
            throw new GeneralException(ErrorStatus.NOT_ENOUGH_STAMPS);
        }

        // 3. 스탬프 사용 처리
        for (Stamp stamp : availableStamps) {
            stamp.useStamp();
        }

        // 4. 스탬프보드 통계 업데이트
        stampBoard.increaseUsedStampCount(request.getStampCount());
        stampLogService.addLog(stampBoard, StampLogStatus.USED, request.getStampCount());

        // 5. 카페 총 사용 스탬프 수 증가
        cafe.increaseTotalUsedStampCount(request.getStampCount());

        return StampConverter.toPostStampRes(stampBoard);
    }
}
