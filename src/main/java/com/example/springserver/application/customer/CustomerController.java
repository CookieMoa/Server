package com.example.springserver.application.customer;

import com.example.springserver.domain.auth.service.AuthorizationService;
import com.example.springserver.domain.cafe.dto.CafeResponseDTO;
import com.example.springserver.domain.customer.converter.CustomerConverter;
import com.example.springserver.domain.user.service.UserService;
import com.example.springserver.entity.Customer;
import com.example.springserver.entity.StampBoard;
import com.example.springserver.global.common.paging.CommonPageReq;
import com.example.springserver.global.security.CustomUserDetails;
import com.example.springserver.domain.customer.dto.CustomerRequestDTO;
import com.example.springserver.domain.customer.service.CustomerService;
import com.example.springserver.domain.customer.dto.CustomerResponseDTO;
import com.example.springserver.global.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "소비자 API")
@RequestMapping("/customers")
public class CustomerController {

    private final AuthorizationService authorizationService;
    private final CustomerService customerService;
    private final UserService userService;

    @Operation(summary = "소비자 등록")
    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<CustomerResponseDTO.PostCustomerRes> postCustomer(
            @RequestPart("data") @Valid CustomerRequestDTO.PostCustomerReq request,
            @RequestPart(value = "profileImg", required = false) MultipartFile profileImg) {

        return ApiResponse.onSuccess(customerService.postCustomer(request, profileImg));
    }

    @Operation(summary = "소비자 정보 수정")
    @PutMapping(value = "/{customerId}", consumes = "multipart/form-data")
    public ApiResponse<CustomerResponseDTO.EditCustomerRes> editCustomer(
            @AuthenticationPrincipal CustomUserDetails userDetail,
            @RequestPart("data") @Valid CustomerRequestDTO.EditCustomerReq request,
            @RequestPart(value = "profileImg", required = false) MultipartFile profileImg,
            @PathVariable Long customerId) {

        // 본인인지 검사
        authorizationService.validateUserAuthorization(userDetail.getUsername(), customerId);

        return ApiResponse.onSuccess(customerService.editCustomer(request, profileImg, customerId));
    }

    @Operation(summary = "소비자 조회")
    @GetMapping("/{customerId}")
    public ApiResponse<CustomerResponseDTO.GetCustomerRes> getCustomer(@AuthenticationPrincipal CustomUserDetails userDetail,
                                                                        @PathVariable Long customerId) {

        return ApiResponse.onSuccess(customerService.getCustomer(customerId));
    }

    @Operation(summary = "작성 가능 리뷰 목록 조회")
    @GetMapping("/{customerId}/pending-review")
    public ApiResponse<List<CustomerResponseDTO.GetPendingReviewRes>> searchPendingReview(
            @AuthenticationPrincipal CustomUserDetails userDetail,
            @PathVariable Long customerId) {

        // 본인인지 검사
        authorizationService.validateUserAuthorization(userDetail.getUsername(), customerId);

        return ApiResponse.onSuccess(customerService.searchPendingReview(customerId));
    }

    @Operation(summary = "소비자 검색")
    @GetMapping("/search")
    public ApiResponse<CustomerResponseDTO.SearchCustomerRes> searchCustomer(@AuthenticationPrincipal CustomUserDetails userDetail,
                                                                             @ModelAttribute @Valid CommonPageReq pageRequest,
                                                                             @RequestParam String query) {

        Page<Customer> customerList = customerService.searchCustomer(pageRequest, query);
        return ApiResponse.onSuccess(CustomerConverter.toSearchCustomerRes(customerList));
    }

    @Operation(summary = "소비자 qr 요청")
    @GetMapping("/{customerId}/qr-code")
    public ApiResponse<CustomerResponseDTO.GetQrcodeRes> getQrcode(@AuthenticationPrincipal CustomUserDetails userDetail,
                                                                        @PathVariable Long customerId) {
        // 본인인지 검사
        authorizationService.validateUserAuthorization(userDetail.getUsername(), customerId);
        String qrcode = customerService.getQrcode(customerId);
        return ApiResponse.onSuccess(CustomerConverter.toGetQrcodeRes(qrcode));
    }

    @Operation(summary = "소비자 스탬프 보드 조회")
    @GetMapping("/{customerId}/stamp-boards")
    public ApiResponse<CustomerResponseDTO.SearchStampBoardsRes> searchStampBoards(@AuthenticationPrincipal CustomUserDetails userDetail,
                                                                                   @PathVariable Long customerId,
                                                                             @ModelAttribute @Valid CommonPageReq pageRequest) {

        // 본인인지 검사
        authorizationService.validateUserAuthorization(userDetail.getUsername(), customerId);

        Page<StampBoard> stampBoardList = customerService.searchStampBoards(customerId, pageRequest);
        return ApiResponse.onSuccess(CustomerConverter.toSearchStampBoardsRes(stampBoardList));
    }

    @Operation(summary = "소비자 작성 리뷰 검색")
    @GetMapping(value = "/{customerId}/reviews")
    public ApiResponse<CustomerResponseDTO.SearchCustomerReviewsRes> searchCustomerReviews(
            @AuthenticationPrincipal CustomUserDetails userDetail,
            @ModelAttribute @Valid CommonPageReq pageRequest,
            @PathVariable("customerId") Long customerId) {

        return ApiResponse.onSuccess(customerService.searchCustomerReviews(pageRequest, customerId));
    }

    @Operation(summary = "탈퇴")
    @DeleteMapping
    public ApiResponse<Void> deleteAccount(
            @AuthenticationPrincipal CustomUserDetails userDetail) {
        userService.deleteAccount(userDetail.getUsername());
        return ApiResponse.onSuccess(null);
    }
}
