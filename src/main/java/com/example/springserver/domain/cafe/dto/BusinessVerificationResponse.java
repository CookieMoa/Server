package com.example.springserver.domain.cafe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BusinessVerificationResponse {
    private boolean success;
    private String reason;
}
