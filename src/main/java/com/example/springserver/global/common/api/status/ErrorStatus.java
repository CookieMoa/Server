package com.example.springserver.global.common.api.status;

import com.example.springserver.global.common.api.BaseCode;
import com.example.springserver.global.common.api.ResponseDTO;
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

    // QR code 생성
    GENERATE_QR_FAILED(HttpStatus.INTERNAL_SERVER_ERROR,"QR5001","QR code 생성 실패."),

    // 로그인 관련
    LOGIN_FAILED(HttpStatus.BAD_REQUEST,"LOGIN400","로그인 실패."),

    // 이미지 에러
    INVALID_IMAGE_FORMAT(HttpStatus.BAD_REQUEST, "IMAGE4001", "유효하지 않은 이미지 형식입니다."),
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE5002", "이미지 업로드에 실패했습니다."),
    INVALID_IMAGE(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE4002", "이미지가 존재하지 않습니다."),

    // 멤버 관려 에러
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),
    MEMBER_IS_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4002", "사용자가 이미 존재합니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4003", "닉네임은 필수 입니다."),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "MEMBER4004", "역할이 올바르지 않습니다. 기대: (ROLE_CAFE || ROLE_CUSTOMER)"),
    SIGNUP_IN_PROGRESS_CAFE(HttpStatus.BAD_REQUEST, "MEMBER4005", "CAFE로 회원가입 중입니다."),
    SIGNUP_IN_PROGRESS_CUSTOMER(HttpStatus.BAD_REQUEST, "MEMBER4006", "CUSTOMER로 회원가입 중입니다."),

    // 토큰 관련 에러
    ACCESS_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "TOKEN4001", "액세스 토큰이 만료되었습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "TOKEN4002", "리프레시 토큰이 만료되었습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN4003", "토큰이 올바르지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN4004", "토큰이 올바르지 않습니다."),
    REFRESH_TOKEN_NOT_EXIST(HttpStatus.BAD_REQUEST, "TOKEN4005", "리프레시 토큰이 존재하지 않습니다."),
    REFRESH_TOKEN_IS_NULL(HttpStatus.BAD_REQUEST, "TOKEN4006", "리프레시 토큰이 null입니다."),
    FORBIDDEN_USER_ACCESS(HttpStatus.BAD_REQUEST, "TOKEN4008", "접근 권한이 없는 사용자입니다"),

    EMAIL_SEND_FAILED(HttpStatus.BAD_REQUEST, "EMAIL4001", "이메일 전송 실패"),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "EMAIL4002", "이메일을 찾을 수 없습니다."),

    // 예시,,,
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "ARTICLE4001", "게시글이 없습니다."),

    // For test
    TEMP_EXCEPTION(HttpStatus.BAD_REQUEST, "TEMP4001", "이거는 테스트"),

    // 페이징 에러
    PAGE_NOT_EXIST(HttpStatus.BAD_REQUEST, "PAGE001", "페이지가 0 이하입니다."),

    // 이메일 인증 코드 에러
    EMAIL_CODE_NOT_EXIST(HttpStatus.BAD_REQUEST, "EMAIL4001", "인증 코드가 만료되었거나 존재하지 않습니다."),
    INVALID_EMAIL_CODE(HttpStatus.BAD_REQUEST, "EMAIL4002", "이메일 인증 코드가 일치하지 않습니다."),
    INVALID_PURPOSE(HttpStatus.BAD_REQUEST, "EMAIL4003", "용도가 올바르지 않습니다."),

    // 이메일 인증 토큰 에러
    EMAIL_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "EMAIL4004", "이메일 인증 토큰이 만료되었습니다."),
    INVALID_EMAIL_TOKEN_CATEGORY(HttpStatus.BAD_REQUEST, "EMAIL4005", "이메일 토큰의 카테고리가 유효하지 않습니다."),
    EMAIL_TOKEN_MISMATCH(HttpStatus.BAD_REQUEST, "EMAIL4006", "이메일 인증 토큰이 요청한 이메일과 일치하지 않습니다."),

    // 키워드 관련 에러
    KEYWORD_NOT_FOUND(HttpStatus.BAD_REQUEST, "KEYWORD4004", "선택한 키워드가 존재하지 않습니다."),

    // 카페 관련 에러
    INVALID_CAFE_REWARD(HttpStatus.BAD_REQUEST, "REWARD4001", "해당 보상은 입력된 카페의 보상이 아닙니다."),
    REWARD_NOT_FOUND(HttpStatus.BAD_REQUEST, "REWARD4004", "해당 보상이 없습니다."),
    CAFE_LOCATION_MISSING(HttpStatus.BAD_REQUEST, "CAFE4004", "카페의 위치 정보(address, latitude, longitude)는 필수입니다."),

    // 스탬프 관련 에러
    STAMPBOARD_NOT_FOUND(HttpStatus.BAD_REQUEST, "STAMP4004", "해당 스탬프보드가 존재하지 않습니다."),
    NOT_ENOUGH_STAMPS(HttpStatus.BAD_REQUEST, "STAMP4005", "스탬프가 부족합니다."),

    // 로그 관련 에러
    STAMPLOG_NOT_FOUND(HttpStatus.BAD_REQUEST, "STAMPLOG4004", "해당 스탬프로그가 존재하지 않습니다."),

    // 정지 관련 에러
    SUSPENDED(HttpStatus.FORBIDDEN, "SUSPENDED4001", "해당 유저 또는 카페는 정지된 상태입니다.");

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
