package com.example.springserver.domain.cafe.dto;

import com.example.springserver.domain.keyword.dto.KeywordResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class CafeResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostCafeRes {
        private Long cafeId;
        private String name;
        private String address;
        private Double latitude;
        private Double longitude;
        private String contact;
        private String intro;
        private String imgUrl;
    }

}
