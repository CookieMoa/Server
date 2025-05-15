package com.example.springserver.domain.ai.converter;

import com.example.springserver.domain.ai.dto.AiResponseDTO;

import java.util.List;

public class AiConverter {
    public static AiResponseDTO.GetKeywordsResultRes toKeywordsResultRes(List<String> predicted_keywords){
        return AiResponseDTO.GetKeywordsResultRes.builder()
                .predicted_keywords(predicted_keywords)
                .build();
    }
}
