package com.example.springserver.application.ai;

import com.example.springserver.domain.admin.dto.AdminResponseDTO;
import com.example.springserver.domain.admin.service.AdminService;
import com.example.springserver.domain.ai.dto.AiResponseDTO;
import com.example.springserver.domain.ai.service.AiService;
import com.example.springserver.global.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "AI API")
@RequestMapping("/ai")
public class AiController {

    private final AiService aiService;

    @Operation(summary = "키워드 예측")
    @GetMapping("/keywords")
    public ApiResponse<AiResponseDTO.GetKeywordsResultRes> predictKeywords(@RequestParam(required = false) String text) {
        return ApiResponse.onSuccess(aiService.getPredictKeywords(text));
    }

    @Operation(summary = "AI 정보")
    @GetMapping("/info")
    public ApiResponse<AiResponseDTO.GetInfoRes> getInfo() {
        return ApiResponse.onSuccess(aiService.getInfo());
    }

    @Operation(summary = "AI 성능")
    @GetMapping("/metrics")
    public ApiResponse<AiResponseDTO.GetMetricsRes> getMetrics() {
        return ApiResponse.onSuccess(aiService.getMetrics());
    }

    @Operation(summary = "모델 학습")
    @PostMapping("/training")
    public ApiResponse<Void> training() {
        aiService.training();
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "모델 학습")
    @GetMapping("/test")
    public ApiResponse<Boolean> test() {
        return ApiResponse.onSuccess(aiService.predictIsMalicious("시발"));
    }
}
