package com.example.springserver.domain.admin.service;


import com.example.springserver.domain.admin.converter.AdminConverter;
import com.example.springserver.domain.admin.dto.AdminResponseDTO;
import com.example.springserver.domain.cafe.converter.CafeConverter;
import com.example.springserver.domain.cafe.dto.CafeResponseDTO;
import com.example.springserver.domain.cafe.repository.CafeRepository;
import com.example.springserver.domain.cafe.service.CafeService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
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
}
