package io.hirecore.hirecorememberserver.sharedkernel.application.port.out;

import java.util.Optional;

public interface LoadProfilePort {
    Optional<String> findNickname(Long accountId);
}
