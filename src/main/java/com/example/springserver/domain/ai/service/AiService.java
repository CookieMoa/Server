package com.example.springserver.domain.ai.service;

import com.example.springserver.domain.ai.converter.AiConverter;
import com.example.springserver.domain.ai.dto.AiResponseDTO;
import com.example.springserver.global.common.api.status.ErrorStatus;
import com.example.springserver.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class AiService {

    public static final String BASE_AI_URL = "http://3.34.137.152:8000";

    public List<String> predictKeywords(String text) {
        List<String> predictedKeywords = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = BASE_AI_URL + "/predict/keywords?text=" + URLEncoder.encode(text, StandardCharsets.UTF_8);

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Object keywordsObj = response.getBody().get("predicted_keywords");
                if (keywordsObj instanceof List<?>) {
                    for (Object keyword : (List<?>) keywordsObj) {
                        if (keyword instanceof String) {
                            predictedKeywords.add((String) keyword);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.AI_PROCESSING_ERROR);
        }
        return predictedKeywords;
    }

    public AiResponseDTO.GetKeywordsResultRes getPredictKeywords(String text) {
        return AiConverter.toKeywordsResultRes(predictKeywords(text));
    }

    public AiResponseDTO.GetInfoRes getInfo() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = BASE_AI_URL + "/info";

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> body = response.getBody();

                String version = (String) body.get("version");
                String lastTrained = (String) body.get("last_trained");
                String performanceImprovement = (String) body.get("performance_improvement");

                return AiConverter.toInfoRes(version, lastTrained, performanceImprovement);
            } else {
                throw new RuntimeException("AI info API 호출 실패");
            }
        } catch (Exception e) {
            throw new RuntimeException("AI info API 처리 중 오류 발생: " + e.getMessage());
        }
    }

    public AiResponseDTO.GetMetricsRes getMetrics() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = BASE_AI_URL + "/metrics";

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("AI metrics API 호출 실패");
            }

            Map<String, Object> body = response.getBody();
            List<AiResponseDTO.metricsDTO> metricsList = new ArrayList<>();

            for (Map.Entry<String, Object> entry : body.entrySet()) {
                String name = entry.getKey();

                // 각 항목이 Map 형태일 때만 처리 (e.g., "quiet", "good_coffee")
                if (entry.getValue() instanceof Map) {
                    Map<String, Object> metricValues = (Map<String, Object>) entry.getValue();
                    Object f1 = metricValues.get("f1-score");

                    if (f1 instanceof Number) {
                        double f1Score = ((Number) f1).doubleValue();
                        long performance = Math.round(f1Score * 100);  // 소수점 제거 후 정수로

                        metricsList.add(AiResponseDTO.metricsDTO.builder()
                                .name(name)
                                .performance(performance)
                                .build());
                    }
                }
            }

            return AiResponseDTO.GetMetricsRes.builder()
                    .metricsList(metricsList)
                    .build();

        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.AI_PROCESSING_ERROR);
        }
    }

}
