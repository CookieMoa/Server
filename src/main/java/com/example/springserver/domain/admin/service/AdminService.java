package com.example.springserver.domain.admin.service;


import com.example.springserver.domain.admin.converter.AdminConverter;
import com.example.springserver.domain.admin.dto.AdminResponseDTO;
import com.example.springserver.domain.admin.enums.Cycle;
import com.example.springserver.domain.admin.enums.Setting;
import com.example.springserver.domain.ai.service.AiService;
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
import com.example.springserver.domain.log.service.StampLogService;
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
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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
    private final AiService aiService;
    private final StampLogService stampLogService;
    private final RedisTemplate<String, String> redisTemplate;

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
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay().minusNanos(1); // 오늘 23:59:59.999999

        List<Object[]> result = stampLogRepository.sumByHourOnDate(start, end);

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

    public void updateAllCafeKeywords() {
        List<Cafe> cafeList = cafeService.getAllCafe();
        for(Cafe cafe: cafeList){
            updateCafeKeywords(cafe.getId());
        }
    }

    public void updateCafeKeywords(Long cafeId) {
        Pageable pageable = PageRequest.of(0, 100);
        Page<Review> reviewPage = reviewService.findReviewByCafeId(cafeId, pageable);
        Cafe cafe = cafeService.getCafeByCafeId(cafeId);

        StringBuilder allReviews = new StringBuilder();
        for (Review review : reviewPage.getContent()) {
            allReviews.append(review.getContent()).append(" ");
        }

        List<String> predictedKeywords = aiService.predictKeywords(allReviews.toString());
        if (!predictedKeywords.isEmpty()) {
            List<Keyword> keywords = keywordService.getKeywordsByNames(predictedKeywords);
            keywordService.updateCafeKeywordMappings(cafe, keywords);
        }
    }

    public AdminResponseDTO.GetReviewCountRes getReviewCount() {
        List<Review> reviewList = reviewService.findAll();

        Map<String, Long> keywordCountMap = new HashMap<>();

        for (Review review : reviewList) {
            List<KeywordMapping> keywordMappings = review.getKeywordMappings();
            if (keywordMappings != null) {
                for (KeywordMapping mapping : keywordMappings) {
                    String keywordName = mapping.getKeyword().getName();
                    keywordCountMap.put(keywordName, keywordCountMap.getOrDefault(keywordName, 0L) + 1);
                }
            }
        }

        List<AdminResponseDTO.reviewCountDTO> reviewCountList = keywordCountMap.entrySet().stream()
                .map(entry -> AdminResponseDTO.reviewCountDTO.builder()
                        .name(entry.getKey())
                        .count(entry.getValue())
                        .build())
                .collect(Collectors.toList());

        return AdminResponseDTO.GetReviewCountRes.builder()
                .reviewCountList(reviewCountList)
                .build();
    }

    public CafeResponseDTO.GetCafeRankRes getCafeRank() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Cafe> issueCafeList = cafeRepository.findAllByOrderByTotalStampCountDesc(pageable);
        List<Cafe> useCafeList = cafeRepository.findAllByOrderByTotalStampCountDesc(pageable);
        return CafeConverter.toGetCafeRankRes(issueCafeList, useCafeList);
    }

    public CustomerResponseDTO.GetUserRankRes getUserRank() {
        List<Customer> customers = customerService.getAll();
        List<CustomerResponseDTO.GetCustomerDetailRes> users = new ArrayList<>();

        for (Customer user : customers) {
            Long totalUsedStampCount = stampLogService.getTotalCountByCustomer(user, StampLogStatus.USED);
            Long totalStampCount = stampLogService.getTotalCountByCustomer(user, StampLogStatus.ISSUED);
            users.add(CustomerConverter.toGetCustomerDetailRes(user, totalStampCount, totalUsedStampCount));
        }

        // 총 발급 수 기준 상위 5명
        List<CustomerResponseDTO.GetCustomerDetailRes> issue = users.stream()
                .sorted(Comparator.comparingLong(CustomerResponseDTO.GetCustomerDetailRes::getTotalStampCount).reversed())
                .limit(5)
                .collect(Collectors.toList());

        // 총 사용 수 기준 상위 5명
        List<CustomerResponseDTO.GetCustomerDetailRes> use = users.stream()
                .sorted(Comparator.comparingLong(CustomerResponseDTO.GetCustomerDetailRes::getTotalUsedStampCount).reversed())
                .limit(5)
                .collect(Collectors.toList());

        return CustomerConverter.toGetUserRankRes(issue, use);
    }

    public AdminResponseDTO.GetMaliciousReviewRes getMaliciousReview() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> reviewList = reviewService.findReviewByIsMalicious(true, pageable);
        List<AdminResponseDTO.maliciousReviewDTO> maliciousReviewDTOList = new ArrayList<>();
        for(Review review: reviewList){
            maliciousReviewDTOList.add(AdminConverter.toMaliciousReviewDTO(review));
        }
        return AdminConverter.toMaliciousReviewRes(maliciousReviewDTOList);
    }

}
