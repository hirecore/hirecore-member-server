package io.hirecore.hirecorememberserver.sharedkernel.application.port.out;

public interface LoadUserStorageLimitPort {
    Long getStorageLimitBytes(Long memberAccountId);
}
