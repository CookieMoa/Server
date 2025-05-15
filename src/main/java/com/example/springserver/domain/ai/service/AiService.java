package com.example.springserver.domain.ai.service;

import com.example.springserver.domain.ai.converter.AiConverter;
import com.example.springserver.domain.ai.dto.AiResponseDTO;
import com.example.springserver.domain.cafe.dto.CafeRequestDTO;
import com.example.springserver.domain.cafe.dto.CafeResponseDTO;
import com.example.springserver.domain.cafe.service.ReviewService;
import com.example.springserver.entity.Cafe;
import com.example.springserver.entity.Customer;
import com.example.springserver.entity.KeywordMapping;
import com.example.springserver.entity.Review;
import com.example.springserver.global.common.api.status.ErrorStatus;
import com.example.springserver.global.exception.GeneralException;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AiService {
    private final ReviewService reviewService;
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

    public Boolean predictIsMalicious(String text) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url =  BASE_AI_URL + "/predict/hate?text=" + URLEncoder.encode(text, StandardCharsets.UTF_8);

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Object result = response.getBody().get("is_hate_speech");
            if (result != null && result.toString().equals("1")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.err.println("Hate speech 판단 중 오류: " + e.getMessage());
        }
        return false;
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


    public File makeReviewKeywordCsv() {
        List<Review> reviewList = reviewService.findAll();

        List<String> LABEL_NAMES = Arrays.asList(
                "quiet", "study_friendly", "power_outlets", "spacious", "cozy",
                "good_coffee", "dessert", "instagrammable", "pet_friendly", "late_open"
        );

        File csvFile = null;

        try {
            // ✅ 오늘 날짜 형식 (예: 2025-05-15)
            String dateStr = LocalDate.now().toString();  // java.time.LocalDate
            String fileName = "review_keywords_" + dateStr + ".csv";

            // ✅ 저장 경로 설정 (예: 현재 프로젝트 디렉토리 기준)
            csvFile = new File(fileName);

            try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
                List<String> header = new ArrayList<>();
                header.add("review_text");
                header.addAll(LABEL_NAMES);
                writer.writeNext(header.toArray(new String[0]));

                for (Review review : reviewList) {
                    String text = review.getContent();
                    List<KeywordMapping> mappings = review.getKeywordMappings();

                    Set<String> keywordSet = mappings != null
                            ? mappings.stream()
                            .map(km -> km.getKeyword().getName())
                            .collect(Collectors.toSet())
                            : Collections.emptySet();

                    List<String> row = new ArrayList<>();
                    row.add(text);
                    for (String keyword : LABEL_NAMES) {
                        row.add(keywordSet.contains(keyword) ? "1" : "0");
                    }

                    writer.writeNext(row.toArray(new String[0]));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("CSV 생성 중 오류 발생", e);
        }

        return csvFile;
    }

    public void sendCsvToAiServer(File csvFile) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(csvFile));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        String url = BASE_AI_URL + "/upload-csv";

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String uploadedFile = (String) response.getBody().get("filename");
                System.out.println("✅ CSV 업로드 성공: " + uploadedFile);
                triggerTraining(uploadedFile);
            } else {
                System.err.println("❌ 업로드 실패: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("❌ 업로드 중 예외 발생: " + e.getMessage());
        }
    }

    public void triggerTraining(String filePath) {
        RestTemplate restTemplate = new RestTemplate();
        String url = BASE_AI_URL + "/train";

        Map<String, String> request = new HashMap<>();
        request.put("filename", filePath);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("✅ 모델 학습 성공: " + response.getBody());
            } else {
                System.err.println("❌ 모델 학습 실패: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("❌ 모델 학습 중 예외 발생: " + e.getMessage());
        }
    }

    public void training() {
        File csv = makeReviewKeywordCsv();
        sendCsvToAiServer(csv);
    }

}
