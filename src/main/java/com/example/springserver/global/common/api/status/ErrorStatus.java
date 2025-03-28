package com.example.securitywithredis.global.common.api.status;

import com.example.securitywithredis.global.common.api.BaseCode;
import com.example.securitywithredis.global.common.api.ResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),


    // 멤버 관려 에러
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),
    MEMBER_IS_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4002", "사용자가 이미 존재합니다.."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4003", "닉네임은 필수 입니다."),

    // 토큰 관련 에러
    ACCESS_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "TOKEN4001", "액세스 토큰이 만료되었습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "TOKEN4002", "리프레시 토큰이 만료되었습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN4003", "토큰이 올바르지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN4004", "토큰이 올바르지 않습니다."),
    REFRESH_TOKEN_NOT_EXIST(HttpStatus.BAD_REQUEST, "TOKEN4005", "리프레시 토큰이 존재하지 않습니다."),
    REFRESH_TOKEN_IS_NULL(HttpStatus.BAD_REQUEST, "TOKEN4006", "리프레시 토큰이 null입니다."),


    // 예시,,,
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "ARTICLE4001", "게시글이 없습니다."),

    // For test
    TEMP_EXCEPTION(HttpStatus.BAD_REQUEST, "TEMP4001", "이거는 테스트"),

    // 페이징 에러
    PAGE_NOT_EXIST(HttpStatus.BAD_REQUEST, "PAGE001", "페이지가 0 이하입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ResponseDTO getReason() {
        return ResponseDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ResponseDTO getReasonHttpStatus() {
        return ResponseDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}