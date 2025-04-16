package com.example.springserver.domain.cafe.service;

import com.example.springserver.domain.cafe.converter.CafeConverter;
import com.example.springserver.domain.cafe.dto.CafeRequestDTO;
import com.example.springserver.domain.cafe.dto.CafeResponseDTO;
import com.example.springserver.domain.cafe.repository.CafeRepository;
import com.example.springserver.domain.keyword.service.KeywordService;
import com.example.springserver.domain.user.enums.AccountStatus;
import com.example.springserver.domain.user.service.UserService;
import com.example.springserver.entity.Cafe;
import com.example.springserver.entity.UserEntity;
import com.example.springserver.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CafeService {

    private final CafeRepository cafeRepository;
    private final KeywordService keywordService;
    private final UserService userService;
    private final S3Service s3Service;

    public CafeResponseDTO.PostCafeRes postCafe(CafeRequestDTO.PostCafeReq request, MultipartFile profileImg) {
        UserEntity user = userService.getUserById(request.getId());

        // 이미지 적용
        String imgUrl;
        if(profileImg == null) {
            imgUrl = s3Service.getBasicImgUrl();
        } else {
            imgUrl = s3Service.uploadFileImage(profileImg);
        }

        Cafe newCafe = CafeConverter.toCafe(request, user, imgUrl);
        cafeRepository.save(newCafe);

        // 계정 상태 ACTIVE로 변경
        user.setAccountStatus(AccountStatus.ACTIVE);
        userService.saveUser(user);


        return CafeConverter.toPostCafeRes(newCafe);
    }

}