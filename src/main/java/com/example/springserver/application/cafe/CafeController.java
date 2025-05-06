package com.example.springserver.application.cafe;

import com.example.springserver.domain.auth.service.AuthorizationService;
import com.example.springserver.domain.cafe.converter.ReviewConverter;
import com.example.springserver.domain.cafe.dto.BusinessVerificationResponse;
import com.example.springserver.domain.cafe.dto.CafeRequestDTO;
import com.example.springserver.domain.cafe.dto.CafeResponseDTO;
import com.example.springserver.domain.cafe.service.CafeService;
import com.example.springserver.domain.cafe.service.VerifyBusinessService;
import com.example.springserver.domain.user.service.UserService;
import com.example.springserver.entity.Customer;
import com.example.springserver.entity.Review;
import com.example.springserver.global.common.api.ApiResponse;
import com.example.springserver.global.common.paging.CommonPageReq;
import com.example.springserver.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "카페 API")
@RequestMapping("/cafes")
public class CafeController {

    private final AuthorizationService authorizationService;
    private final VerifyBusinessService verificationService;
    private final CafeService cafeService;
    private final UserService userService;

    @Operation(summary = "카페 등록")
    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<CafeResponseDTO.PostCafeRes> postCafe(
            @RequestPart("data") @Valid CafeRequestDTO.PostCafeReq request,
            @RequestPart(value = "profileImg", required = false) MultipartFile profileImg) {

        return ApiResponse.onSuccess(cafeService.postCafe(request, profileImg));
    }

    @Operation(summary = "카페 정보 조회")
    @GetMapping("/{cafeId}")
    public ApiResponse<CafeResponseDTO.GetCafeRes> getCafe(
            @AuthenticationPrincipal CustomUserDetails userDetail,
            @PathVariable("cafeId") Long cafeId) {

        return ApiResponse.onSuccess(cafeService.getCafe(cafeId));
    }

    @Operation(summary = "본인 카페 정보 조회")
    @GetMapping("/my")
    public ApiResponse<CafeResponseDTO.GetMyCafeRes> getMyCafe(
            @AuthenticationPrincipal CustomUserDetails userDetail) {

        return ApiResponse.onSuccess(cafeService.getMyCafe(userDetail));
    }

    @Operation(summary = "카페 정보 수정")
    @PutMapping(value = "/{cafeId}", consumes = "multipart/form-data")
    public ApiResponse<CafeResponseDTO.EditCafeRes> editCafe(
            @AuthenticationPrincipal CustomUserDetails userDetail,
            @RequestPart("data") @Valid CafeRequestDTO.EditCafeReq request,
            @RequestPart(value = "profileImg", required = false) MultipartFile profileImg,
            @PathVariable("cafeId") Long cafeId) {

        // 본인인지 검사
        authorizationService.validateUserAuthorization(userDetail.getUsername(), cafeId);

        return ApiResponse.onSuccess(cafeService.editCafe(request, profileImg, cafeId));
    }

    @Operation(summary = "카페 사업자 등록 인증")
    @PostMapping("/verify-business")
    public ApiResponse<?> verifyBusiness(
            @RequestBody @Valid CafeRequestDTO.VerifyBusinessReq request) {
        BusinessVerificationResponse result = verificationService.verifyBusiness(request);
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "카페 광고 등록")
    @PostMapping(value = "/{cafeId}/adv", consumes = "multipart/form-data")
    public ApiResponse<CafeResponseDTO.PostCafeAdvRes> postCafeAdv(
            @AuthenticationPrincipal CustomUserDetails userDetail,
            @RequestPart(value = "advImg") MultipartFile advImg,
            @PathVariable("cafeId") Long cafeId) {

        // 본인인지 검사
        authorizationService.validateUserAuthorization(userDetail.getUsername(), cafeId);

        return ApiResponse.onSuccess(cafeService.postCafeAdv(advImg, cafeId));
    }

    @Operation(summary = "카페 스탬프 보상 등록")
    @PostMapping("/{cafeId}/rewards")
    public ApiResponse<CafeResponseDTO.PostStampRewardRes> postStampReward(
            @AuthenticationPrincipal CustomUserDetails userDetail,
            @RequestBody @Valid CafeRequestDTO.PostStampRewardReq request,
            @PathVariable("cafeId") Long cafeId) {

        // 본인인지 검사
        authorizationService.validateUserAuthorization(userDetail.getUsername(), cafeId);

        return ApiResponse.onSuccess(cafeService.postStampReward(request, cafeId));
    }

    @Operation(summary = "카페 스탬프 보상 수정")
    @PutMapping("/{cafeId}/rewards/{rewardId}")
    public ApiResponse<CafeResponseDTO.EditStampRewardRes> editStampReward(
            @AuthenticationPrincipal CustomUserDetails userDetail,
            @RequestBody @Valid CafeRequestDTO.PostStampRewardReq request,
            @PathVariable("cafeId") Long cafeId,
            @PathVariable("rewardId") Long rewardId) {

        // 본인인지 검사
        authorizationService.validateUserAuthorization(userDetail.getUsername(), cafeId);

        return ApiResponse.onSuccess(cafeService.editStampReward(request, cafeId, rewardId));
    }

    @Operation(summary = "카페 스탬프 보상 삭제")
    @DeleteMapping("/{cafeId}/rewards/{rewardId}")
    public ApiResponse<Void> deleteStampReward(
            @AuthenticationPrincipal CustomUserDetails userDetail,
            @PathVariable("cafeId") Long cafeId,
            @PathVariable("rewardId") Long rewardId) {

        // 본인인지 검사
        authorizationService.validateUserAuthorization(userDetail.getUsername(), cafeId);

        cafeService.deleteStampReward(cafeId, rewardId);

        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "리뷰 작성")
    @PostMapping(value = "/{cafeId}/reviews")
    public ApiResponse<CafeResponseDTO.PostReviewRes> editCafe(
            @AuthenticationPrincipal CustomUserDetails userDetail,
            @RequestBody @Valid CafeRequestDTO.PostReviewReq request,
            @PathVariable("cafeId") Long cafeId) {

        // 본인인지 검사
        authorizationService.validateUserAuthorization(userDetail.getUsername(), request.getCustomerId());

        return ApiResponse.onSuccess(cafeService.postReview(request, cafeId));
    }

    @Operation(summary = "카페 리뷰 검색")
    @GetMapping(value = "/{cafeId}/reviews")
    public ApiResponse<CafeResponseDTO.SearchCafeReviewsRes> searchCafeReviews(
            @AuthenticationPrincipal CustomUserDetails userDetail,
            @ModelAttribute @Valid CommonPageReq pageRequest,
            @PathVariable("cafeId") Long cafeId) {

        return ApiResponse.onSuccess(cafeService.searchCafeReviews(pageRequest, cafeId));
    }
}
