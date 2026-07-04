package io.hirecore.hirecorememberserver.modules.membership.application.usecase;

import io.hirecore.hirecorememberserver.modules.membership.application.port.in.LoadEntitlementStorageQuotaUseCase;
import io.hirecore.hirecorememberserver.modules.membership.application.port.out.LoadUserMembershipEntitlementPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoadEntitlementStorageQuotaUseCaseImpl implements LoadEntitlementStorageQuotaUseCase {

    private final LoadUserMembershipEntitlementPort loadUserMembershipEntitlementPort;

    @Override
    @Transactional(readOnly = true)
    public Long execute(Long memberAccountId) {
        return loadUserMembershipEntitlementPort.findStorageQuotaBytesSnapshot(memberAccountId);
    }
}
