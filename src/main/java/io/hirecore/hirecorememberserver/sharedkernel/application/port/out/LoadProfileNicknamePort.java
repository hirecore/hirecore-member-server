package io.hirecore.hirecorememberserver.sharedkernel.application.port.out;

import java.util.Optional;

public interface LoadProfileNicknamePort {
    Optional<String> findNickname(Long accountId);
}
