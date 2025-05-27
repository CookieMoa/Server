package com.example.springserver.global.scheduling;

import com.example.springserver.domain.admin.enums.Cycle;
import com.example.springserver.domain.admin.enums.Setting;
import com.example.springserver.domain.admin.service.AdminService;
import com.example.springserver.domain.admin.service.SettingService;
import com.example.springserver.domain.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class TrainingScheduler {

    private final AiService aiService;
    private final AdminService adminService;
    private final SettingService settingService;

    @Scheduled(cron = "0 0 4 * * *")
    public void scheduledTrainingTask() {
        Boolean isEnabled = settingService.getOrCreate(Setting.MODEL_LEARNING);
        if (!Boolean.TRUE.equals(isEnabled)) return;

        Cycle cycle = settingService.getOrCreate(Setting.MODEL_LEARNING_CYCLE);
        LocalDate today = LocalDate.now();

        switch (cycle) {
            case DAILY:
                aiService.training();
                break;
            case WEEKLY:
                if (today.getDayOfWeek() == DayOfWeek.MONDAY) {
                    aiService.training();
                }
                break;
            case MONTHLY:
                if (today.getDayOfMonth() == 1) {
                    aiService.training();
                }
                break;
        }
    }
}
