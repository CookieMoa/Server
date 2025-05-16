package com.example.springserver.domain.customer.service;


import com.example.springserver.domain.cafe.converter.CafeConverter;
import com.example.springserver.domain.cafe.converter.ReviewConverter;
import com.example.springserver.domain.cafe.dto.CafeResponseDTO;
import com.example.springserver.domain.cafe.enums.CafeStatus;
import com.example.springserver.domain.cafe.service.ReviewService;
import com.example.springserver.domain.customer.converter.CustomerConverter;
import com.example.springserver.domain.customer.dto.CustomerRequestDTO;
import com.example.springserver.domain.customer.dto.CustomerResponseDTO;
import com.example.springserver.domain.customer.repository.CustomerRepository;
import com.example.springserver.domain.keyword.service.KeywordService;
import com.example.springserver.domain.log.enums.StampLogStatus;
import com.example.springserver.domain.log.service.StampLogService;
import com.example.springserver.domain.stamp.service.StampBoardService;
import com.example.springserver.domain.user.dto.UserResponseDTO;
import com.example.springserver.domain.user.enums.AccountStatus;
import com.example.springserver.domain.user.repository.UserRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final KeywordService keywordService;
    private final StampBoardService stampBoardService;
    private final StampLogService stampLogService;
    private final ReviewService reviewService;
    private final UserService userService;
    private final S3Service s3Service;
    private final UserRepository userRepository;

    public Customer getCustomerByUserId(Long userId) {
        return customerRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
    }

    public List<Customer> getTopNewUser(int count, String keyword) {
        Pageable pageable = PageRequest.of(0, count);

        if (keyword == null || keyword.trim().isEmpty()) {
            return customerRepository.findAllByOrderByCreatedAtDesc(pageable);
        } else {
            return customerRepository.findByNameContainingIgnoreCaseOrderByCreatedAtDesc(keyword, pageable);
        }
    }

    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    public List<Customer> getTopNewUser(int count) {
        Pageable pageable = PageRequest.of(0, count);
        return customerRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public List<CustomerResponseDTO.GetCustomerRes> getRecentUser() {
        List<CustomerResponseDTO.GetCustomerRes> userListDTO = new ArrayList<>();
        List<Customer> userList = getTopNewUser(5);
        for (Customer customer : userList) {
            List<Keyword> keywords = keywordService.getKeywordsByCustomer(customer);
            userListDTO.add(CustomerConverter.toGetCustomerRes(customer, keywords));
        }
        return userListDTO;
    }

    public void validateCustomerExists(Long userId) {
        if (!customerRepository.existsByUserId(userId)) {
            throw new GeneralException(ErrorStatus.MEMBER_NOT_FOUND);
        }
    }

    public void validateUserNotSuspended(Customer user) {
        if (user.getUser().getAccountStatus().equals(AccountStatus.LOCKED)) {
            throw new GeneralException(ErrorStatus.SUSPENDED);
        }
    }


    public CustomerResponseDTO.PostCustomerRes postCustomer(CustomerRequestDTO.PostCustomerReq request, MultipartFile profileImg) {
        UserEntity user = userService.getUserById(request.getId());

        // 이미지 적용
        String imgUrl;
        if(profileImg == null) {
            imgUrl = s3Service.getBasicImgUrl();
        } else {
            imgUrl = s3Service.uploadFileImage(profileImg);
        }

        Customer newCustomer = CustomerConverter.toCustomer(request, user, imgUrl);
        customerRepository.save(newCustomer);

        // 계정 상태 ACTIVE로 변경
        user.setAccountStatus(AccountStatus.ACTIVE);
        userService.saveUser(user);

        // 키워드 조회 및 매핑
        List<Keyword> keywords = keywordService.getKeywordsByNames(request.getKeywordList());
        if (keywords.isEmpty()) {
            throw new GeneralException(ErrorStatus.KEYWORD_NOT_FOUND);
        }
        keywordService.createCustomerKeywordMappings(newCustomer, keywords);

        return CustomerConverter.toPostCustomerRes(newCustomer, keywords);
    }

    public CustomerResponseDTO.EditCustomerRes editCustomer(CustomerRequestDTO.EditCustomerReq request, MultipartFile profileImg, Long customerId) {

        Customer customer = getCustomerByUserId(customerId);

        boolean isNameUpdated = false;
        boolean isImgUpdated = false;
        boolean isKeywordUpdated = false;

        List<Keyword> keywords = null;

        // 이미지 수정
        if (profileImg != null && !profileImg.isEmpty()) {
            customer.setImgUrl(s3Service.uploadFileImage(profileImg));
            isImgUpdated = true;
        }

        // 이름 수정
        if (request.getName() != null) {
            customer.setName(request.getName());
            isNameUpdated = true;
        }

        // 키워드 수정
        if (request.getKeywordList() != null && !request.getKeywordList().isEmpty()) {
            keywords = keywordService.getKeywordsByNames(request.getKeywordList());
            if (keywords.isEmpty()) {
                throw new GeneralException(ErrorStatus.KEYWORD_NOT_FOUND);
            }
            keywordService.updateCustomerKeywordMappings(customer, keywords);
            isKeywordUpdated = true;
        }

        customerRepository.save(customer);

        return CustomerConverter.toEditCustomerRes(customer,
                isNameUpdated,
                isImgUpdated,
                isKeywordUpdated,
                keywords);
    }

    public CustomerResponseDTO.GetCustomerRes getCustomer(Long userId) {

        Customer customer = getCustomerByUserId(userId);

        List<Keyword> keywords = keywordService.getKeywordsByCustomer(customer);

        return CustomerConverter.toGetCustomerRes(customer, keywords);
    }

    public List<CustomerResponseDTO.GetCustomerDetailRes> getUserList(String keyword) {
        List<CustomerResponseDTO.GetCustomerDetailRes> userListDTO = new ArrayList<>();
        List<Customer> userList = getTopNewUser(10, keyword);
        for (Customer user : userList) {
            List<Keyword> keywords = keywordService.getKeywordsByCustomer(user);
            Long visitedCafeCount = stampBoardService.countAllByCustomer(user);
            Long totalUsedStampCount = stampLogService.getTotalCountByCustomer(user, StampLogStatus.USED);
            Long totalStampCount = stampLogService.getTotalCountByCustomer(user, StampLogStatus.ISSUED);
            Pageable pageable = PageRequest.of(0, 3);
            Page<Review> maliciousReviewPage = reviewService.findReviewByCustomerId(user.getId(), true,pageable);
            List<CafeResponseDTO.GetCafeReviewRes> maliciousReviewList = maliciousReviewPage.stream()
                    .map(review -> {
                        List<Keyword> reviewKeywords = keywordService.getKeywordsByReview(review);
                        return ReviewConverter.toGetCafeReviewRes(review, reviewKeywords);
                    })
                    .toList();

            Page<Review> reviewPage = reviewService.findReviewByCustomerId(user.getId(), false, pageable);
            List<CafeResponseDTO.GetCafeReviewRes> reviewList = reviewPage.stream()
                    .map(review -> {
                        List<Keyword> reviewKeywords = keywordService.getKeywordsByReview(review);
                        return ReviewConverter.toGetCafeReviewRes(review, reviewKeywords);
                    })
                    .toList();

            userListDTO.add(CustomerConverter.toGetCustomerDetailRes(
                    user,
                    keywords,
                    visitedCafeCount,
                    totalStampCount,
                    totalUsedStampCount,
                    maliciousReviewList,
                    reviewList));
        }
        return userListDTO;
    }

    public Page<Customer> searchCustomer(CommonPageReq pageRequest, String query) {

        Pageable pageable = pageRequest.toPageable();

        Page<Customer> customers = customerRepository.findByNameStartingWith(query, pageable);

        if (customers.isEmpty()) {
            throw new GeneralException(ErrorStatus.MEMBER_NOT_FOUND);
        }

        return customers;
    }

    public String getQrcode(Long userId) {

        Customer customer = getCustomerByUserId(userId);
        String jsonPayload = String.format("{\"userId\": \"%s\", \"timestamp\": \"%d\"}", userId, System.currentTimeMillis());
        String encodedPayload = Base64.getEncoder().encodeToString(jsonPayload.getBytes(StandardCharsets.UTF_8));

        String base64QR;
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(encodedPayload, BarcodeFormat.QR_CODE, 250, 250);
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", outputStream);
            base64QR = Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.GENERATE_QR_FAILED);
        }

        return base64QR;
    }

    public List<CustomerResponseDTO.GetPendingReviewRes> searchPendingReview(Long customerId) {

        Customer customer = getCustomerByUserId(customerId);
        List<StampLog> stampLogList = stampLogService.searchPendingReviewsByCustomer(customerId);

        return CustomerConverter.toSearchPendingReviewRes(stampLogList);
    }

    public Page<StampBoard> searchStampBoards(Long customerId, CommonPageReq pageRequest) {

        Pageable pageable = pageRequest.toPageable();

        return stampBoardService.searchStampBoardByCustomerId(customerId, pageable);
    }


    public CustomerResponseDTO.SearchCustomerReviewsRes searchCustomerReviews(CommonPageReq pageRequest, Long customerId) {
        // 1. 소비자 ID로 리뷰 페이지 조회
        Page<Review> reviewPage = reviewService.findReviewByCustomerId(customerId, pageRequest.toPageable());

        // 2. 각 리뷰에 대해 키워드 조회하고 DTO로 변환
        List<CustomerResponseDTO.GetCustomerReviewRes> reviewResList = reviewPage.stream()
                .map(review -> {
                    List<Keyword> keywords = keywordService.getKeywordsByReview(review);
                    return ReviewConverter.toGetCustomerReviewRes(review, keywords);
                })
                .toList();

        // 3. 최종 응답 DTO 조립
        return ReviewConverter.toSearchCustomerReviewsRes(reviewPage, reviewResList);
    }

    public CustomerResponseDTO.SearchCustomerReviewsRes getUserRank(CommonPageReq pageRequest, Long customerId) {
        // 1. 소비자 ID로 리뷰 페이지 조회
        Page<Review> reviewPage = reviewService.findReviewByCustomerId(customerId, pageRequest.toPageable());

        // 2. 각 리뷰에 대해 키워드 조회하고 DTO로 변환
        List<CustomerResponseDTO.GetCustomerReviewRes> reviewResList = reviewPage.stream()
                .map(review -> {
                    List<Keyword> keywords = keywordService.getKeywordsByReview(review);
                    return ReviewConverter.toGetCustomerReviewRes(review, keywords);
                })
                .toList();

        // 3. 최종 응답 DTO 조립
        return ReviewConverter.toSearchCustomerReviewsRes(reviewPage, reviewResList);
    }

    public void lockUser(Long customerId) {
        Customer user = getCustomerByUserId(customerId);
        user.getUser().setAccountStatus(AccountStatus.LOCKED);
        customerRepository.save(user);
    }

    public void unlockUser(Long customerId) {
        Customer user = getCustomerByUserId(customerId);
        user.getUser().setAccountStatus(AccountStatus.ACTIVE);
        customerRepository.save(user);
    }

    public void deleteAccount(Long customerId) {
        Customer user = getCustomerByUserId(customerId);
        customerRepository.delete(user);
        userRepository.delete(user.getUser());
    }
}
