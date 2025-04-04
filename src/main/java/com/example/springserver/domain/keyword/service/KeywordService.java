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

    public List<Keyword> getKeywordsByIds(List<Long> keywordIds) {
        return keywordRepository.findAllById(keywordIds);
    }

    @Transactional
    public void createCustomerKeywordMappings(Customer customer, List<Keyword> keywords) {
        List<KeywordMapping> mappings = KeywordConverter.toCustomerKeywordMappings(customer, keywords);
        keywordMappingRepository.saveAll(mappings);
    }

    @Transactional
    public void createCafeKeywordMappings(Cafe cafe, List<Keyword> keywords) {
        List<KeywordMapping> mappings = KeywordConverter.toCafeKeywordMappings(cafe, keywords);
        keywordMappingRepository.saveAll(mappings);
    }

    @Transactional
    public void createReviewKeywordMappings(Review review, List<Keyword> keywords) {
        List<KeywordMapping> mappings = KeywordConverter.toReviewKeywordMappings(review, keywords);
        keywordMappingRepository.saveAll(mappings);
    }
}
