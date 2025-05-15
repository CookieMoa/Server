package com.example.springserver.domain.cafe.converter;

import com.example.springserver.domain.cafe.dto.CafeRequestDTO;
import com.example.springserver.domain.cafe.dto.CafeResponseDTO;
import com.example.springserver.domain.customer.dto.CustomerResponseDTO;
import com.example.springserver.domain.keyword.converter.KeywordConverter;
import com.example.springserver.domain.keyword.dto.KeywordResponseDTO;
import com.example.springserver.entity.Cafe;
import com.example.springserver.entity.Customer;
import com.example.springserver.entity.Keyword;
import com.example.springserver.entity.Review;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReviewConverter {

    // 날짜를 포맷하는 메서드
    private static String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    // 시간을 포맷하는 메서드
    private static String formatTime(LocalTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(formatter);
    }

    public static Review toReview(CafeRequestDTO.PostReviewReq request, Cafe cafe, Customer customer, Boolean isMalicious){

        return Review.builder()
                .customer(customer)
                .cafe(cafe)
                .content(request.getContent())
                .name(customer.getName())
                .isMalicious(isMalicious)
                .build();
    }

    public static CafeResponseDTO.PostReviewRes toPostReviewRes(Review review, List<Keyword> keywords){
        List<KeywordResponseDTO.KeywordDto> keywordDtoList = keywords.stream()
                .map(KeywordConverter::toKeywordDto).toList();

        return CafeResponseDTO.PostReviewRes.builder()
                .reviewId(review.getId())
                .cafeId(review.getCafe().getId())
                .customerId(review.getCustomer().getId())
                .content(review.getContent())
                .name(review.getName())
                .keywordList(keywordDtoList)
                .createdAt(formatDateTime(review.getCreatedAt()))
                .updatedAt(formatDateTime(review.getUpdatedAt()))
                .build();
    }

    public static CafeResponseDTO.GetCafeReviewRes toGetCafeReviewRes(Review review, List<Keyword> keywords){
        List<KeywordResponseDTO.KeywordDto> keywordDtoList = keywords.stream()
                .map(KeywordConverter::toKeywordDto).toList();

        return CafeResponseDTO.GetCafeReviewRes.builder()
                .reviewId(review.getId())
                .cafeId(review.getCafe().getId())
                .customerId(review.getCustomer().getId())
                .customerName(review.getName())
                .customerImgUrl(review.getCustomer().getImgUrl())
                .content(review.getContent())
                .keywordList(keywordDtoList)
                .createdAt(formatDateTime(review.getCreatedAt()))
                .updatedAt(formatDateTime(review.getUpdatedAt()))
                .build();
    }

    public static CafeResponseDTO.SearchCafeReviewsRes toSearchCafeReviewsRes(Page<Review> reviewList, List<CafeResponseDTO.GetCafeReviewRes> getReviewResList) {

        return CafeResponseDTO.SearchCafeReviewsRes.builder()
                .reviewList(getReviewResList)
                .count(reviewList.getTotalElements())
                .limit(reviewList.getSize())
                .page(reviewList.getNumber())
                .build();
    }

    public static CustomerResponseDTO.GetCustomerReviewRes toGetCustomerReviewRes(Review review, List<Keyword> keywords){
        List<KeywordResponseDTO.KeywordDto> keywordDtoList = keywords.stream()
                .map(KeywordConverter::toKeywordDto).toList();

        return CustomerResponseDTO.GetCustomerReviewRes.builder()
                .reviewId(review.getId())
                .cafeId(review.getCafe().getId())
                .customerId(review.getCustomer().getId())
                .cafeName(review.getCafe().getName())
                .cafeImgUrl(review.getCafe().getImgUrl())
                .content(review.getContent())
                .keywordList(keywordDtoList)
                .createdAt(formatDateTime(review.getCreatedAt()))
                .updatedAt(formatDateTime(review.getUpdatedAt()))
                .build();
    }

    public static CustomerResponseDTO.SearchCustomerReviewsRes toSearchCustomerReviewsRes(Page<Review> reviewList, List<CustomerResponseDTO.GetCustomerReviewRes> getReviewResList) {

        return CustomerResponseDTO.SearchCustomerReviewsRes.builder()
                .reviewList(getReviewResList)
                .count(reviewList.getTotalElements())
                .limit(reviewList.getSize())
                .page(reviewList.getNumber())
                .build();
    }
}
