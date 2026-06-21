package io.hirecore.hirecorememberserver.sharedkernel.application.port.out;

public interface LoadUserStorageLimitPort {
    Long findStorageLimitBytes(Long memberAccountId);
}
