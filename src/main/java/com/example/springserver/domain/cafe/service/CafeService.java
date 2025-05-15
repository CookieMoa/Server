package com.example.springserver.domain.cafe.service;

import com.example.springserver.domain.ai.dto.AiResponseDTO;
import com.example.springserver.domain.ai.service.AiService;
import com.example.springserver.domain.cafe.converter.CafeConverter;
import com.example.springserver.domain.cafe.converter.ReviewConverter;
import com.example.springserver.domain.cafe.dto.CafeRequestDTO;
import com.example.springserver.domain.cafe.dto.CafeResponseDTO;
import com.example.springserver.domain.cafe.enums.CafeStatus;
import com.example.springserver.domain.cafe.repository.CafeRepository;
import com.example.springserver.domain.cafe.repository.StampRewardRepository;
import com.example.springserver.domain.customer.service.CustomerService;
import com.example.springserver.domain.keyword.service.KeywordService;
import com.example.springserver.domain.log.enums.StampLogStatus;
import com.example.springserver.domain.log.service.StampLogService;
import com.example.springserver.domain.stamp.service.StampBoardService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CafeService {

    private final CafeRepository cafeRepository;
    private final StampRewardRepository stampRewardRepository;
    private final StampBoardService stampBoardService;
    private final StampLogService stampLogService;
    private final UserService userService;
    private final ReviewService reviewService;
    private final KeywordService keywordService;
    private final S3Service s3Service;
    private final CustomerService customerService;
    private final RedisTemplate<String, String> redisTemplate;
    private final AiService aiService;

    public Cafe getCafeByUserId(Long userId) {
        return cafeRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
    }

    public Cafe getCafeByCafeId(Long cafeId) {
        return cafeRepository.findById(cafeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
    }

    public List<Cafe> getTopNewCafe(int count, String keyword) {
        Pageable pageable = PageRequest.of(0, count);

        if (keyword == null || keyword.trim().isEmpty()) {
            return cafeRepository.findAllByOrderByCreatedAtDesc(pageable);
        } else {
            return cafeRepository.findByNameContainingIgnoreCaseOrderByCreatedAtDesc(keyword, pageable);
        }
    }

    public List<Cafe> getAllCafe() {
        return cafeRepository.findAllByOrderByCreatedAtDesc();
    }


    public List<Cafe> getTopNewCafe(int count) {
        Pageable pageable = PageRequest.of(0, count);
        return cafeRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public List<CafeResponseDTO.GetCafeRes> getRecentCafe() {
        List<CafeResponseDTO.GetCafeRes> cafeListDTO = new ArrayList<>();

        List<Cafe> cafeList = getTopNewCafe(5);
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

    public void validateCafeNotSuspended(Cafe cafe) {
        if (cafe.getCafeStatus().equals(CafeStatus.LOCKED)) {
            throw new GeneralException(ErrorStatus.SUSPENDED);
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

    public List<CafeResponseDTO.GetMyCafeRes> getCafeList(String keyword) {
        List<CafeResponseDTO.GetMyCafeRes> cafeListDTO = new ArrayList<>();
        List<Cafe> cafeList = getTopNewCafe(10, keyword);
        for (Cafe cafe : cafeList) {
            List<Keyword> keywords = keywordService.getKeywordsByCafe(cafe);
            List<StampReward> rewards = getStampRewardsByCafe(cafe);
            cafeListDTO.add(CafeConverter.toGetMyCafeRes(cafe, keywords, rewards));
        }
        return cafeListDTO;
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

        String reviewText = request.getContent();
        Boolean isMalicious = false;

        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://3.34.137.152:8000/predict/hate?text=" + URLEncoder.encode(reviewText, StandardCharsets.UTF_8);

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Object result = response.getBody().get("is_hate_speech");
                if (result instanceof Integer && ((Integer) result) == 1) {
                    isMalicious = true;
                }
            }
        } catch (Exception e) {
            System.err.println("Hate speech 판단 중 오류: " + e.getMessage());
        }

        Review newReview = ReviewConverter.toReview(request, cafe, customer, isMalicious);
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

    public CafeResponseDTO.SearchCafeReviewsRes searchCafeReviews(CommonPageReq pageRequest, Long cafeId) {
        // 1. 카페 ID로 리뷰 페이지 조회
        Page<Review> reviewPage = reviewService.findReviewByCafeId(cafeId, pageRequest.toPageable());

        // 2. 각 리뷰에 대해 키워드 조회하고 DTO로 변환
        List<CafeResponseDTO.GetCafeReviewRes> reviewResList = reviewPage.stream()
                .map(review -> {
                    List<Keyword> keywords = keywordService.getKeywordsByReview(review);
                    return ReviewConverter.toGetCafeReviewRes(review, keywords);
                })
                .toList();

        // 3. 최종 응답 DTO 조립
        return ReviewConverter.toSearchCafeReviewsRes(reviewPage, reviewResList);
    }

    public CafeResponseDTO.SearchCafeNearByRes searchCafeNearBy(
            CustomUserDetails userDetail,
            Double lat,
            Double lon,
            Double radius,
            String sortBy
    ) {
        UserEntity user = userService.getUserByUsername(userDetail.getUsername());
        Customer customer = customerService.getCustomerByUserId(user.getId());
        // 1. Redis에서 거리 포함 검색
        GeoResults<RedisGeoCommands.GeoLocation<String>> geoResults =
                redisTemplate.opsForGeo().radius(
                        "cafe-location",
                        new Circle(new Point(lon, lat), new Distance(radius, Metrics.KILOMETERS)),
                        RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                                .includeDistance()
                );

        if (geoResults == null || geoResults.getContent().isEmpty()) {
            return CafeConverter.toSearchCafeNearByRes(Collections.emptyList(), sortBy);
        }

        // 2. 카페 ID & 거리 맵 추출
        List<Long> cafeIds = new ArrayList<>();
        Map<Long, Double> distanceMap = new HashMap<>();

        for (GeoResult<RedisGeoCommands.GeoLocation<String>> result : geoResults.getContent()) {
            Long cafeId = Long.valueOf(result.getContent().getName());
            double distance = result.getDistance().getValue();
            cafeIds.add(cafeId);
            distanceMap.put(cafeId, distance);
        }

        // 3. 카페 정보 조회 (MySQL)
        List<Cafe> cafes = cafeRepository.findAllByIdIn(cafeIds);

        // 4. 소비자 키워드 조회
        List<Keyword> customerKeywords = keywordService.getKeywordsByCustomer(customer);
        Set<Long> customerKeywordIds = customerKeywords.stream()
                .map(Keyword::getId)
                .collect(Collectors.toSet());

        // 5. 카페 키워드 조회 + DTO 변환
        List<CafeResponseDTO.GetCafeNearByRes> resultList = new ArrayList<>();

        for (Cafe cafe : cafes) {
            List<Keyword> cafeKeywords = keywordService.getKeywordsByCafe(cafe);
            Double distance = distanceMap.get(cafe.getId());

            resultList.add(CafeConverter.toGetCafeNearByRes(cafe, cafeKeywords, distance));
        }

        // 6. 정렬
        if (sortBy.equalsIgnoreCase("preference")) {
            resultList.sort(Comparator.comparingInt(
                    (CafeResponseDTO.GetCafeNearByRes dto) ->
                            (int) dto.getKeywordList().stream()
                                    .filter(k -> customerKeywordIds.contains(k.getKeywordId()))
                                    .count()
            ).reversed());
        } else {
            resultList.sort(Comparator.comparingDouble(dto -> distanceMap.get(dto.getCafeId())));
        }

        // 7. 응답 반환
        return CafeConverter.toSearchCafeNearByRes(resultList, sortBy);
    }

    public CafeResponseDTO.SearchCafeAdvRes searchCafeAdv(
            CustomUserDetails userDetail,
            Double lat,
            Double lon,
            Double radius
    ) {
        UserEntity user = userService.getUserByUsername(userDetail.getUsername());
        Customer customer = customerService.getCustomerByUserId(user.getId());
        // 1. Redis에서 거리 포함 검색
        GeoResults<RedisGeoCommands.GeoLocation<String>> geoResults =
                redisTemplate.opsForGeo().radius(
                        "cafe-location",
                        new Circle(new Point(lon, lat), new Distance(radius, Metrics.KILOMETERS)),
                        RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                                .includeDistance()
                );

        if (geoResults == null || geoResults.getContent().isEmpty()) {
            return CafeConverter.toSearchCafeAdvRes(Collections.emptyList());
        }

        // 2. 카페 ID 추출
        List<Long> cafeIds = new ArrayList<>();

        for (GeoResult<RedisGeoCommands.GeoLocation<String>> result : geoResults.getContent()) {
            Long cafeId = Long.valueOf(result.getContent().getName());
            cafeIds.add(cafeId);
        }

        // 3. 카페 정보 조회 (MySQL)
        List<Cafe> cafes = cafeRepository.findAllByIdIn(cafeIds);

        // 4. DTO 변환
        List<CafeResponseDTO.GetCafeAdvRes> resultList = new ArrayList<>();

        for (Cafe cafe : cafes) {
            resultList.add(CafeConverter.toGetCafeAdvRes(cafe));
        }

        // 5. 응답 반환
        return CafeConverter.toSearchCafeAdvRes(resultList);
    }

    public CafeResponseDTO.SearchCafesRes searchCafes(
            CustomUserDetails userDetail,
            String query) {

        UserEntity user = userService.getUserByUsername(userDetail.getUsername());
        Customer customer = customerService.getCustomerByUserId(user.getId());

        // 1. 이름 기반 검색
        List<Cafe> nameMatched = cafeRepository.findByNameContaining(query);

        // 2. 키워드 기반 검색
        AiResponseDTO.GetKeywordsResultRes getKeywordsResultRes = aiService.getPredictKeywords(query);
        List<String> predictedKeywords = getKeywordsResultRes.getPredicted_keywords();
        List<Cafe> keywordMatched = keywordService.getCafesByKeywordNames(predictedKeywords);

        // 3. 중복 제거 및 source 분류
        Map<Long, CafeResponseDTO.GetCafesRes> resultMap = new LinkedHashMap<>();

        for (Cafe cafe : nameMatched) {
            resultMap.put(cafe.getId(), CafeConverter.toGetCafesRes(cafe, stampBoardService.findStampBoard(cafe, customer), "name"));
        }
        for (Cafe cafe : keywordMatched) {
            if (!resultMap.containsKey(cafe.getId())) {
                resultMap.put(cafe.getId(), CafeConverter.toGetCafesRes(cafe, stampBoardService.findStampBoard(cafe, customer), "keyword"));
            }
        }

        // 4. DTO 변환 및 응답 생성
        List<CafeResponseDTO.GetCafesRes> cafeList = new ArrayList<>(resultMap.values());
        return CafeConverter.toSearchCafesRes(cafeList);
    }

    public void lockCafe(Long cafeId) {
        Cafe cafe = getCafeByUserId(cafeId);
        cafe.setCafeStatus(CafeStatus.LOCKED);
        cafeRepository.save(cafe);
    }

    public void unlockCafe(Long cafeId) {
        Cafe cafe = getCafeByUserId(cafeId);
        cafe.setCafeStatus(CafeStatus.ACTIVE);
        cafeRepository.save(cafe);
    }
}
