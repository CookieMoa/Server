package com.example.springserver.domain.cafe.converter;

import com.example.springserver.domain.cafe.dto.CafeRequestDTO;
import com.example.springserver.domain.cafe.dto.CafeResponseDTO;
import com.example.springserver.entity.Cafe;
import com.example.springserver.entity.UserEntity;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CafeConverter {

    // 날짜를 포맷하는 메서드
    private static String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    public static Cafe toCafe(CafeRequestDTO.PostCafeReq request, UserEntity user, String imgUrl){

        return Cafe.builder()
                .user(user)
                .name(request.getName())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .contact(request.getContact())
                .intro(request.getIntro())
                .imgUrl(imgUrl)
                .build();
    }

    public static CafeResponseDTO.PostCafeRes toPostCafeRes(Cafe cafe){
        return CafeResponseDTO.PostCafeRes.builder()
                .cafeId(cafe.getId())
                .name(cafe.getName())
                .address(cafe.getAddress())
                .latitude(cafe.getLatitude())
                .longitude(cafe.getLongitude())
                .contact(cafe.getContact())
                .intro(cafe.getIntro())
                .imgUrl(cafe.getImgUrl())
                .build();
    }
}
