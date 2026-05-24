package io.hirecore.hirecorememberserver.modules.profile.application.port.out;

import java.util.Optional;

public interface LoadProfileNicknamePort {
    Optional<String> findNickname(Long accountId);
}
