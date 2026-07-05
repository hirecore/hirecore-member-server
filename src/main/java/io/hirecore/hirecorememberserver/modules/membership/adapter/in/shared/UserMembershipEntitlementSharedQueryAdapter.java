package io.hirecore.hirecorememberserver.modules.membership.adapter.in.shared;

import io.hirecore.hirecorememberserver.modules.membership.application.port.in.LoadEntitlementStorageQuotaUseCase;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadUserStorageLimitSharedPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMembershipEntitlementSharedQueryAdapter implements LoadUserStorageLimitSharedPort {

    private final LoadEntitlementStorageQuotaUseCase loadEntitlementStorageQuotaUseCase;

    @Override
    public Long findStorageLimitBytes(Long memberAccountId) {
        return loadEntitlementStorageQuotaUseCase.execute(memberAccountId);
    }
}
