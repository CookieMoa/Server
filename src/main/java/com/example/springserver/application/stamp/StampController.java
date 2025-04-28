package com.example.springserver.application.stamp;

import com.example.springserver.domain.auth.service.AuthorizationService;
import com.example.springserver.domain.stamp.dto.StampRequestDTO;
import com.example.springserver.domain.stamp.dto.StampResponseDTO;
import com.example.springserver.domain.stamp.service.StampService;
import com.example.springserver.global.common.api.ApiResponse;
import com.example.springserver.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "스탬프 API")
@RequestMapping("/stamps")
public class StampController {

    private final AuthorizationService authorizationService;
    private final StampService stampService;

    @Operation(summary = "스탬프 찍기")
    @PostMapping
    public ApiResponse<StampResponseDTO.PostStampRes> postStamp(
            @AuthenticationPrincipal CustomUserDetails userDetail,
            @Valid StampRequestDTO.PostStampReq request) {

        // 본인인지 검사
        authorizationService.validateUserAuthorization(userDetail.getUsername(), request.getCafeId());

        return ApiResponse.onSuccess(stampService.postStamp(request));
    }
}
