package com.example.springserver.domain.log.service;

import com.example.springserver.domain.cafe.converter.CafeConverter;
import com.example.springserver.domain.cafe.service.CafeService;
import com.example.springserver.domain.customer.service.CustomerService;
import com.example.springserver.domain.log.converter.StampLogConverter;
import com.example.springserver.domain.log.enums.StampLogStatus;
import com.example.springserver.domain.log.repository.StampLogRepository;
import com.example.springserver.domain.stamp.converter.StampConverter;
import com.example.springserver.domain.stamp.dto.StampRequestDTO;
import com.example.springserver.domain.stamp.dto.StampResponseDTO;
import com.example.springserver.domain.stamp.repository.StampRepository;
import com.example.springserver.domain.stamp.service.StampBoardService;
import com.example.springserver.entity.*;
import com.example.springserver.global.common.api.status.ErrorStatus;
import com.example.springserver.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StampLogService {
    private final StampLogRepository stampLogRepository;

    public void addLog(StampBoard stampBoard, StampLogStatus stampLogStatus, Integer count) {
        StampLog newLog = StampLogConverter.toStampLog(stampBoard, stampLogStatus, count);
        stampLogRepository.save(newLog);
    }

    public Long getTotalCountByCustomer(Customer customer, StampLogStatus stampLogStatus) {
        return stampLogRepository.sumByCustomer(customer, stampLogStatus);
    }

    public Long getTotalCountByCafe(Cafe cafe, StampLogStatus stampLogStatus) {
        return stampLogRepository.sumByCafe(cafe, stampLogStatus);
    }

    public List<StampLog> searchPendingReviewsByCustomer(Long customerId) {
        return stampLogRepository.findValidPendingReviewsByCustomer(customerId, LocalDateTime.now().minusDays(10));
    }

    public StampLog getStampLog(Long stampLogId) {
        return stampLogRepository.findById(stampLogId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.STAMPLOG_NOT_FOUND));
    }
}
