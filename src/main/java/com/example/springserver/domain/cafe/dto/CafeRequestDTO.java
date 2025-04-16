package com.example.springserver.domain.cafe.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class CafeRequestDTO {

    @Getter
    @Setter
    public static class PostCafeReq {

        @NotNull(message = "id는 필수입니다.")
        private Long id;

        @NotEmpty
        private String name;
        @NotEmpty
        private String address;
        @NotEmpty
        private Double latitude;
        @NotEmpty
        private Double longitude;

        private String contact;
        private String intro;
    }
}