package com.example.springserver.global.common.paging;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@Setter
public class CommonPageReq {
    @Min(1)
    private int size;

    @Min(1)
    private int page;

    public CommonPageReq() {
        this.size = 20; // 기본값 20
        this.page = 1;   // 기본값 1
    }

    // PageRequest 객체로 변환하는 메서드
    public Pageable toPageable() {
        return PageRequest.of(this.page - 1, this.size, Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}