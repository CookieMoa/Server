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
}
