package io.hirecore.hirecorememberserver.sharedkernel.application.port.out;

/**
 * 회원의 사용한 스토리지 용량(byte) 조회 포트.
 *
 * <p>cross-BC 노출 능력이자, storage BC 자체 내부에서도 동일 시그니처가 필요한 경우 함께 사용합니다.
 * 도메인 객체가 필요한 storage BC 내부 흐름은 별도로 {@code modules/storage/application/port/out/LoadUserStorageUsagePort}
 * ({@code Optional<UserStorageUsage>} 반환)를 사용합니다.</p>
 */
public interface LoadUserUsedQuotaPort {
    Long findUsedQuotaBytes(Long memberAccountId);
}
