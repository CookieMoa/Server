package com.example.springserver.domain.admin.dto;

import com.example.springserver.domain.cafe.dto.CafeResponseDTO;
import com.example.springserver.domain.customer.dto.CustomerResponseDTO;
import com.example.springserver.domain.keyword.dto.KeywordResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
