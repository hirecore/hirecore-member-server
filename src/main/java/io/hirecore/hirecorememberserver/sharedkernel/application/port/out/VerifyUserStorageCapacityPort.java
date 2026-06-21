package io.hirecore.hirecorememberserver.sharedkernel.application.port.out;

public interface VerifyUserStorageCapacityPort {
    void verifyCapacityFor(Long memberAccountId, Long uploadFileSizeBytes);
}
