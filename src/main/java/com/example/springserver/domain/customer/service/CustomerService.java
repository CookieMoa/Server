package com.example.springserver.domain.customer.service;


import com.example.springserver.domain.customer.converter.CustomerConverter;
import com.example.springserver.domain.customer.dto.CustomerRequestDTO;
import com.example.springserver.domain.customer.dto.CustomerResponseDTO;
import com.example.springserver.domain.customer.repository.CustomerRepository;
import com.example.springserver.domain.keyword.service.KeywordService;
import com.example.springserver.domain.user.enums.AccountStatus;
import com.example.springserver.domain.user.service.UserService;
import com.example.springserver.entity.Customer;
import com.example.springserver.entity.Keyword;
import com.example.springserver.entity.UserEntity;
import com.example.springserver.global.common.api.status.ErrorStatus;
import com.example.springserver.global.common.paging.CommonPageReq;
import com.example.springserver.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final KeywordService keywordService;
    private final UserService userService;

    public Customer getCustomerByUserId(Long userId) {
        return customerRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
    }

    public CustomerResponseDTO.PostCustomerRes postCustomer(CustomerRequestDTO.PostCustomerReq request, MultipartFile profileImg) {
        UserEntity user = userService.getUserById(request.getId());

        // todo: 프로필 이미지 업로드 (MultipartFile 사용) (S3)
        String imgUrl = null;
//        if (request.getProfileImg() != null && !request.getProfileImg().isEmpty()) {
//
//        }

        Customer newCustomer = CustomerConverter.toCustomer(request, user, imgUrl);
        customerRepository.save(newCustomer);

        // 계정 상태 ACTIVE로 변경
        user.setAccountStatus(AccountStatus.ACTIVE);
        userService.saveUser(user);

        // 키워드 조회 및 매핑
        List<Keyword> keywords = keywordService.getKeywordsByIds(request.getKeywordList());
        if (keywords.isEmpty()) {
            throw new GeneralException(ErrorStatus.KEYWORD_NOT_FOUND);
        }
        keywordService.createCustomerKeywordMappings(newCustomer, keywords);

        return CustomerConverter.toPostCustomerRes(newCustomer, keywords);
    }

    public CustomerResponseDTO.GetCustomerRes getCustomer(Long userId) {

        Customer customer = getCustomerByUserId(userId);

        List<Keyword> keywords = keywordService.getKeywordsByCustomer(customer);

        return CustomerConverter.toGetCustomerRes(customer, keywords);
    }

    public Page<Customer> searchCustomer(CommonPageReq pageRequest, String query) {

        Pageable pageable = pageRequest.toPageable();

        Page<Customer> customers = customerRepository.findByNameStartingWith(query, pageable);

        if (customers.isEmpty()) {
            throw new GeneralException(ErrorStatus.MEMBER_NOT_FOUND);
        }

        return customers;
    }
}
