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
    public AiResponseDTO.GetKeywordsResultRes predictKeywords(String text) {
        List<String> predictedKeywords = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String baseUrl = "http://3.34.137.152:8000/predict/keywords?text=";
            String url = baseUrl + URLEncoder.encode(text, StandardCharsets.UTF_8);

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
        return AiConverter.toKeywordsResultRes(predictedKeywords);
    }
}
