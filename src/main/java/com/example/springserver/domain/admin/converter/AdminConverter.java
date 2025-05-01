package com.example.springserver.domain.admin.converter;

import com.example.springserver.domain.admin.dto.AdminResponseDTO;
import com.example.springserver.domain.cafe.dto.CafeResponseDTO;
import com.example.springserver.domain.customer.dto.CustomerResponseDTO;

import java.util.List;

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

    public static AdminResponseDTO.GetRecentUserRes toRecentUserRes(List<CustomerResponseDTO.GetCustomerRes> userList){
        return AdminResponseDTO.GetRecentUserRes.builder()
                .userList(userList)
                .build();
    }

    public static AdminResponseDTO.GetRecentCafeRes toRecentCafeRes(List<CafeResponseDTO.GetCafeRes> cafeList){
        return AdminResponseDTO.GetRecentCafeRes.builder()
                .cafeList(cafeList)
                .build();
    }
}
