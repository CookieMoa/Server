package com.example.springserver.global.scheduling;

import com.example.springserver.domain.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KeywordScheduler {

    private final AdminService adminService;

    @Scheduled(cron = "0 0 4 * * *")
    public void runDailyCafeKeywordUpdate() {
        adminService.updateAllCafeKeywords();
    }
}
