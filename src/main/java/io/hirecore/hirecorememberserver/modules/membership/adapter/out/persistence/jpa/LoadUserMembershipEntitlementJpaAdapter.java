package io.hirecore.hirecorememberserver.modules.membership.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.membership.adapter.out.persistence.jpa.repository.UserMembershipEntitlementJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.membership.application.port.out.LoadUserMembershipEntitlementPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoadUserMembershipEntitlementJpaAdapter implements LoadUserMembershipEntitlementPort {

    private final UserMembershipEntitlementJpaQueryRepository userMembershipEntitlementJpaQueryRepository;

    @Override
    public Long getStorageQuotaBytesSnapshot(Long memberAccountId) {
        return userMembershipEntitlementJpaQueryRepository.findStorageQuotaBytesSnapshotByMemberAccountId(memberAccountId)
                .orElse(0L);
    }
}
