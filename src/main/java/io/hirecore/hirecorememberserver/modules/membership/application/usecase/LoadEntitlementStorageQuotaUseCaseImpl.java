package io.hirecore.hirecorememberserver.modules.membership.application.usecase;

import io.hirecore.hirecorememberserver.modules.membership.application.LoadUserMembershipEntitlementService;
import io.hirecore.hirecorememberserver.modules.membership.application.port.in.LoadEntitlementStorageQuotaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoadEntitlementStorageQuotaUseCaseImpl implements LoadEntitlementStorageQuotaUseCase {

    private final LoadUserMembershipEntitlementService loadUserMembershipEntitlementService;

    @Override
    @Transactional(readOnly = true)
    public Long execute(Long memberAccountId) {
        return loadUserMembershipEntitlementService.loadStorageQuotaBytesSnapshot(memberAccountId);
    }
}
