package com.example.springserver.global.scheduling;

import com.example.springserver.domain.admin.enums.Cycle;
import com.example.springserver.domain.admin.enums.Setting;
import com.example.springserver.domain.admin.service.AdminService;
import com.example.springserver.domain.admin.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class KeywordScheduler {

    private final AdminService adminService;
    private final SettingService settingService;

    @Scheduled(cron = "0 0 4 * * *")
    public void runScheduledKeywordUpdate() {
        Boolean isEnabled = settingService.getOrCreate(Setting.KEYWORD_ANALYSIS);
        if (!Boolean.TRUE.equals(isEnabled)) return;

        Cycle cycle = settingService.getOrCreate(Setting.KEYWORD_ANALYSIS_CYCLE);
        LocalDate today = LocalDate.now();

        switch (cycle) {
            case DAILY:
                adminService.updateAllCafeKeywords();
                break;
            case WEEKLY:
                if (today.getDayOfWeek() == DayOfWeek.MONDAY) {
                    adminService.updateAllCafeKeywords();
                }
                break;
            case MONTHLY:
                if (today.getDayOfMonth() == 1) {
                    adminService.updateAllCafeKeywords();
                }
                break;
        }
    }
}
