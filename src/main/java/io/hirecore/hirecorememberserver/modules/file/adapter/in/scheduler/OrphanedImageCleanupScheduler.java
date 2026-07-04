package io.hirecore.hirecorememberserver.modules.file.adapter.in.scheduler;

import io.hirecore.hirecorememberserver.modules.file.application.port.in.CleanupOrphanedImageUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// ORPHANED 이미지 정리 cron 진입점 (로직은 UseCase 에 위임)
@Slf4j
@Component
@RequiredArgsConstructor
public class OrphanedImageCleanupScheduler {

    private final CleanupOrphanedImageUseCase cleanupOrphanedImageUseCase;

    // 매시 정각(UTC) 실행, 한 주기당 batch-size 만큼만 처리
    @Scheduled(cron = "0 0 * * * *", zone = "UTC")
    public void run() {
        try {
            cleanupOrphanedImageUseCase.execute();
        } catch (Exception e) {
            log.error("ORPHANED 이미지 정리 주기 실패 — 다음 주기에 재시도", e);
        }
    }
}
