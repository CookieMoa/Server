package com.example.springserver.domain.customer.converter;

import com.example.springserver.domain.customer.dto.CustomerRequestDTO;
import com.example.springserver.domain.customer.dto.CustomerResponseDTO;
import com.example.springserver.domain.keyword.converter.KeywordConverter;
import com.example.springserver.domain.keyword.dto.KeywordResponseDTO;
import com.example.springserver.entity.Customer;
import com.example.springserver.entity.Keyword;
import com.example.springserver.entity.UserEntity;

import java.util.List;

public class CustomerConverter {

    public static Customer toCustomer(CustomerRequestDTO.PostCustomerReq request, UserEntity user, String imgUrl){

        return Customer.builder()
                .user(user)
                .name(request.getName())
                .imgUrl(imgUrl)
                .build();
    }

    public static CustomerResponseDTO.PostCustomerRes toPostCustomerRes(Customer customer, List<Keyword> keywords){
        List<KeywordResponseDTO.KeywordDto> keywordDtoList = keywords.stream()
                .map(KeywordConverter::toKeywordDto).toList();

        return CustomerResponseDTO.PostCustomerRes.builder()
                .customerId(customer.getId())
                .name(customer.getName())
                .imgUrl(customer.getImgUrl())
                .keywordList(keywordDtoList)
                .build();
    }
}
