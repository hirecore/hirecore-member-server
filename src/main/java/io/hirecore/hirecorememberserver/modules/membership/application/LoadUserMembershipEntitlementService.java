package io.hirecore.hirecorememberserver.modules.membership.application;

import io.hirecore.hirecorememberserver.modules.membership.application.port.out.LoadUserMembershipEntitlement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoadUserMembershipEntitlementService {

    private final LoadUserMembershipEntitlement loadUserMembershipEntitlement;

    public Long loadStorageQuotaBytesSnapshot(Long memberAccountId){
        loadUserMembershipEntitlement.getStorageQuotaBytesSnapshot(memberAccountId);
        return null;
    }
}
