package com.example.springserver.global.scheduling;

import com.example.springserver.domain.admin.service.AdminService;
import com.example.springserver.domain.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainingScheduler {

    private final AiService aiService;

    @Scheduled(cron = "0 0 4 1 * *")
    public void runMonthlyCafeKeywordUpdate() {
        aiService.training();
    }
}
