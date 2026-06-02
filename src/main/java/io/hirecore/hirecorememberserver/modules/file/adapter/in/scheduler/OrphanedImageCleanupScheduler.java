package io.hirecore.hirecorememberserver.modules.file.adapter.in.scheduler;

import io.hirecore.hirecorememberserver.modules.file.application.port.in.CleanupOrphanedImageUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * ORPHANED 이미지를 주기적으로 S3 에서 삭제하고 DELETED 로 마킹하는 진입점.
 *
 * <p>본 클래스는 cron 트리거만 들고 비즈니스 로직은 UseCase 에 위임합니다.
 * 어드민 도구 등에서 즉시 트리거가 필요할 경우 동일 UseCase 를 직접 호출하면 됩니다.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrphanedImageCleanupScheduler {

    private final CleanupOrphanedImageUseCase cleanupOrphanedImageUseCase;

    /**
     * 매시간 정각(UTC)에 한 주기를 실행합니다.
     * 한 주기는 batch-size 만큼만 처리하며, 후보가 더 많으면 다음 주기에 이어서 처리됩니다.
     */
    @Scheduled(cron = "0 0 * * * *", zone = "UTC")
    public void run() {
        try {
            cleanupOrphanedImageUseCase.execute();
        } catch (Exception e) {
            log.error("ORPHANED 이미지 정리 주기 실패 — 다음 주기에 재시도", e);
        }
    }
}
