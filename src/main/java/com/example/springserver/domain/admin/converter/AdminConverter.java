package com.example.springserver.domain.admin.converter;

import com.example.springserver.domain.admin.dto.AdminResponseDTO;

public class AdminConverter {
    public static AdminResponseDTO.GetDashboardRes toDashboardRes(Long customerCount, Long cafeCount, Long issuedCouponCount, Long usedCouponCount, Long couponUsageRate){
        return AdminResponseDTO.GetDashboardRes.builder()
                .customerCount(customerCount)
                .cafeCount(cafeCount)
                .issuedCouponCount(issuedCouponCount)
                .usedCouponCount(usedCouponCount)
                .couponUsageRate(couponUsageRate)
                .build();
    }
}
