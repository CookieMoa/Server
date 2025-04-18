package com.example.springserver.domain.cafe.service;

import com.example.springserver.domain.cafe.dto.BusinessVerificationRequest;
import com.example.springserver.domain.cafe.dto.BusinessVerificationResponse;
import com.example.springserver.domain.cafe.dto.CafeRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VerifyBusinessService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${external.api.bizcheck.url}")
    private String apiUrl;

    @Value("${external.api.bizcheck.key}")
    private String serviceKey;

    public BusinessVerificationResponse verifyBusiness(CafeRequestDTO.VerifyBusinessReq verifyBusinessRequest) {
        BusinessVerificationRequest.BusinessInfo businessInfo =
                new BusinessVerificationRequest.BusinessInfo(
                        verifyBusinessRequest.getBusinessNumber(),
                        verifyBusinessRequest.getOpeningDate(),
                        verifyBusinessRequest.getRepresentativeName()
                );
        String decodedServicekey = null;
        try {
            decodedServicekey = URLDecoder.decode(serviceKey, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return new BusinessVerificationResponse(false, "서비스 키 디코딩 실패");
        }
        BusinessVerificationRequest request =
                new BusinessVerificationRequest(List.of(businessInfo));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<BusinessVerificationRequest> entity = new HttpEntity<>(request, headers);

        String fullUrl = String.format("%s?serviceKey=%s&returnType=JSON", apiUrl, decodedServicekey);

        ResponseEntity<String> response;
        try {
            response = restTemplate.postForEntity(fullUrl, entity, String.class);
        } catch (Exception e) {
            return new BusinessVerificationResponse(false, "국세청 API 호출 실패");
        }

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode dataNode = root.path("data");

            if (dataNode.isArray() && !dataNode.isEmpty()) {
                JsonNode firstEntry = dataNode.get(0);
                String valid = firstEntry.path("valid").asText();
                String bSttCd = firstEntry.path("status").path("b_stt_cd").asText();

                if (!"01".equals(valid)) {
                    return new BusinessVerificationResponse(false, "사업자 정보가 일치하지 않습니다.");
                }

                if (!"01".equals(bSttCd)) {
                    return new BusinessVerificationResponse(false, "해당 사업자는 현재 영업 중이 아닙니다.");
                }

                return new BusinessVerificationResponse(true, "사업자 인증 성공");
            }

            return new BusinessVerificationResponse(false, "응답 데이터가 비어 있습니다.");
        } catch (JsonProcessingException e) {
            return new BusinessVerificationResponse(false, "응답 파싱 실패");
        }
    }
}
