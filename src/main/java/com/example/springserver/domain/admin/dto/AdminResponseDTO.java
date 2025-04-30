package com.example.springserver.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
