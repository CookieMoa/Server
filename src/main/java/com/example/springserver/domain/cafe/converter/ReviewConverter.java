package com.example.springserver.domain.cafe.converter;

import com.example.springserver.domain.cafe.dto.CafeRequestDTO;
import com.example.springserver.domain.cafe.dto.CafeResponseDTO;
import com.example.springserver.domain.customer.converter.CustomerConverter;
import com.example.springserver.domain.customer.dto.CustomerResponseDTO;
import com.example.springserver.domain.keyword.converter.KeywordConverter;
import com.example.springserver.domain.keyword.dto.KeywordResponseDTO;
import com.example.springserver.entity.Cafe;
import com.example.springserver.entity.Customer;
import com.example.springserver.entity.Keyword;
import com.example.springserver.entity.Review;
import com.example.springserver.global.common.paging.CommonPageRes;
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

    public static Review toReview(CafeRequestDTO.PostReviewReq request, Cafe cafe, Customer customer){

        return Review.builder()
                .customer(customer)
                .cafe(cafe)
                .content(request.getContent())
                .name(customer.getName())
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

    public static CafeResponseDTO.GetReviewRes toGetReviewRes(Review review, List<Keyword> keywords){
        List<KeywordResponseDTO.KeywordDto> keywordDtoList = keywords.stream()
                .map(KeywordConverter::toKeywordDto).toList();

        return CafeResponseDTO.GetReviewRes.builder()
                .reviewId(review.getId())
                .cafeId(review.getCafe().getId())
                .customerId(review.getCustomer().getId())
                .name(review.getName())
                .content(review.getContent())
                .keywordList(keywordDtoList)
                .createdAt(formatDateTime(review.getCreatedAt()))
                .updatedAt(formatDateTime(review.getUpdatedAt()))
                .build();
    }

    public static CafeResponseDTO.SearchCafeReviewsRes toSearchCafeReviewsRes(Page<Review> reviewList, List<CafeResponseDTO.GetReviewRes> getReviewResList) {

        return CafeResponseDTO.SearchCafeReviewsRes.builder()
                .reviewList(getReviewResList)
                .count(reviewList.getTotalElements())
                .limit(reviewList.getSize())
                .page(reviewList.getNumber())
                .build();
    }
}
