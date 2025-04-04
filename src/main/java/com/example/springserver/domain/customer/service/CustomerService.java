package com.example.springserver.domain.customer.service;


import com.example.springserver.domain.customer.converter.CustomerConverter;
import com.example.springserver.domain.customer.dto.CustomerRequestDTO;
import com.example.springserver.domain.customer.dto.CustomerResponseDTO;
import com.example.springserver.domain.customer.repository.CustomerRepository;
import com.example.springserver.domain.keyword.service.KeywordService;
import com.example.springserver.domain.user.service.UserService;
import com.example.springserver.entity.Customer;
import com.example.springserver.entity.Keyword;
import com.example.springserver.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final KeywordService keywordService;
    private final UserService userService;

    public CustomerResponseDTO.PostCustomerRes postCustomer(CustomerRequestDTO.PostCustomerReq request) {
        UserEntity user = userService.getUserById(request.getId());

        // todo: 프로필 이미지 업로드 (MultipartFile 사용) (S3)
        String imgUrl = null;
//        if (request.getProfileImg() != null && !request.getProfileImg().isEmpty()) {
//
//        }

        Customer newCustomer = CustomerConverter.toCustomer(request, user, imgUrl);
        customerRepository.save(newCustomer);

        // 키워드 조회 및 매핑
        List<Keyword> keywords = keywordService.getKeywordsByIds(request.getKeywordList());
        keywordService.createCustomerKeywordMappings(newCustomer, keywords);

        return CustomerConverter.toPostCustomerRes(newCustomer, keywords);
    }
}
