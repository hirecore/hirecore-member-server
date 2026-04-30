package io.hirecore.hirecorememberserver.sharedkernel.application.port.out;

public interface LoadUserStorageUsagePort {
    Long getUsedQuotaBytes(Long memberAccountId);
}
