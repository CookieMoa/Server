package com.example.springserver.domain.keyword.converter;

import com.example.springserver.domain.keyword.dto.KeywordResponseDTO;
import com.example.springserver.entity.*;

import java.util.List;
import java.util.stream.Collectors;

public class KeywordConverter {

    // 키워드DTO 생성
    public static KeywordResponseDTO.KeywordDto toKeywordDto(Keyword keyword) {
        return KeywordResponseDTO.KeywordDto.builder()
                .keywordId(keyword.getId())
                .name(keyword.getName())
                .build();
    }


    // 키워드 매핑 생성 (소비자, 카페, 리뷰)
    public static List<KeywordMapping> toCustomerKeywordMappings(Customer customer, List<Keyword> keywords) {
        return keywords.stream()
                .map(keyword -> KeywordMapping.builder()
                        .customer(customer)
                        .keyword(keyword)
                        .build())
                .collect(Collectors.toList());
    }

    public static List<KeywordMapping> toCafeKeywordMappings(Cafe cafe, List<Keyword> keywords) {
        return keywords.stream()
                .map(keyword -> KeywordMapping.builder()
                        .cafe(cafe)
                        .keyword(keyword)
                        .build())
                .collect(Collectors.toList());
    }

    public static List<KeywordMapping> toReviewKeywordMappings(Review review, List<Keyword> keywords) {
        return keywords.stream()
                .map(keyword -> KeywordMapping.builder()
                        .review(review)
                        .keyword(keyword)
                        .build())
                .collect(Collectors.toList());
    }
}
