package com.example.springserver.domain.cafe.service;

import com.example.springserver.domain.cafe.converter.CafeConverter;
import com.example.springserver.domain.cafe.dto.CafeRequestDTO;
import com.example.springserver.domain.cafe.dto.CafeResponseDTO;
import com.example.springserver.domain.cafe.repository.CafeRepository;
import com.example.springserver.domain.cafe.repository.StampRewardRepository;
import com.example.springserver.domain.customer.converter.CustomerConverter;
import com.example.springserver.domain.customer.dto.CustomerRequestDTO;
import com.example.springserver.domain.customer.dto.CustomerResponseDTO;
import com.example.springserver.domain.keyword.service.KeywordService;
import com.example.springserver.domain.user.enums.AccountStatus;
import com.example.springserver.domain.user.service.UserService;
import com.example.springserver.entity.*;
import com.example.springserver.global.common.api.status.ErrorStatus;
import com.example.springserver.global.exception.GeneralException;
import com.example.springserver.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CafeService {

    private final CafeRepository cafeRepository;
    private final StampRewardRepository stampRewardRepository;
    private final UserService userService;
    private final KeywordService keywordService;
    private final S3Service s3Service;

    public Cafe getCafeByUserId(Long userId) {
        return cafeRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
    }

    public void validateCafeExists(Long userId) {
        if (!cafeRepository.existsByUserId(userId)) {
            throw new GeneralException(ErrorStatus.MEMBER_NOT_FOUND);
        }
    }

    public StampReward getStampRewardById(Long rewardId) {
        return stampRewardRepository.findById(rewardId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.REWARD_NOT_FOUND));
    }

    public List<StampReward> getStampRewardsByCafe(Cafe cafe) {
        return stampRewardRepository.findAllByCafe(cafe);
    }

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

    public CafeResponseDTO.GetCafeRes getCafe(Long cafeId) {
        Cafe cafe = getCafeByUserId(cafeId);

        List<Keyword> keywords = keywordService.getKeywordsByCafe(cafe);
        List<StampReward> rewards = getStampRewardsByCafe(cafe);
        
        return CafeConverter.toGetCafeRes(cafe, keywords, rewards);
    }

    public CafeResponseDTO.EditCafeRes editCafe(CafeRequestDTO.EditCafeReq request, MultipartFile profileImg, Long cafeId) {

        Cafe cafe = getCafeByUserId(cafeId);

        boolean isNameUpdated = false;
        boolean isAddressUpdated = false;
        boolean isContactUpdated = false;
        boolean isIntroUpdated = false;
        boolean isImgUpdated = false;

        // 이미지 수정
        if (profileImg != null && !profileImg.isEmpty()) {
            cafe.setImgUrl(s3Service.uploadFileImage(profileImg));
            isImgUpdated = true;
        }

        // 이름 수정
        if (request.getName() != null) {
            cafe.setName(request.getName());
            isNameUpdated = true;
        }

        // 주소 및 위치 수정
        if (request.getAddress() != null) {
            if (request.getLatitude() == null || request.getLongitude() == null) {
                throw new GeneralException(ErrorStatus.CAFE_LOCATION_MISSING);
            }
            cafe.setAddress(request.getAddress());
            cafe.setLatitude(request.getLatitude());
            cafe.setLongitude(request.getLongitude());
            isAddressUpdated = true;
        }

        // 연락처 수정
        if (request.getContact() != null) {
            cafe.setContact(request.getContact());
            isContactUpdated = true;
        }

        // 인트로 수정
        if (request.getIntro() != null) {
            cafe.setIntro(request.getIntro());
            isIntroUpdated = true;
        }

        cafeRepository.save(cafe);

        return CafeConverter.toEditCafeRes(
                cafe,
                isNameUpdated,
                isAddressUpdated,
                isContactUpdated,
                isIntroUpdated,
                isImgUpdated
        );
    }

    public CafeResponseDTO.PostCafeAdvRes postCafeAdv(MultipartFile advImg, Long cafeId) {
        if (advImg == null || advImg.isEmpty()) {
            throw new GeneralException(ErrorStatus.INVALID_IMAGE); // 예외처리 정의해두면 좋아
        }

        Cafe cafe = getCafeByUserId(cafeId);
        cafe.setAdvImgUrl(s3Service.uploadFileImage(advImg));
        cafeRepository.save(cafe);

        return CafeConverter.toPostCafeAdvRes(cafe); //
    }

    public CafeResponseDTO.PostStampRewardRes postStampReward(CafeRequestDTO.PostStampRewardReq request, Long cafeId) {
        Cafe cafe = getCafeByUserId(cafeId);
        StampReward newStampReward = CafeConverter.toStampReward(request, cafe);

        return CafeConverter.toPostStampRewardRes(stampRewardRepository.save(newStampReward)); //
    }

    public CafeResponseDTO.EditStampRewardRes editStampReward(CafeRequestDTO.PostStampRewardReq request, Long cafeId, Long rewardId) {
        StampReward stampReward = getStampRewardById(rewardId);
        boolean isRewardNameUpdated = false;
        boolean isStampCountUpdated = false;

        if (!stampReward.getCafe().getId().equals(cafeId)) {
            throw new GeneralException(ErrorStatus.INVALID_CAFE_REWARD);
        }

        // 이름 수정
        if (request.getReward() != null) {
            stampReward.setRewardName(request.getReward());
            isRewardNameUpdated = true;
        }

        // 보상 스탬프 수 수정
        if (request.getStampCount() != null) {
            stampReward.setStampCount(request.getStampCount());
            isStampCountUpdated = true;
        }

        stampRewardRepository.save(stampReward);

        return CafeConverter.toEditStampRewardRes(
                stampReward,
                isRewardNameUpdated,
                isStampCountUpdated);
    }

    public void deleteStampReward(Long cafeId, Long rewardId) {
        StampReward stampReward = getStampRewardById(rewardId);

        if (!stampReward.getCafe().getId().equals(cafeId)) {
            throw new GeneralException(ErrorStatus.INVALID_CAFE_REWARD);
        }
        stampRewardRepository.delete(stampReward);
    }
}