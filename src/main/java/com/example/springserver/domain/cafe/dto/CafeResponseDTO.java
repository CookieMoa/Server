package com.example.springserver.domain.cafe.dto;

import com.example.springserver.domain.cafe.enums.CafeStatus;
import com.example.springserver.domain.keyword.dto.KeywordResponseDTO;
import com.example.springserver.domain.stamp.dto.StampResponseDTO;
import com.example.springserver.global.common.paging.CommonPageRes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

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
        private String openTime;
        private String closeTime;
        private String imgUrl;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetCafeRes {
        private Long cafeId;
        private String name;
        private String address;
        private Double latitude;
        private Double longitude;
        private String contact;
        private String intro;
        private String imgUrl;
        private String openTime;
        private String closeTime;
        private List<StampRewardDto> rewardList;
        private List<KeywordResponseDTO.KeywordDto> keywordList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetMyCafeRes {
        private Long cafeId;
        private String name;
        private String address;
        private Double latitude;
        private Double longitude;
        private String contact;
        private String intro;
        private Long totalStampCount;
        private Long totalUsedStampCount;
        private String imgUrl;
        private String advImgUrl;
        private String openTime;
        private String closeTime;
        private CafeStatus cafeStatus;
        private LocalDateTime createdAt;
        private List<StampRewardDto> rewardList;
        private List<KeywordResponseDTO.KeywordDto> keywordList;
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
        private String openTime;
        private String closeTime;
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

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostStampRewardRes {
        private Long stampRewardId;
        private String reward;
        private Integer stampCount;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditStampRewardRes {
        private Long stampRewardId;
        private String reward;
        private Integer stampCount;
        private String createdAt;
        private String updatedAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StampRewardDto {
        private Long stampRewardId;
        private String reward;
        private Integer stampCount;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostReviewRes {
        private Long reviewId;
        private Long cafeId;
        private Long customerId;
        private String name;
        private String content;
        private List<KeywordResponseDTO.KeywordDto> keywordList;
        private String createdAt;
        private String updatedAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetCafeReviewRes {
        private Long reviewId;
        private Long cafeId;
        private Long customerId;
        private String customerName;
        private String customerImgUrl;
        private String content;
        private List<KeywordResponseDTO.KeywordDto> keywordList;
        private String createdAt;
        private String updatedAt;
    }

    @SuperBuilder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchCafeReviewsRes extends CommonPageRes {
        private List<CafeResponseDTO.GetCafeReviewRes> reviewList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetCafeNearByRes {
        private Long cafeId;
        private String name;
        private String address;
        private String imgUrl;
        private Double latitude;
        private Double longitude;
        private Double distance;
        private List<KeywordResponseDTO.KeywordDto> keywordList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchCafeNearByRes {
        private List<CafeResponseDTO.GetCafeNearByRes> cafeList;
        private String sortBy;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetCafesRes {
        private Long cafeId;
        private String name;
        private String address;
        private String imgUrl;
        private String searchBy;
        private StampResponseDTO.StampBoardDto stampBoard;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchCafesRes {
        private List<CafeResponseDTO.GetCafesRes> cafeList;
    }
}
