package io.hirecore.hirecorememberserver.modules.membership.application.port.out;

public interface LoadUserMembershipEntitlementPort {
    Long getStorageQuotaBytesSnapshot(Long memberAccountId);
}
