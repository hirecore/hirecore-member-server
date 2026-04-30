package io.hirecore.hirecorememberserver.modules.membership.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.membership.adapter.out.persistence.jpa.repository.UserMembershipEntitlementJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.membership.application.port.out.LoadUserMembershipEntitlementPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMembershipEntitlementJpaQueryAdapter implements LoadUserMembershipEntitlementPort {

    private final UserMembershipEntitlementJpaQueryRepository userMembershipEntitlementJpaQueryRepository;

    @Override
    public Long getStorageQuotaBytesSnapshot(Long memberAccountId) {
        // todo: 차후에 수정해야할듯
        Long tempSize = 40L * (1024 * 1024);

        return userMembershipEntitlementJpaQueryRepository.findStorageQuotaBytesSnapshotByMemberAccountId(memberAccountId)
                .orElse(tempSize);
    }
}
