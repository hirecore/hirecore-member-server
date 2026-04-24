package io.hirecore.hirecorememberserver.modules.membership.application;

import io.hirecore.hirecorememberserver.modules.membership.application.port.out.LoadUserMembershipEntitlementPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoadUserMembershipEntitlementService {

    private final LoadUserMembershipEntitlementPort loadUserMembershipEntitlementPort;

    public Long loadStorageQuotaBytesSnapshot(Long memberAccountId){
        loadUserMembershipEntitlementPort.getStorageQuotaBytesSnapshot(memberAccountId);
        return null;
    }
}
