package com.jinjjaseoul.common.utils;

import com.jinjjaseoul.domain.user.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final UserRepository userRepository;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm:ss:SSS");

    // 매주 오전 6시마다 전체 유저의 활동점수 초기화
    @Scheduled(cron = "0 0 6 * * MON", zone = "Asia/Seoul")
    public void initUserRecommendNumbers() {
        log.info("현재 시간 = {}", formatter.format(LocalDateTime.now()));
        userRepository.initNumOfActive();
    }
}