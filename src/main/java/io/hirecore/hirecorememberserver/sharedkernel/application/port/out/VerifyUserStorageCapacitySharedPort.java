package io.hirecore.hirecorememberserver.sharedkernel.application.port.out;

public interface VerifyUserStorageCapacitySharedPort {
    void verifyCapacityFor(Long memberAccountId, Long uploadFileSizeBytes);
}
