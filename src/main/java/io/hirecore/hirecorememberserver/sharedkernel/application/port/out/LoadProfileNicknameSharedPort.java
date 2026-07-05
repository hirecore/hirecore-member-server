package io.hirecore.hirecorememberserver.sharedkernel.application.port.out;

import java.util.Optional;

public interface LoadProfileNicknameSharedPort {
    Optional<String> findNickname(Long accountId);
}
