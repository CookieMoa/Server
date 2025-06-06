package com.example.springserver.domain.admin.converter;

import com.example.springserver.domain.admin.dto.AdminResponseDTO;
import com.example.springserver.domain.admin.enums.Cycle;
import com.example.springserver.domain.cafe.dto.CafeResponseDTO;
import com.example.springserver.domain.customer.dto.CustomerResponseDTO;
import com.example.springserver.entity.Review;

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

    public static AdminResponseDTO.GetStampTransactionsRes toStampTransactionsRes(List<AdminResponseDTO.StampTransactionDto> stampTransactionList) {
        return AdminResponseDTO.GetStampTransactionsRes.builder()
                .stampTransactionList(stampTransactionList)
                .build();
    }

    public static AdminResponseDTO.StampTransactionDto toStampTransactionDTO(Long hour, Long count) {
        return AdminResponseDTO.StampTransactionDto.builder()
                .hour(hour)
                .count(count)
                .build();
    }

    public static AdminResponseDTO.GetCafeListRes toCafeListRes(List<CafeResponseDTO.GetMyCafeRes> cafeList) {
        return AdminResponseDTO.GetCafeListRes.builder()
                .cafeList(cafeList)
                .build();
    }

    public static AdminResponseDTO.GetUserListRes toUserListRes(List<CustomerResponseDTO.GetCustomerDetailRes> userList) {
        return AdminResponseDTO.GetUserListRes.builder()
                .userList(userList)
                .build();
    }

    public static AdminResponseDTO.maliciousReviewDTO toMaliciousReviewDTO(Review review) {
        return AdminResponseDTO.maliciousReviewDTO.builder()
                .review(review.getContent())
                .createdAt(review.getCreatedAt())
                .userId(review.getCustomer().getId())
                .userName(review.getCustomer().getName())
                .build();
    }

    public static AdminResponseDTO.GetMaliciousReviewRes toMaliciousReviewRes(List<AdminResponseDTO.maliciousReviewDTO> maliciousReviewDTOList) {
        return AdminResponseDTO.GetMaliciousReviewRes.builder()
                .maliciousReviewList(maliciousReviewDTOList)
                .build();
    }

    public static AdminResponseDTO.GetSettingRes toSettingRes(Boolean isModelLearning, Cycle modelLearningCycle, Boolean isKeywordAnalysis, Cycle keywordAnalysisCycle, Boolean isBlockRepeatedAbuse, Integer abuseThreshold, Boolean isBlockMaliciousUser, Integer maliciousThreshold, Boolean isDetectMaliciousReview) {
        return AdminResponseDTO.GetSettingRes.builder()
                .isModelLearning(isModelLearning)
                .modelLearningCycle(modelLearningCycle)
                .isKeywordAnalysis(isKeywordAnalysis)
                .keywordAnalysisCycle(keywordAnalysisCycle)
                .isBlockRepeatedAbuser(isBlockRepeatedAbuse)
                .abuseThreshold(abuseThreshold)
                .isBlockMaliciousUser(isBlockMaliciousUser)
                .maliciousThreshold(maliciousThreshold)
                .isDetectMaliciousReview(isDetectMaliciousReview)
                .build();
    }
}
