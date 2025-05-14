package com.example.springserver.domain.customer.converter;

import com.example.springserver.domain.cafe.dto.CafeResponseDTO;
import com.example.springserver.domain.customer.dto.CustomerRequestDTO;
import com.example.springserver.domain.customer.dto.CustomerResponseDTO;
import com.example.springserver.domain.keyword.converter.KeywordConverter;
import com.example.springserver.domain.keyword.dto.KeywordResponseDTO;
import com.example.springserver.domain.stamp.converter.StampConverter;
import com.example.springserver.domain.stamp.dto.StampResponseDTO;
import com.example.springserver.entity.*;
import com.example.springserver.global.common.paging.CommonPageRes;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CustomerConverter {

    // 날짜를 포맷하는 메서드
    private static String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    public static Customer toCustomer(CustomerRequestDTO.PostCustomerReq request, UserEntity user, String imgUrl){

        return Customer.builder()
                .user(user)
                .name(request.getName())
                .imgUrl(imgUrl)
                .build();
    }

    public static CustomerResponseDTO.PostCustomerRes toPostCustomerRes(Customer customer, List<Keyword> keywords){
        List<KeywordResponseDTO.KeywordDto> keywordDtoList = keywords.stream()
                .map(KeywordConverter::toKeywordDto).toList();

        return CustomerResponseDTO.PostCustomerRes.builder()
                .customerId(customer.getId())
                .name(customer.getName())
                .imgUrl(customer.getImgUrl())
                .keywordList(keywordDtoList)
                .build();
    }

    public static CustomerResponseDTO.EditCustomerRes toEditCustomerRes(
            Customer customer,
            boolean isNameUpdated,
            boolean isImgUpdated,
            boolean isKeywordUpdated,
            List<Keyword> keywords
    ) {
        return CustomerResponseDTO.EditCustomerRes.builder()
                .customerId(customer.getId())
                .name(isNameUpdated ? customer.getName() : null)
                .imgUrl(isImgUpdated ? customer.getImgUrl() : null)
                .keywordList(isKeywordUpdated && keywords != null
                        ? keywords.stream().map(KeywordConverter::toKeywordDto).toList()
                        : null)
                .createdAt(formatDateTime(customer.getCreatedAt()))
                .updatedAt(formatDateTime(customer.getUpdatedAt()))
                .build();
    }

    public static CustomerResponseDTO.GetCustomerRes toGetCustomerRes(Customer customer, List<Keyword> keywords){
        List<KeywordResponseDTO.KeywordDto> keywordDtoList = keywords.stream()
                .map(KeywordConverter::toKeywordDto).toList();

        return CustomerResponseDTO.GetCustomerRes.builder()
                .customerId(customer.getId())
                .name(customer.getName())
                .imgUrl(customer.getImgUrl())
                .keywordList(keywordDtoList)
                .build();
    }

    public static CustomerResponseDTO.GetCustomerDetailRes toGetCustomerDetailRes(
                Customer customer,
                List<Keyword> keywords,
                Long visitedCafeCount,
                Long totalStampCount,
                Long totalUsedStampCount,
                List<CafeResponseDTO.GetCafeReviewRes> maliciousReviewList,
                List<CafeResponseDTO.GetCafeReviewRes> reviewList
    ) {
        List<KeywordResponseDTO.KeywordDto> keywordDtoList = keywords.stream()
                .map(KeywordConverter::toKeywordDto).toList();

        return CustomerResponseDTO.GetCustomerDetailRes.builder()
                .customerId(customer.getId())
                .name(customer.getName())
                .imgUrl(customer.getImgUrl())
                .keywordList(keywordDtoList)
                .email(customer.getUser().getUsername())
                .visitedCafeCount(visitedCafeCount)
                .totalStampCount(totalStampCount)
                .totalUsedStampCount(totalUsedStampCount)
                .maliciousReviewList(maliciousReviewList)
                .reviewList(reviewList)
                .accountStatus(customer.getUser().getAccountStatus())
                .createdAt(customer.getCreatedAt())
                .build();
    }

    public static CustomerResponseDTO.GetCustomerRes toSimpleGetCustomerRes(Customer customer) {
        return CustomerResponseDTO.GetCustomerRes.builder()
                .customerId(customer.getId())
                .name(customer.getName())
                .build();
    }

    public static CustomerResponseDTO.SearchCustomerRes toSearchCustomerRes(Page<Customer> customerList) {

        List<CustomerResponseDTO.GetCustomerRes> getSimpleCustomerResList = customerList.stream()
                .map(CustomerConverter::toSimpleGetCustomerRes).toList();

        CommonPageRes commonPageRes = new CommonPageRes(
                customerList.getTotalElements(),   // 총 개수 (count)
                customerList.getSize(),           // 페이지 당 개수 (limit)
                customerList.getNumber()          // 현재 페이지 번호 (page)
        );

        return CustomerResponseDTO.SearchCustomerRes.builder()

                .customerList(getSimpleCustomerResList)
                .count(commonPageRes.getCount())
                .limit(commonPageRes.getLimit())
                .page(commonPageRes.getPage())
                .build();
    }

    public static CustomerResponseDTO.GetQrcodeRes toGetQrcodeRes(String qrCodeBase64) {
        return CustomerResponseDTO.GetQrcodeRes.builder()
                .qrCodeBase64(qrCodeBase64)
                .build();
    }

    public static CustomerResponseDTO.SearchStampBoardsRes toSearchStampBoardsRes(Page<StampBoard> stampBoardList) {

        List<StampResponseDTO.GetStampBoardRes> getStampBoardResList = stampBoardList.stream()
                .map(stampBoard -> {
                    int availableStamps = stampBoard.getStampsCount() - stampBoard.getUsedStamps();

                    // 카페의 StampReward 중에서 availableStamps보다 크거나 같은 것 중 가장 가까운 목표
                    // 만약 모든 목표보다 availableStamps이 크다면 가장 큰 목표 반환
                    List<Integer> stampGoals = stampBoard.getCafe().getStampRewards().stream()
                            .map(StampReward::getStampCount)
                            .sorted()
                            .toList();

                    Integer stampGoal = stampGoals.stream()
                            .filter(goal -> goal >= availableStamps)
                            .findFirst()
                            .orElseGet(() -> stampGoals.isEmpty() ? null : stampGoals.get(stampGoals.size() - 1));

                    return StampConverter.toGetStampBoardRes(stampBoard, stampGoal);
                })
                .toList();

        return CustomerResponseDTO.SearchStampBoardsRes.builder()

                .stampBoardList(getStampBoardResList)
                .count(stampBoardList.getTotalElements())
                .limit(stampBoardList.getSize())
                .page(stampBoardList.getNumber())
                .build();
    }

    // SearchPendingReviewRes DTO
    public static List<CustomerResponseDTO.GetPendingReviewRes> toSearchPendingReviewRes(List<StampLog> stampLogList) {

        return stampLogList.stream()
                .map(stampLog -> {
                    Cafe cafe = stampLog.getStampBoard().getCafe();

                    return CustomerResponseDTO.GetPendingReviewRes.builder()
                            .stampLogId(stampLog.getId())
                            .cafeId(cafe.getId())
                            .cafeName(cafe.getName())
                            .date(formatDateTime(stampLog.getCreatedAt()))
                            .build();
                })
                .toList();
    }
}
