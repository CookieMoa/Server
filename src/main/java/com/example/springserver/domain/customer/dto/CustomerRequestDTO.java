package com.example.springserver.domain.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class CustomerRequestDTO {

    @Getter
    @Setter
    public static class PostCustomerReq {

        @NotNull(message = "id는 필수입니다.")
        private Long id;

        @NotEmpty
        private String name;

        private List<Long> keywordList;
    }
}