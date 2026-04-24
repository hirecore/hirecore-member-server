package io.hirecore.hirecorememberserver.modules.membership.application.port.in;

/**
 * 사용자에게 부여된 권리의 스토리지 쿼타를 조회
 * */
public interface LoadEntitlementStorageQuotaUseCase {
    Long execute(Long memberAccountId);
}
