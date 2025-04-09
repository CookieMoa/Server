package com.example.springserver.domain.customer.dto;

import com.example.springserver.domain.keyword.dto.KeywordResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
