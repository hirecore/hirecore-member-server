package io.hirecore.hirecorememberserver.modules.membership.application.port.out;

public interface LoadUserMembershipEntitlement {
    Long getStorageQuotaBytesSnapshot(Long memberAccountId);
}
