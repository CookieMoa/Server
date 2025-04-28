package com.example.springserver.domain.stamp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class StampResponseDTO {

    private StampResponseDTO() {
        throw new IllegalStateException("Utility class");
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostStampRes {
        private Long stampBoardId;
        private Long cafeId;
        private Long customerId;
        private Integer stampCount;
        private String createdAt;
        private String updatedAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetStampBoardRes {
        private Long stampBoardId;
        private Long cafeId;
        private String cafeName;
        private Integer stampCount;
        private Integer stampGoal;
    }
}
