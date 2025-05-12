package com.example.springserver.domain.cafe.service;

import com.example.springserver.domain.cafe.converter.CafeConverter;
import com.example.springserver.domain.cafe.converter.ReviewConverter;
import com.example.springserver.domain.cafe.dto.CafeRequestDTO;
import com.example.springserver.domain.cafe.dto.CafeResponseDTO;
import com.example.springserver.domain.cafe.repository.CafeRepository;
import com.example.springserver.domain.cafe.repository.StampRewardRepository;
import com.example.springserver.domain.customer.service.CustomerService;
import com.example.springserver.domain.keyword.service.KeywordService;
import com.example.springserver.domain.log.service.StampLogService;
import com.example.springserver.domain.user.enums.AccountStatus;
import com.example.springserver.domain.user.service.UserService;
import com.example.springserver.entity.*;
import com.example.springserver.global.common.api.status.ErrorStatus;
import com.example.springserver.global.common.paging.CommonPageReq;
import com.example.springserver.global.exception.GeneralException;
import com.example.springserver.global.s3.S3Service;
import com.example.springserver.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CafeService {

    private final CafeRepository cafeRepository;
    private final StampRewardRepository stampRewardRepository;
    private final StampLogService stampLogService;
    private final UserService userService;
    private final ReviewService reviewService;
    private final KeywordService keywordService;
    private final S3Service s3Service;
    private final CustomerService customerService;
    private final RedisTemplate<String, String> redisTemplate;

    public Cafe getCafeByUserId(Long userId) {
        return cafeRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
    }

    public List<Cafe> getTop5RecentCafe() {
        return cafeRepository.findTop5ByOrderByCreatedAtDesc();
    }

    public List<CafeResponseDTO.GetCafeRes> getRecentCafe() {
        List<CafeResponseDTO.GetCafeRes> cafeListDTO = new ArrayList<>();

        List<Cafe> cafeList = getTop5RecentCafe();
        for (Cafe cafe : cafeList) {
            List<Keyword> keywords = keywordService.getKeywordsByCafe(cafe);
            List<StampReward> rewards = getStampRewardsByCafe(cafe);
            cafeListDTO.add(CafeConverter.toGetCafeRes(cafe, keywords, rewards));
        }
        return cafeListDTO;
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

        // 1. user 검색
        UserEntity user = userService.getUserById(request.getId());

        // 2. 이미지 적용
        String imgUrl;
        if(profileImg == null) {
            imgUrl = s3Service.getBasicImgUrl();
        } else {
            imgUrl = s3Service.uploadFileImage(profileImg);
        }

        // 3. MySql에 cafe 저장
        Cafe newCafe = CafeConverter.toCafe(request, user, imgUrl);
        cafeRepository.save(newCafe);

        // 4. Redis에 위치 정보 저장
        redisTemplate.opsForGeo().add(
                "cafe-location",
                new Point(newCafe.getLongitude(), newCafe.getLatitude()),
                newCafe.getId().toString()
        );

        // 5. 계정 상태 ACTIVE로 변경
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

    public CafeResponseDTO.GetMyCafeRes getMyCafe(CustomUserDetails userDetail) {
        UserEntity user = userService.getUserByUsername(userDetail.getUsername());
        Cafe cafe = getCafeByUserId(user.getId());

        List<Keyword> keywords = keywordService.getKeywordsByCafe(cafe);
        List<StampReward> rewards = getStampRewardsByCafe(cafe);

        return CafeConverter.toGetMyCafeRes(cafe, keywords, rewards);
    }

    public CafeResponseDTO.EditCafeRes editCafe(CafeRequestDTO.EditCafeReq request, MultipartFile profileImg, Long cafeId) {

        Cafe cafe = getCafeByUserId(cafeId);

        boolean isNameUpdated = false;
        boolean isAddressUpdated = false;
        boolean isContactUpdated = false;
        boolean isIntroUpdated = false;
        boolean isOpenTimeUpdated = false;
        boolean isCloseTimeUpdated = false;
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

            // Redis 위치 정보도 갱신
            redisTemplate.opsForGeo().add(
                    "cafe-location",
                    new Point(request.getLongitude(), request.getLatitude()),
                    cafe.getId().toString()
            );
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

        // 오픈 시간 수정
        if (request.getOpenTime() != null) {
            cafe.setOpenTime(request.getOpenTime());
            isOpenTimeUpdated = true;
        }

        // 마감 시간 수정
        if (request.getCloseTime() != null) {
            cafe.setCloseTime(request.getCloseTime());
            isCloseTimeUpdated = true;
        }

        cafeRepository.save(cafe);

        return CafeConverter.toEditCafeRes(
                cafe,
                isNameUpdated,
                isAddressUpdated,
                isContactUpdated,
                isIntroUpdated,
                isOpenTimeUpdated,
                isCloseTimeUpdated,
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

    public void deleteCafeAdv(Long cafeId) {

        Cafe cafe = getCafeByUserId(cafeId);
        cafe.setAdvImgUrl(null);
        cafeRepository.save(cafe);
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

    public CafeResponseDTO.PostReviewRes postReview(CafeRequestDTO.PostReviewReq request, Long cafeId) {
        Cafe cafe = getCafeByUserId(cafeId);
        Customer customer = customerService.getCustomerByUserId(request.getCustomerId());

        Review newReview = ReviewConverter.toReview(request, cafe, customer);
        Review review = reviewService.toReview(newReview);

        // 키워드 조회 및 매핑
        List<Keyword> keywords = keywordService.getKeywordsByNames(request.getKeywordList());
        if (keywords.isEmpty()) {
            throw new GeneralException(ErrorStatus.KEYWORD_NOT_FOUND);
        }
        keywordService.createReviewKeywordMappings(review, keywords);

        // StampLog.pendingReview -> false로 변경 (리뷰 작성 완료 상태)
        StampLog stampLog = stampLogService.getStampLog(request.getStampLogId());
        if (stampLog.getPendingReview()) {
            stampLog.setPendingReview(false);
        }

        return ReviewConverter.toPostReviewRes(review, keywords);
    }

    public CafeResponseDTO.SearchReviewsRes searchCafeReviews(CommonPageReq pageRequest, Long cafeId) {
        // 1. 카페 ID로 리뷰 페이지 조회
        Page<Review> reviewPage = reviewService.findReviewByCafeId(cafeId, pageRequest.toPageable());

        // 2. 각 리뷰에 대해 키워드 조회하고 DTO로 변환
        List<CafeResponseDTO.GetReviewRes> reviewResList = reviewPage.stream()
                .map(review -> {
                    List<Keyword> keywords = keywordService.getKeywordsByReview(review);
                    return ReviewConverter.toGetReviewRes(review, keywords);
                })
                .toList();

        // 3. 최종 응답 DTO 조립
        return ReviewConverter.toSearchReviewsRes(reviewPage, reviewResList);
    }
}
