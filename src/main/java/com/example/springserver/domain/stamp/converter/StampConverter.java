package com.example.springserver.domain.stamp.converter;

import com.example.springserver.domain.stamp.dto.StampResponseDTO;
import com.example.springserver.entity.Cafe;
import com.example.springserver.entity.Customer;
import com.example.springserver.entity.Stamp;
import com.example.springserver.entity.StampBoard;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StampConverter {

    private StampConverter() {
        throw new UnsupportedOperationException("Utility class");
    }

    // 날짜를 포맷하는 메서드
    private static String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    public static StampBoard toStampBoard(Cafe cafe, Customer customer){

        return StampBoard.builder()
                .cafe(cafe)
                .customer(customer)
                .stampsCount(0)
                .usedStamps(0)
                .build();
    }

    public static Stamp toStamp(StampBoard stampBoard){

        return Stamp.builder()
                .stampBoard(stampBoard)
                .build();
    }

    public static StampResponseDTO.PostStampRes toPostStampRes(StampBoard stampBoard){
        return StampResponseDTO.PostStampRes.builder()
                .stampBoardId(stampBoard.getId())
                .cafeId(stampBoard.getCafe().getId())
                .customerId(stampBoard.getCustomer().getId())
                .stampCount(stampBoard.getStampsCount()- stampBoard.getUsedStamps())
                .createdAt(formatDateTime(stampBoard.getCreatedAt()))
                .updatedAt(formatDateTime(stampBoard.getUpdatedAt()))
                .build();
    }

    public static StampResponseDTO.GetStampBoardRes toGetStampBoardRes(StampBoard stampBoard, Integer stampGoal){
        return StampResponseDTO.GetStampBoardRes.builder()
                .stampBoardId(stampBoard.getId())
                .cafeId(stampBoard.getCafe().getId())
                .cafeName(stampBoard.getCafe().getName())
                .stampCount(stampBoard.getStampsCount()- stampBoard.getUsedStamps())
                .stampGoal(stampGoal)
                .build();
    }
}
