package com.example.springserver.domain.cafe.service;

import com.example.springserver.domain.log.enums.StampLogStatus;
import com.example.springserver.domain.log.service.StampLogService;
import com.example.springserver.domain.stamp.converter.StampConverter;
import com.example.springserver.domain.stamp.repository.StampRepository;
import com.example.springserver.domain.stamp.service.StampBoardService;
import com.example.springserver.entity.Cafe;
import com.example.springserver.entity.Customer;
import com.example.springserver.entity.Stamp;
import com.example.springserver.entity.StampBoard;
import com.example.springserver.global.common.api.status.ErrorStatus;
import com.example.springserver.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewStampHelper {

    private final StampRepository stampRepository;
    private final StampBoardService stampBoardService;
    private final StampLogService stampLogService;

    public void addReviewStamp(Cafe cafe, Customer customer, int count) {
        // 1. 스탬프 보드 조회
        StampBoard stampBoard = stampBoardService.findStampBoard(cafe, customer);
        if (stampBoard == null) throw new GeneralException(ErrorStatus.STAMPBOARD_NOT_FOUND);
        // 2. 스탬프 생성
        Stamp stamp = StampConverter.toStamp(stampBoard);
        stampRepository.save(stamp);
        // 3. 스탬프보드 통계 업데이트
        stampBoard.increaseStampCount(count);
        stampLogService.addReviewLog(stampBoard, StampLogStatus.ISSUED, count);
        // 4. 카페 총 발급 스탬프 수 증가
        cafe.increaseTotalStampCount(count);
    }
}