package com.example.springserver.domain.cafe.converter;

import com.example.springserver.domain.cafe.dto.CafeRequestDTO;
import com.example.springserver.domain.cafe.dto.CafeResponseDTO;
import com.example.springserver.domain.customer.dto.CustomerResponseDTO;
import com.example.springserver.domain.keyword.converter.KeywordConverter;
import com.example.springserver.domain.keyword.dto.KeywordResponseDTO;
import com.example.springserver.domain.stamp.converter.StampConverter;
import com.example.springserver.entity.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CafeConverter {

    private CafeConverter() {
        throw new UnsupportedOperationException("Utility class");
    }

    // 날짜를 포맷하는 메서드
    private static String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    // 시간을 포맷하는 메서드
    private static String formatTime(LocalTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(formatter);
    }

    public static Cafe toCafe(CafeRequestDTO.PostCafeReq request, UserEntity user, String imgUrl){

        return Cafe.builder()
                .user(user)
                .name(request.getName())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .contact(request.getContact())
                .intro(request.getIntro())
                .openTime(request.getOpenTime())
                .closeTime(request.getCloseTime())
                .imgUrl(imgUrl)
                .build();
    }

    public static StampReward toStampReward(CafeRequestDTO.PostStampRewardReq request, Cafe cafe){

        return StampReward.builder()
                .rewardName(request.getReward())
                .stampCount(request.getStampCount())
                .cafe(cafe)
                .build();
    }

    public static CafeResponseDTO.StampRewardDto toStampRewardDto(StampReward stampReward) {
        return CafeResponseDTO.StampRewardDto.builder()
                .stampRewardId(stampReward.getId())
                .reward(stampReward.getRewardName())
                .stampCount(stampReward.getStampCount())
                .build();
    }

    public static CafeResponseDTO.PostCafeRes toPostCafeRes(Cafe cafe){
        return CafeResponseDTO.PostCafeRes.builder()
                .cafeId(cafe.getId())
                .name(cafe.getName())
                .address(cafe.getAddress())
                .latitude(cafe.getLatitude())
                .longitude(cafe.getLongitude())
                .contact(cafe.getContact())
                .intro(cafe.getIntro())
                .openTime(formatTime(cafe.getOpenTime()))
                .closeTime(formatTime(cafe.getCloseTime()))
                .imgUrl(cafe.getImgUrl())
                .build();
    }

    public static CafeResponseDTO.GetCafeRes toGetCafeRes(Cafe cafe, List<Keyword> keywords, List<StampReward> rewards){
        List<KeywordResponseDTO.KeywordDto> keywordDtoList = keywords.stream()
                .map(KeywordConverter::toKeywordDto).toList();

        List<CafeResponseDTO.StampRewardDto> rewardDtoList = rewards.stream()
                .map(CafeConverter::toStampRewardDto).toList();

        return CafeResponseDTO.GetCafeRes.builder()
                .cafeId(cafe.getId())
                .name(cafe.getName())
                .address(cafe.getAddress())
                .latitude(cafe.getLatitude())
                .longitude(cafe.getLongitude())
                .contact(cafe.getContact())
                .intro(cafe.getIntro())
                .imgUrl(cafe.getImgUrl())
                .openTime(formatTime(cafe.getOpenTime()))
                .closeTime(formatTime(cafe.getCloseTime()))
                .rewardList(rewardDtoList)
                .keywordList(keywordDtoList)
                .createdAt(cafe.getCreatedAt())
                .build();
    }

    public static CafeResponseDTO.GetMyCafeRes toGetMyCafeRes(Cafe cafe,
                                                              List<Keyword> keywords,
                                                              List<StampReward> rewards){
        List<KeywordResponseDTO.KeywordDto> keywordDtoList = keywords.stream()
                .map(KeywordConverter::toKeywordDto).toList();

        List<CafeResponseDTO.StampRewardDto> rewardDtoList = rewards.stream()
                .map(CafeConverter::toStampRewardDto).toList();

        return CafeResponseDTO.GetMyCafeRes.builder()
                .cafeId(cafe.getId())
                .name(cafe.getName())
                .address(cafe.getAddress())
                .latitude(cafe.getLatitude())
                .longitude(cafe.getLongitude())
                .contact(cafe.getContact())
                .intro(cafe.getIntro())
                .totalStampCount(cafe.getTotalStampCount())
                .totalUsedStampCount(cafe.getTotalUsedStampCount())
                .imgUrl(cafe.getImgUrl())
                .advImgUrl(cafe.getAdvImgUrl())
                .openTime(formatTime(cafe.getOpenTime()))
                .closeTime(formatTime(cafe.getCloseTime()))
                .rewardList(rewardDtoList)
                .keywordList(keywordDtoList)
                .createdAt(cafe.getCreatedAt())
                .cafeStatus(cafe.getCafeStatus())
                .build();
    }

    public static CafeResponseDTO.GetMyCafeRes toGetMyCafeRes(Cafe cafe){
        return CafeResponseDTO.GetMyCafeRes.builder()
                .cafeId(cafe.getId())
                .name(cafe.getName())
                .address(cafe.getAddress())
                .latitude(cafe.getLatitude())
                .longitude(cafe.getLongitude())
                .contact(cafe.getContact())
                .intro(cafe.getIntro())
                .totalStampCount(cafe.getTotalStampCount())
                .totalUsedStampCount(cafe.getTotalUsedStampCount())
                .imgUrl(cafe.getImgUrl())
                .advImgUrl(cafe.getAdvImgUrl())
                .openTime(formatTime(cafe.getOpenTime()))
                .closeTime(formatTime(cafe.getCloseTime()))
                .createdAt(cafe.getCreatedAt())
                .cafeStatus(cafe.getCafeStatus())
                .build();
    }

    public static CafeResponseDTO.EditCafeRes toEditCafeRes(
            Cafe cafe,
            boolean isNameUpdated,
            boolean isAddressUpdated,
            boolean isContactUpdated,
            boolean isIntroUpdated,
            boolean isOpenTimeUpdated,
            boolean isCloseTimeUpdated,
            boolean isImgUpdated
    ) {
        return CafeResponseDTO.EditCafeRes.builder()
                .cafeId(cafe.getId())
                .name(isNameUpdated ? cafe.getName() : null)
                .address(isAddressUpdated ? cafe.getAddress() : null)
                .latitude(isAddressUpdated ? cafe.getLatitude() : null)
                .longitude(isAddressUpdated ? cafe.getLongitude() : null)
                .contact(isContactUpdated ? cafe.getContact() : null)
                .intro(isIntroUpdated ? cafe.getIntro() : null)
                .openTime(isOpenTimeUpdated ? formatTime(cafe.getOpenTime()) : null)
                .closeTime(isCloseTimeUpdated ? formatTime(cafe.getCloseTime()) : null)
                .imgUrl(isImgUpdated ? cafe.getImgUrl() : null)
                .createdAt(formatDateTime(cafe.getCreatedAt()))
                .updatedAt(formatDateTime(cafe.getUpdatedAt()))
                .build();
    }

    public static CafeResponseDTO.PostCafeAdvRes toPostCafeAdvRes(Cafe cafe){
        return CafeResponseDTO.PostCafeAdvRes.builder()
                .cafeId(cafe.getId())
                .advImgUrl(cafe.getAdvImgUrl())
                .build();
    }

    public static CafeResponseDTO.PostStampRewardRes toPostStampRewardRes(StampReward stampReward){
        return CafeResponseDTO.PostStampRewardRes.builder()
                .stampRewardId(stampReward.getId())
                .reward(stampReward.getRewardName())
                .stampCount(stampReward.getStampCount())
                .build();
    }

    public static CafeResponseDTO.EditStampRewardRes toEditStampRewardRes(StampReward stampReward, boolean isRewardNameUpdated, boolean isStampCountUpdated){
        return CafeResponseDTO.EditStampRewardRes.builder()
                .stampRewardId(stampReward.getId())
                .reward(isRewardNameUpdated ? stampReward.getRewardName() : null)
                .stampCount(isStampCountUpdated ? stampReward.getStampCount() : null)
                .createdAt(formatDateTime(stampReward.getCreatedAt()))
                .updatedAt(formatDateTime(stampReward.getUpdatedAt()))
                .build();
    }

    public static CafeResponseDTO.GetCafeNearByRes toGetCafeNearByRes(Cafe cafe, List<Keyword> keywords, Double distance){
        List<KeywordResponseDTO.KeywordDto> keywordDtoList = keywords.stream()
                .map(KeywordConverter::toKeywordDto).toList();

        return CafeResponseDTO.GetCafeNearByRes.builder()
                .cafeId(cafe.getId())
                .name(cafe.getName())
                .address(cafe.getAddress())
                .imgUrl(cafe.getImgUrl())
                .latitude(cafe.getLatitude())
                .longitude(cafe.getLongitude())
                .distance(distance)
                .keywordList(keywordDtoList)
                .build();
    }

    public static CafeResponseDTO.SearchCafeNearByRes toSearchCafeNearByRes(
            List<CafeResponseDTO.GetCafeNearByRes> getCafeNearByList,
            String sortBy
    ) {

        return CafeResponseDTO.SearchCafeNearByRes.builder()
                .cafeList(getCafeNearByList)
                .sortBy(sortBy)
                .build();
    }

    public static CafeResponseDTO.GetCafeAdvRes toGetCafeAdvRes(Cafe cafe){
        return CafeResponseDTO.GetCafeAdvRes.builder()
                .cafeId(cafe.getId())
                .name(cafe.getName())
                .advImgUrl(cafe.getAdvImgUrl())
                .build();
    }

    public static CafeResponseDTO.SearchCafeAdvRes toSearchCafeAdvRes(
            List<CafeResponseDTO.GetCafeAdvRes> getCafeAdvList
    ) {
        return CafeResponseDTO.SearchCafeAdvRes.builder()
                .cafeAdvList(getCafeAdvList)
                .build();
    }

    public static CafeResponseDTO.GetCafeRankRes toGetCafeRankRes(List<Cafe> issueCafeList, List<Cafe> useCafeList ) {
        List<CafeResponseDTO.GetMyCafeRes> issue = issueCafeList.stream()
                .map(CafeConverter::toGetMyCafeRes).toList();
        List<CafeResponseDTO.GetMyCafeRes> use = issueCafeList.stream()
                .map(CafeConverter::toGetMyCafeRes).toList();

        return CafeResponseDTO.GetCafeRankRes.builder()
                .issue(issue)
                .use(use)
                .build();
    }

    public static CafeResponseDTO.GetCafesRes toGetCafesRes(Cafe cafe, StampBoard stampBoard, String searchBy) {
        return CafeResponseDTO.GetCafesRes.builder()
                .cafeId(cafe.getId())
                .name(cafe.getName())
                .address(cafe.getAddress())
                .imgUrl(cafe.getImgUrl())
                .searchBy(searchBy)
                .stampBoard(StampConverter.toStampBoardDto(stampBoard))
                .build();
    }

    public static CafeResponseDTO.SearchCafesRes toSearchCafesRes(List<CafeResponseDTO.GetCafesRes> cafeList) {
        return CafeResponseDTO.SearchCafesRes.builder()
                .cafeList(cafeList)
                .build();
    }
}
