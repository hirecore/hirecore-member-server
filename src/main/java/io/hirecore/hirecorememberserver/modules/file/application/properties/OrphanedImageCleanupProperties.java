package io.hirecore.hirecorememberserver.modules.file.application.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

// ORPHANED 이미지 정리 정책 설정
@ConfigurationProperties(prefix = "hirecore.file.orphan-cleanup")
public record OrphanedImageCleanupProperties(
        // 이만큼 경과한 메타만 청소 (복구 안전망)
        Duration gracePeriod,
        // 주기당 최대 처리 수 (S3 DeleteObjects 상한 1000 권장)
        Integer batchSize
) {
}
