package com.example.springserver.domain.log.converter;

import com.example.springserver.domain.log.enums.StampLogStatus;
import com.example.springserver.domain.stamp.dto.StampResponseDTO;
import com.example.springserver.entity.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StampLogConverter {

    private StampLogConverter() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static StampLog toStampLog(StampBoard stampBoard, StampLogStatus stampLogStatus, Integer count){
        return StampLog.builder()
                .stampBoard(stampBoard)
                .stampLogStatus(stampLogStatus)
                .count(count)
                .build();
    }

    public static StampLog toReviewStampLog(StampBoard stampBoard, StampLogStatus stampLogStatus, Integer count){
        return StampLog.builder()
                .stampBoard(stampBoard)
                .stampLogStatus(stampLogStatus)
                .count(count)
                .pendingReview(false)
                .build();
    }
}
