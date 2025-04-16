package com.example.springserver.application.cafe;

import com.example.springserver.domain.auth.service.AuthorizationService;
import com.example.springserver.domain.cafe.dto.CafeRequestDTO;
import com.example.springserver.domain.cafe.dto.CafeResponseDTO;
import com.example.springserver.domain.cafe.service.CafeService;
import com.example.springserver.domain.customer.converter.CustomerConverter;
import com.example.springserver.domain.customer.dto.CustomerRequestDTO;
import com.example.springserver.domain.customer.dto.CustomerResponseDTO;
import com.example.springserver.domain.user.service.UserService;
import com.example.springserver.entity.Customer;
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
    private final CafeService cafeService;
    private final UserService userService;

    @Operation(summary = "카페 등록")
    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<CafeResponseDTO.PostCafeRes> postCafe(
            @RequestPart("data") @Valid CafeRequestDTO.PostCafeReq request,
            @RequestPart(value = "profileImg", required = false) MultipartFile profileImg) {

        return ApiResponse.onSuccess(cafeService.postCafe(request, profileImg));
    }
}
