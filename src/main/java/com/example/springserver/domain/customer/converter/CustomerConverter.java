package com.example.springserver.domain.customer.converter;

import com.example.springserver.domain.customer.dto.CustomerRequestDTO;
import com.example.springserver.domain.customer.dto.CustomerResponseDTO;
import com.example.springserver.domain.keyword.converter.KeywordConverter;
import com.example.springserver.domain.keyword.dto.KeywordResponseDTO;
import com.example.springserver.entity.Customer;
import com.example.springserver.entity.Keyword;
import com.example.springserver.entity.UserEntity;
import com.example.springserver.global.common.paging.CommonPageRes;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerConverter {

    // 날짜를 포맷하는 메서드
    private static String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

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

    public static CustomerResponseDTO.EditCustomerRes toEditCustomerRes(
            Customer customer,
            boolean isNameUpdated,
            boolean isImgUpdated,
            boolean isKeywordUpdated,
            List<Keyword> keywords
    ) {
        return CustomerResponseDTO.EditCustomerRes.builder()
                .customerId(customer.getId())
                .name(isNameUpdated ? customer.getName() : null)
                .imgUrl(isImgUpdated ? customer.getImgUrl() : null)
                .keywordList(isKeywordUpdated && keywords != null
                        ? keywords.stream().map(KeywordConverter::toKeywordDto).toList()
                        : null)
                .createdAt(formatDateTime(customer.getCreatedAt()))
                .updatedAt(formatDateTime(customer.getUpdatedAt()))
                .build();
    }

    public static CustomerResponseDTO.GetCustomerRes toGetCustomerRes(Customer customer, List<Keyword> keywords){
        List<KeywordResponseDTO.KeywordDto> keywordDtoList = keywords.stream()
                .map(KeywordConverter::toKeywordDto).toList();

        return CustomerResponseDTO.GetCustomerRes.builder()
                .customerId(customer.getId())
                .name(customer.getName())
                .imgUrl(customer.getImgUrl())
                .keywordList(keywordDtoList)
                .build();
    }

    public static CustomerResponseDTO.GetCustomerRes toSimpleGetCustomerRes(Customer customer) {
        return CustomerResponseDTO.GetCustomerRes.builder()
                .customerId(customer.getId())
                .name(customer.getName())
                .build();
    }

    public static CustomerResponseDTO.SearchCustomerRes toSearchCustomerRes(Page<Customer> customerList) {

        List<CustomerResponseDTO.GetCustomerRes> getSimpleCustomerResList = customerList.stream()
                .map(CustomerConverter::toSimpleGetCustomerRes).toList();

        CommonPageRes commonPageRes = new CommonPageRes(
                customerList.getTotalElements(),   // 총 개수 (count)
                customerList.getSize(),           // 페이지 당 개수 (limit)
                customerList.getNumber()          // 현재 페이지 번호 (page)
        );

        return CustomerResponseDTO.SearchCustomerRes.builder()

                .customerList(getSimpleCustomerResList)
                .count(commonPageRes.getCount())
                .limit(commonPageRes.getLimit())
                .page(commonPageRes.getPage())
                .build();
    }
}
