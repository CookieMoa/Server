package com.example.springserver.domain.cafe.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

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

        @NotNull(message = "오픈 시간은 필수입니다.")
        @JsonFormat(pattern = "HH:mm")
        private LocalTime openTime;

        @NotNull(message = "마감 시간은 필수입니다.")
        @JsonFormat(pattern = "HH:mm")
        private LocalTime closeTime;
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
        @JsonFormat(pattern = "HH:mm")
        private LocalTime openTime;
        @JsonFormat(pattern = "HH:mm")
        private LocalTime closeTime;
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

    @Getter
    public static class PostReviewReq {

        @NotNull(message = "id는 필수입니다.")
        private Long customerId;

        @NotNull(message = "id는 필수입니다.")
        private Long stampLogId;

        @NotEmpty
        private String content;

        private List<String> keywordList;
    }
}