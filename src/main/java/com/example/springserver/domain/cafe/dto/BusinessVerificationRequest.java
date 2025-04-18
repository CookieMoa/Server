package com.example.springserver.domain.cafe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BusinessVerificationRequest {
    private List<BusinessInfo> businesses;

    @Getter
    @AllArgsConstructor
    public static class BusinessInfo {
        private String b_no;      // 사업자 번호
        private String start_dt;  // 개업일자 (yyyyMMdd)
        private String p_nm;      // 대표자명
    }
}
