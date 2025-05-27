package com.example.springserver.domain.admin.service;


import com.example.springserver.domain.admin.converter.AdminConverter;
import com.example.springserver.domain.admin.dto.AdminResponseDTO;
import com.example.springserver.domain.admin.enums.Cycle;
import com.example.springserver.domain.admin.enums.Setting;
import com.example.springserver.domain.ai.service.AiService;
import com.example.springserver.domain.cafe.converter.CafeConverter;
import com.example.springserver.domain.cafe.dto.CafeResponseDTO;
import com.example.springserver.domain.cafe.repository.CafeRepository;
import com.example.springserver.domain.cafe.service.CafeService;
import com.example.springserver.domain.cafe.service.ReviewService;
import com.example.springserver.domain.customer.converter.CustomerConverter;
import com.example.springserver.domain.customer.dto.CustomerResponseDTO;
import com.example.springserver.domain.customer.repository.CustomerRepository;
import com.example.springserver.domain.customer.service.CustomerService;
import com.example.springserver.domain.keyword.service.KeywordService;
import com.example.springserver.domain.log.enums.StampLogStatus;
import com.example.springserver.domain.log.repository.StampLogRepository;
import com.example.springserver.domain.log.service.StampLogService;
import com.example.springserver.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SettingService {

    private final RedisTemplate<String, String> redisTemplate;

    @SuppressWarnings("unchecked")
    private <T> T convertValue(String raw, Class<T> type) {
        if (type == Boolean.class) return (T) Boolean.valueOf(raw);
        if (type == Integer.class) return (T) Integer.valueOf(raw);
        if (type == String.class) return (T) raw;
        if (type.isEnum()) {
            for (T constant : type.getEnumConstants()) {
                if (constant.toString().equalsIgnoreCase(raw)) {
                    return constant;
                }
            }
        }
        throw new IllegalArgumentException("Unsupported type: " + type.getName());
    }


    public <T> T getOrCreate(Setting setting) {
        String key = setting.name();
        String value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            String defaultString = setting.getDefaultValue().toString();
            redisTemplate.opsForValue().set(key, defaultString);
            value = defaultString;
        }

        return convertValue(value, (Class<T>) setting.getValueType());
    }

    public AdminResponseDTO.GetSettingRes  getSetting() {
        Boolean isModelLearning = getOrCreate(Setting.MODEL_LEARNING);
        Cycle modelLearningCycle = getOrCreate(Setting.MODEL_LEARNING_CYCLE);
        Boolean isKeywordAnalysis = getOrCreate(Setting.KEYWORD_ANALYSIS);
        Cycle keywordAnalysisCycle = getOrCreate(Setting.KEYWORD_ANALYSIS);
        Boolean isBlockRepeatedAbuser = getOrCreate(Setting.BLOCK_REPEATED_ABUSER);
        Integer abuseThreshold = getOrCreate(Setting.ABUSE_THRESHOLD);
        Boolean isBlockMaliciousUser = getOrCreate(Setting.BLOCK_MALICIOUS_USER);
        Integer maliciousThreshold = getOrCreate(Setting.MALICIOUS_THRESHOLD);
        Boolean isDetectMaliciousReview = getOrCreate(Setting.DETECT_MALICIOUS_REVIEW);

        return AdminConverter.toSettingRes(isModelLearning,
                modelLearningCycle,
                isKeywordAnalysis,
                keywordAnalysisCycle,
                isBlockRepeatedAbuser,
                abuseThreshold,
                isBlockMaliciousUser,
                maliciousThreshold,
                isDetectMaliciousReview);

    }

    public void updateSetting(Setting setting, String value) {
        try {
            Object typedValue = convertValue(value, setting.getValueType());
            redisTemplate.opsForValue().set(setting.name(), typedValue.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
