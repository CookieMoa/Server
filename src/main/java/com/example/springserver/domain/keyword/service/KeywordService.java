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

import java.util.List;
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
}
