package com.example.springserver.application.admin;

import com.example.springserver.domain.admin.dto.AdminResponseDTO;
import com.example.springserver.domain.admin.service.AdminService;
import com.example.springserver.domain.auth.service.AuthorizationService;
import com.example.springserver.global.common.api.ApiResponse;
import com.example.springserver.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "관리자 API")
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "대시보드 조회")
    @GetMapping("/dashboard")
    public ApiResponse<AdminResponseDTO.GetDashboardRes> getDashboard() {
        return ApiResponse.onSuccess(adminService.getDashboard());
    }

    @Operation(summary = "최근 가입 유저 조회")
    @GetMapping("/recent-user")
    public ApiResponse<AdminResponseDTO.GetRecentUserRes> getRecentUser() {
        return ApiResponse.onSuccess(adminService.getRecentUser());
    }

    @Operation(summary = "최근 가입 카페 조회")
    @GetMapping("/recent-cafe")
    public ApiResponse<AdminResponseDTO.GetRecentCafeRes> getRecentCafe() {
        return ApiResponse.onSuccess(adminService.getRecentCafe());
    }

    @Operation(summary = "시간대별 스탬프 거래수 조회")
    @GetMapping("/stamp-transactions")
    public ApiResponse<AdminResponseDTO.GetStampTransactionsRes> getStampTransactions() {
        return ApiResponse.onSuccess(adminService.getStampTransactions());
    }

    @Operation(summary = "카페 리스트 조회")
    @GetMapping("/all/cafe")
    public ApiResponse<AdminResponseDTO.GetCafeListRes> getAllCafe(@RequestParam(required = false) String keyword) {
        return ApiResponse.onSuccess(adminService.getAllCafe(keyword));
    }

    @Operation(summary = "유저 리스트 조회")
    @GetMapping("/all/user")
    public ApiResponse<AdminResponseDTO.GetUserListRes> getAllUser(@RequestParam(required = false) String keyword) {
        return ApiResponse.onSuccess(adminService.getAllUser(keyword));
    }

    @Operation(summary = "유저 정지")
    @PatchMapping("/user/lock/{userId}")
    public ApiResponse<Void> lockUser(@PathVariable("userId") Long userId) {
        adminService.lockUser(userId);
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "유저 정지 해제")
    @PatchMapping("/user/unlock/{userId}")
    public ApiResponse<Void> unlockUser(@PathVariable("userId") Long userId) {
        adminService.unlockUser(userId);
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "카페 정지")
    @PatchMapping("/cafe/lock/{cafeId}")
    public ApiResponse<Void> lockCafe(@PathVariable("cafeId") Long cafeId) {
        adminService.lockCafe(cafeId);
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "카페 정지 해제")
    @PatchMapping("/cafe/unlock/{cafeId}")
    public ApiResponse<Void> unlockCafe(@PathVariable("cafeId") Long cafeId) {
        adminService.unlockCafe(cafeId);
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "키워드별 리뷰 개수")
    @GetMapping("/review/count")
    public ApiResponse<AdminResponseDTO.GetReviewCountRes> getReviewCount() {
        return ApiResponse.onSuccess(adminService.getReviewCount());
    }

    @Operation(summary = "키워드 업데이트")
    @PatchMapping("/cafe/keywords")
    public ApiResponse<Void> updateKeywords() {
        adminService.updateAllCafeKeywords();
        return ApiResponse.onSuccess(null);
    }
}
