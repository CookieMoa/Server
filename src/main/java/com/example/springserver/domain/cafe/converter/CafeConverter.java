package com.example.springserver.domain.cafe.converter;

import com.example.springserver.domain.cafe.dto.CafeRequestDTO;
import com.example.springserver.domain.cafe.dto.CafeResponseDTO;
import com.example.springserver.entity.Cafe;
import com.example.springserver.entity.StampBoard;
import com.example.springserver.entity.StampReward;
import com.example.springserver.entity.UserEntity;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CafeConverter {

    private CafeConverter() {
        throw new UnsupportedOperationException("Utility class");
    }

    // 날짜를 포맷하는 메서드
    private static String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
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

    public static CafeResponseDTO.PostCafeRes toPostCafeRes(Cafe cafe){
        return CafeResponseDTO.PostCafeRes.builder()
                .cafeId(cafe.getId())
                .name(cafe.getName())
                .address(cafe.getAddress())
                .latitude(cafe.getLatitude())
                .longitude(cafe.getLongitude())
                .contact(cafe.getContact())
                .intro(cafe.getIntro())
                .imgUrl(cafe.getImgUrl())
                .build();
    }

    public static CafeResponseDTO.EditCafeRes toEditCafeRes(
            Cafe cafe,
            boolean isNameUpdated,
            boolean isAddressUpdated,
            boolean isContactUpdated,
            boolean isIntroUpdated,
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
}
