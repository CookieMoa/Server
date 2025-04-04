package com.example.springserver.domain.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class CustomerRequestDTO {

    @Getter
    public static class PostCustomerReq {

        @NotEmpty
        private Long id;

        @NotEmpty
        private String name;

        private MultipartFile profileImg;

        private List<Long> keywordList;
    }
}