package io.hirecore.hirecorememberserver.modules.membership.adapter.in.shared;

import io.hirecore.hirecorememberserver.modules.membership.application.port.in.LoadEntitlementStorageQuotaUseCase;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadUserStorageLimitPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMembershipEntitlementAdapter implements LoadUserStorageLimitPort {

    private final LoadEntitlementStorageQuotaUseCase loadEntitlementStorageQuotaUseCase;

    @Override
    public Long getBytes(Long memberAccountId) {
        return loadEntitlementStorageQuotaUseCase.execute(memberAccountId);
    }
}
