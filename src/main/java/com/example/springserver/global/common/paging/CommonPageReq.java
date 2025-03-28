package com.example.securitywithredis.global.common.paging;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
public class CommonPageReq {
    @Min(1)
    private int size;

    @Min(1)
    private int page;

    public CommonPageReq() {
        this.size = 10; // 기본값 10
        this.page = 1;   // 기본값 1
    }

    // PageRequest 객체로 변환하는 메서드
    public Pageable toPageable() {
        return PageRequest.of(this.page - 1, this.size);
    }
}