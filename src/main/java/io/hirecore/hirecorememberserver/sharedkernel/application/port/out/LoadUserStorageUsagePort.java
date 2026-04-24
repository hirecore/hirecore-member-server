package io.hirecore.hirecorememberserver.sharedkernel.application.port.out;

public interface LoadUserStorageUsagePort {
    Long getBytes(Long memberAccountId);
}
