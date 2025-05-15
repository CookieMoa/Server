package com.example.springserver.domain.admin.dto;

import com.example.springserver.domain.ai.dto.AiResponseDTO;
import com.example.springserver.domain.cafe.dto.CafeResponseDTO;
import com.example.springserver.domain.customer.dto.CustomerResponseDTO;
import com.example.springserver.domain.keyword.dto.KeywordResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class AdminResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetDashboardRes {
        private Long customerCount;
        private Long cafeCount;
        private Long issuedCouponCount;
        private Long usedCouponCount;
        private Long couponUsageRate;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetRecentUserRes {
        private List<CustomerResponseDTO.GetCustomerRes> userList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetRecentCafeRes {
        private List<CafeResponseDTO.GetCafeRes> cafeList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StampTransactionDto {
        private Long hour;
        private Long count;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetStampTransactionsRes {
        private List<StampTransactionDto> stampTransactionList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CafeDTO {
        private Long hour;
        private Long count;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetCafeListRes {
        private List<CafeResponseDTO.GetMyCafeRes> cafeList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetUserListRes {
        private List<CustomerResponseDTO.GetCustomerDetailRes> userList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetReviewCountRes {
        private List<AdminResponseDTO.reviewCountDTO> reviewCountList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class reviewCountDTO {
        private String name;
        private Long count;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class maliciousReviewDTO {
        private Long userId;
        private String userName;
        private String review;
        private LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetMaliciousReviewRes {
        private List<maliciousReviewDTO> maliciousReviewList;
    }

}
