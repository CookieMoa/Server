package com.example.securitywithredis.global.common.paging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommonPageRes {

    private Long count; // 총 개수
    private Integer limit; // 페이지 당 개수
    private Integer page; // 현재 페이지 번호
}