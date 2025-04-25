package com.example.springserver.domain.cafe.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

public class CafeRequestDTO {

    @Getter
    @Setter
    public static class PostCafeReq {

        @NotNull(message = "id는 필수입니다.")
        private Long id;

        @NotEmpty
        private String name;
        @NotEmpty
        private String address;
        @NotNull
        private Double latitude;
        @NotNull
        private Double longitude;

        private String contact;
        private String intro;
    }

    @Getter
    @Setter
    public static class EditCafeReq {
        private String name;
        private String address;
        private Double latitude;
        private Double longitude;
        private String contact;
        private String intro;
    }

    @Getter
    public static class VerifyBusinessReq {
        @NotEmpty
        private String businessNumber;
        @NotEmpty
        private String representativeName;
        @NotEmpty
        private String openingDate;
    }

    @Getter
    public static class PostStampRewardReq {
        private String reward;
        private Integer stampCount;
    }
}