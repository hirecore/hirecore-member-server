package io.hirecore.hirecorememberserver.modules.membership.application.port.in;

// 사용자 권리의 스토리지 쿼타 조회
public interface LoadEntitlementStorageQuotaUseCase {
    Long execute(Long memberAccountId);
}
