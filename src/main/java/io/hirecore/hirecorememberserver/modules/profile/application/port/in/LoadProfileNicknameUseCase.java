package io.hirecore.hirecorememberserver.modules.profile.application.port.in;

import java.util.Optional;

public interface LoadProfileNicknameUseCase {
    Optional<String> execute(Long accountId);
}
