package io.hirecore.hirecorememberserver.sharedkernel.application.port.out;

public interface LoadUserStorageLimitSharedPort {
    Long findStorageLimitBytes(Long memberAccountId);
}
