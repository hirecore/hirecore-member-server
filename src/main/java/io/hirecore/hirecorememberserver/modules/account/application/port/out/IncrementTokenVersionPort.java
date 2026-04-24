package io.hirecore.hirecorememberserver.modules.account.application.port.out;

public interface IncrementTokenVersionPort {
    void incrementTokenVersion(Long memberId);
}
