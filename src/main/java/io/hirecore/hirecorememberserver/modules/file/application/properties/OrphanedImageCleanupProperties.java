package io.hirecore.hirecorememberserver.modules.file.application.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * ORPHANED 이미지 정리 정책 설정.
 *
 * @param gracePeriod ORPHANED 마킹 시점으로부터 이만큼 경과한 메타만 청소 대상. 사용자 실수/버그 복구 안전망.
 * @param batchSize   한 주기에 처리할 최대 객체 수. S3 {@code DeleteObjects} 한 요청 상한(1000)과 일치시켜 두는 것을 권장.
 */
@ConfigurationProperties(prefix = "hirecore.file.orphan-cleanup")
public record OrphanedImageCleanupProperties(
        Duration gracePeriod,
        Integer batchSize
) {
}
