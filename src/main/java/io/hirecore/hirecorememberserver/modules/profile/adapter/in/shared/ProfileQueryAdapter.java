package io.hirecore.hirecorememberserver.modules.profile.adapter.in.shared;

import io.hirecore.hirecorememberserver.modules.profile.application.port.in.LoadProfileNicknameUseCase;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadProfilePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProfileQueryAdapter implements LoadProfilePort {

    private final LoadProfileNicknameUseCase loadProfileNicknameUseCase;

    public Optional<String> findNickname(Long accountId) {
        return loadProfileNicknameUseCase.execute(accountId);
    }
}
