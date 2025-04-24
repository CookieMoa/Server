package com.example.springserver.domain.cafe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CafeResponseDTO {

    private CafeResponseDTO() {
        throw new IllegalStateException("Utility class");
    }

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

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditCafeRes {
        private Long cafeId;
        private String name;
        private String address;
        private Double latitude;
        private Double longitude;
        private String contact;
        private String intro;
        private String imgUrl;
        private String createdAt;
        private String updatedAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostCafeAdvRes {
        private Long cafeId;
        private String advImgUrl;
    }
}
