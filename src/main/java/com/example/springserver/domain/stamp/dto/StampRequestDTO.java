package com.example.springserver.domain.stamp.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

public class StampRequestDTO {

    private StampRequestDTO() {
        throw new IllegalStateException("Utility class");
    }

    @Getter
    public static class PostStampReq {

        @NotNull(message = "cafeId는 필수입니다.")
        private Long cafeId;

        @NotNull(message = "customerId는 필수입니다.")
        private Long customerId;

        @NotNull
        private Integer stampCount;
    }
}