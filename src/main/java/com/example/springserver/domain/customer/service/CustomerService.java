package com.example.springserver.domain.customer.service;


import com.example.springserver.domain.customer.converter.CustomerConverter;
import com.example.springserver.domain.customer.dto.CustomerRequestDTO;
import com.example.springserver.domain.customer.dto.CustomerResponseDTO;
import com.example.springserver.domain.customer.repository.CustomerRepository;
import com.example.springserver.domain.keyword.service.KeywordService;
import com.example.springserver.domain.stamp.service.StampBoardService;
import com.example.springserver.domain.user.enums.AccountStatus;
import com.example.springserver.domain.user.service.UserService;
import com.example.springserver.entity.Customer;
import com.example.springserver.entity.Keyword;
import com.example.springserver.entity.StampBoard;
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
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final KeywordService keywordService;
    private final StampBoardService stampBoardService;
    private final UserService userService;
    private final S3Service s3Service;

    public Customer getCustomerByUserId(Long userId) {
        return customerRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
    }

    public void validateCustomerExists(Long userId) {
        if (!customerRepository.existsByUserId(userId)) {
            throw new GeneralException(ErrorStatus.MEMBER_NOT_FOUND);
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
        String qrData = String.format("{\"userId\": \"%s\", \"timestamp\": \"%d\"}", userId, System.currentTimeMillis());

        String base64QR;
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrData, BarcodeFormat.QR_CODE, 250, 250);
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", outputStream);
            base64QR = Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.GENERATE_QR_FAILED);
        }

        return base64QR;
    }

    public Page<StampBoard> searchStampBoards(Long customerId, CommonPageReq pageRequest) {

        Pageable pageable = pageRequest.toPageable();

        return stampBoardService.searchStampBoard(customerId, pageable);
    }
}
