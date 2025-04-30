package com.example.springserver.domain.admin.service;


import com.example.springserver.domain.admin.converter.AdminConverter;
import com.example.springserver.domain.admin.dto.AdminResponseDTO;
import com.example.springserver.domain.cafe.repository.CafeRepository;
import com.example.springserver.domain.customer.converter.CustomerConverter;
import com.example.springserver.domain.customer.dto.CustomerRequestDTO;
import com.example.springserver.domain.customer.dto.CustomerResponseDTO;
import com.example.springserver.domain.customer.repository.CustomerRepository;
import com.example.springserver.domain.keyword.service.KeywordService;
import com.example.springserver.domain.user.enums.AccountStatus;
import com.example.springserver.domain.user.service.UserService;
import com.example.springserver.entity.Customer;
import com.example.springserver.entity.Keyword;
import com.example.springserver.entity.UserEntity;
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
import java.util.Base64;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final CustomerRepository customerRepository;
    private final CafeRepository cafeRepository;

    public AdminResponseDTO.GetDashboardRes getDashboard() {
        Long customerCount = customerRepository.count();
        Long cafeCount = cafeRepository.count();
        Long issuedCouponCount = 0L;
        Long usedCouponCount = 0L;
        Long couponUsageRate = 0L;
        return AdminConverter.toDashboardRes(customerCount,
                cafeCount,
                issuedCouponCount,
                usedCouponCount,
                couponUsageRate);
    }
}
