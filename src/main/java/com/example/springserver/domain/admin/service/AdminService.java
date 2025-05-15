package com.example.springserver.domain.admin.service;


import com.example.springserver.domain.admin.converter.AdminConverter;
import com.example.springserver.domain.admin.dto.AdminResponseDTO;
import com.example.springserver.domain.cafe.converter.CafeConverter;
import com.example.springserver.domain.cafe.dto.CafeResponseDTO;
import com.example.springserver.domain.cafe.repository.CafeRepository;
import com.example.springserver.domain.cafe.service.CafeService;
import com.example.springserver.domain.cafe.service.ReviewService;
import com.example.springserver.domain.customer.converter.CustomerConverter;
import com.example.springserver.domain.customer.dto.CustomerRequestDTO;
import com.example.springserver.domain.customer.dto.CustomerResponseDTO;
import com.example.springserver.domain.customer.repository.CustomerRepository;
import com.example.springserver.domain.customer.service.CustomerService;
import com.example.springserver.domain.keyword.service.KeywordService;
import com.example.springserver.domain.log.enums.StampLogStatus;
import com.example.springserver.domain.log.repository.StampLogRepository;
import com.example.springserver.domain.user.enums.AccountStatus;
import com.example.springserver.domain.user.service.UserService;
import com.example.springserver.entity.*;
import com.example.springserver.global.common.api.status.ErrorStatus;
import com.example.springserver.global.common.paging.CommonPageReq;
import com.example.springserver.global.exception.GeneralException;
import com.example.springserver.global.s3.S3Service;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final CustomerRepository customerRepository;
    private final CafeRepository cafeRepository;
    private final StampLogRepository stampLogRepository;
    private final CafeService cafeService;
    private final CustomerService customerService;
    private final ReviewService reviewService;
    private final KeywordService keywordService;

    public AdminResponseDTO.GetDashboardRes getDashboard() {
        Long customerCount = customerRepository.count();
        Long cafeCount = cafeRepository.count();
        Long issuedCouponCount = stampLogRepository.sum(StampLogStatus.ISSUED);
        Long usedCouponCount = stampLogRepository.sum(StampLogStatus.USED);
        Long couponUsageRate = 0L;
        if (issuedCouponCount != 0L)
            couponUsageRate = usedCouponCount/issuedCouponCount;

        return AdminConverter.toDashboardRes(
                customerCount,
                cafeCount,
                issuedCouponCount,
                usedCouponCount,
                couponUsageRate);
    }

    public AdminResponseDTO.GetRecentUserRes getRecentUser() {
        return AdminConverter.toRecentUserRes(customerService.getRecentUser());
    }

    public AdminResponseDTO.GetRecentCafeRes getRecentCafe() {
        return AdminConverter.toRecentCafeRes(cafeService.getRecentCafe());
    }

    public AdminResponseDTO.GetStampTransactionsRes getStampTransactions() {
        List<Object[]> result = stampLogRepository.sumByHourOnDate(java.sql.Date.valueOf(LocalDate.now()));

        List<AdminResponseDTO.StampTransactionDto> stampTransactionList = result.stream()
                .map(row -> {
                    Long hour = ((Number) row[0]).longValue();
                    Long count = ((Number) row[1]).longValue();
                    return AdminConverter.toStampTransactionDTO(hour, count);
                })
                .collect(Collectors.toList());
        return AdminConverter.toStampTransactionsRes(stampTransactionList);
    }

    public AdminResponseDTO.GetCafeListRes getAllCafe(String keyword) {
        return AdminConverter.toCafeListRes(cafeService.getCafeList(keyword));
    }

    public AdminResponseDTO.GetUserListRes getAllUser(String keyword) {
        return AdminConverter.toUserListRes(customerService.getUserList(keyword));
    }

    public void lockUser(Long userId) {
        customerService.lockUser(userId);
    }

    public void unlockUser(Long userId) {
        customerService.unlockUser(userId);
    }

    public void lockCafe(Long cafeId) {
        cafeService.lockCafe(cafeId);
    }

    public void unlockCafe(Long cafeId) {
        cafeService.unlockCafe(cafeId);
    }

    public void updateCafeKeywords(Long cafeId) {
        Pageable pageable = PageRequest.of(0, 100);
        Page<Review> reviewPage = reviewService.findReviewByCafeId(cafeId, pageable);
        Cafe cafe = cafeService.getCafeByCafeId(cafeId);

        StringBuilder allReviews = new StringBuilder();
        for (Review review : reviewPage.getContent()) {
            allReviews.append(review.getContent()).append(" ");
        }

        List<String> predictedKeywords = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String baseUrl = "http://3.34.137.152:8000/predict/keywords?text=";
            String url = baseUrl + URLEncoder.encode(allReviews.toString(), StandardCharsets.UTF_8);

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
            System.err.println("Cafe keyword prediction error: " + e.getMessage());
            return;
        }

        if (!predictedKeywords.isEmpty()) {
            List<Keyword> keywords = keywordService.getKeywordsByNames(predictedKeywords);
            keywordService.createCafeKeywordMappings(cafe, keywords);
        }
    }

    public int test() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://3.34.137.152:8000/predict/hate?text=" + URLEncoder.encode("시발", StandardCharsets.UTF_8);

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Object result = response.getBody().get("is_hate_speech");
                if (result instanceof Integer && ((Integer) result) == 1) {
                    return 1;
                } else {
                    return 0;
                }
            }
        } catch (Exception e) {
            System.err.println("Hate speech 판단 중 오류: " + e.getMessage());
        }
        return -1;
    }

}
