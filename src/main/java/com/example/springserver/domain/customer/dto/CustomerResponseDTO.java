package com.example.springserver.domain.customer.dto;

import com.example.springserver.domain.keyword.dto.KeywordResponseDTO;
import com.example.springserver.domain.stamp.dto.StampResponseDTO;
import com.example.springserver.global.common.paging.CommonPageRes;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

public class CustomerResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostCustomerRes {
        private Long customerId;
        private String name;
        private String imgUrl;
        private List<KeywordResponseDTO.KeywordDto> keywordList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditCustomerRes {
        private Long customerId;
        private String name;
        private String imgUrl;
        private List<KeywordResponseDTO.KeywordDto> keywordList;
        private String createdAt;
        private String updatedAt;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetCustomerRes {
        private Long customerId;
        private String name;
        private String imgUrl;
        private List<KeywordResponseDTO.KeywordDto> keywordList;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetCustomerDetailRes {
        private Long customerId;
        private String name;
        private String imgUrl;
        private String email;
        private Long visitedCafeCount;
        private Long totalStampCount;
        private Long totalUsedStampCount;
        private LocalDateTime createdAt;
        private List<KeywordResponseDTO.KeywordDto> keywordList;
    }

    @SuperBuilder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchCustomerRes extends CommonPageRes {
        private List<GetCustomerRes> customerList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetQrcodeRes {
        private String qrCodeBase64;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetPendingReviewRes {
        private Long stampLogId;
        private Long cafeId;
        private String cafeName;
        private String date;
    }

    @SuperBuilder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchStampBoardsRes extends CommonPageRes {
        private List<StampResponseDTO.GetStampBoardRes> stampBoardList;
    }
}
