package com.example.springserver.application.cafe;

import com.example.springserver.domain.auth.service.AuthorizationService;
import com.example.springserver.domain.cafe.dto.BusinessVerificationResponse;
import com.example.springserver.domain.cafe.dto.CafeRequestDTO;
import com.example.springserver.domain.cafe.dto.CafeResponseDTO;
import com.example.springserver.domain.cafe.service.CafeService;
import com.example.springserver.domain.cafe.service.VerifyBusinessService;
import com.example.springserver.domain.user.service.UserService;
import com.example.springserver.global.common.api.ApiResponse;
import com.example.springserver.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @Operation(summary = "카페 정보 수정")
    @PutMapping(value = "/{cafeId}", consumes = "multipart/form-data")
    public ApiResponse<CafeResponseDTO.EditCafeRes> editCafe(
            @AuthenticationPrincipal CustomUserDetails userDetail,
            @RequestPart("data") @Valid CafeRequestDTO.EditCafeReq request,
            @RequestPart(value = "profileImg", required = false) MultipartFile profileImg,
            @PathVariable Long cafeId) {

        // 본인인지 검사
        authorizationService.validateCustomerAuthorization(userDetail.getUsername(), cafeId);

        return ApiResponse.onSuccess(cafeService.editCafe(request, profileImg, cafeId));
    }

    @Operation(summary = "카페 사업자 등록 인증")
    @PostMapping("/verify-business")
    public ApiResponse<?> verifyBusiness(
            @RequestBody @Valid CafeRequestDTO.VerifyBusinessReq request) {
        BusinessVerificationResponse result = verificationService.verifyBusiness(request);
        return ApiResponse.onSuccess(result);
    }
}
