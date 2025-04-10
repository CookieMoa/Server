package com.example.springserver.domain.customer.dto;

import com.example.springserver.domain.keyword.dto.KeywordResponseDTO;
import com.example.springserver.global.common.paging.CommonPageRes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

public class CustomerResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostCustomerRes {
        private Long customerId;
        private String name;
        private String imgUrl;
        private List<KeywordResponseDTO.KeywordDto> keywordList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetCustomerRes {
        private Long customerId;
        private String name;
        private String imgUrl;
        private List<KeywordResponseDTO.KeywordDto> keywordList;
    }

    @SuperBuilder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchCustomerRes extends CommonPageRes {
        private List<GetCustomerRes> customerList;
    }
}
