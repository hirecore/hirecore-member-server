package io.hirecore.hirecorememberserver.modules.storage.application.port.out;

public interface LoadUserUsedQuotaPort {
    Long getBytes(Long memberAccountId);
}
