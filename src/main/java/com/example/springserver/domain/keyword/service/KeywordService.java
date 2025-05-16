package com.example.springserver.domain.keyword.service;


import com.example.springserver.domain.customer.converter.CustomerConverter;
import com.example.springserver.domain.customer.dto.CustomerRequestDTO;
import com.example.springserver.domain.customer.dto.CustomerResponseDTO;
import com.example.springserver.domain.keyword.converter.KeywordConverter;
import com.example.springserver.domain.keyword.repository.KeywordMappingRepository;
import com.example.springserver.domain.keyword.repository.KeywordRepository;
import com.example.springserver.domain.user.service.UserService;
import com.example.springserver.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;
    private final KeywordMappingRepository keywordMappingRepository;

    public List<Keyword> getKeywordsByNames(List<String> keywordNames) {
        return keywordRepository.findAllByNameIn(keywordNames);
    }

    public void createCustomerKeywordMappings(Customer customer, List<Keyword> keywords) {
        List<KeywordMapping> mappings = KeywordConverter.toCustomerKeywordMappings(customer, keywords);
        keywordMappingRepository.saveAll(mappings);
    }

    public void createCafeKeywordMappings(Cafe cafe, List<Keyword> keywords) {
        List<KeywordMapping> mappings = KeywordConverter.toCafeKeywordMappings(cafe, keywords);
        keywordMappingRepository.saveAll(mappings);
    }

    public void createReviewKeywordMappings(Review review, List<Keyword> keywords) {
        List<KeywordMapping> mappings = KeywordConverter.toReviewKeywordMappings(review, keywords);
        keywordMappingRepository.saveAll(mappings);
    }

    public void updateCafeKeywordMappings(Cafe cafe, List<Keyword> keywords) {
        keywordMappingRepository.deleteByCafe(cafe);

        List<KeywordMapping> mappings = KeywordConverter.toCafeKeywordMappings(cafe, keywords);
        keywordMappingRepository.saveAll(mappings);
    }

    public void updateCustomerKeywordMappings(Customer customer, List<Keyword> keywords) {
        // 기존 매핑 삭제
        keywordMappingRepository.deleteByCustomer(customer);

        // 새로운 매핑 생성
        List<KeywordMapping> mappings = KeywordConverter.toCustomerKeywordMappings(customer, keywords);
        keywordMappingRepository.saveAll(mappings);
    }

    public List<Keyword> getKeywordsByCustomer(Customer customer) {
        List<KeywordMapping> mappings = keywordMappingRepository.findAllByCustomer(customer);
        return mappings.stream()
                .map(KeywordMapping::getKeyword)
                .collect(Collectors.toList());
    }

    public List<Keyword> getKeywordsByCafe(Cafe cafe) {
        List<KeywordMapping> mappings = keywordMappingRepository.findAllByCafe(cafe);
        return mappings.stream()
                .map(KeywordMapping::getKeyword)
                .collect(Collectors.toList());
    }

    public List<Keyword> getKeywordsByReview(Review review) {
        List<KeywordMapping> mappings = keywordMappingRepository.findAllByReview(review);
        return mappings.stream()
                .map(KeywordMapping::getKeyword)
                .collect(Collectors.toList());
    }

    public List<Cafe> getCafesByKeywordNames(List<String> keywordNames) {
        // 1. 이름으로 키워드 엔티티 조회
        List<Keyword> keywords = keywordRepository.findAllByNameIn(keywordNames);
        if (keywords.isEmpty()) return Collections.emptyList();

        // 2. 키워드 ID로 매핑 조회 (cafe_id가 존재하는 것만)
        List<KeywordMapping> mappings = keywordMappingRepository.findAllByKeywordInAndCafeIsNotNull(keywords);

        // 3. 카페 ID로 그룹핑해서 겹치는 키워드 수 기준으로 정렬
        Map<Cafe, Long> cafeCountMap = mappings.stream()
                .collect(Collectors.groupingBy(KeywordMapping::getCafe, Collectors.counting()));

        return cafeCountMap.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())) // 많이 겹치는 순
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
