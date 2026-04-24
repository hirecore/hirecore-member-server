package io.hirecore.hirecorememberserver.sharedkernel.application.port.out;

public interface LoadUserStorageLimitPort {
    Long getBytes(Long memberAccountId);
}
