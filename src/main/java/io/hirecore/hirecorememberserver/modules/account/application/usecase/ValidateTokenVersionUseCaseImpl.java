package io.hirecore.hirecorememberserver.modules.account.application.usecase;

import io.hirecore.hirecorememberserver.modules.account.application.port.in.ValidateTokenVersionUseCase;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.LoadTokenVersionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ValidateTokenVersionUseCaseImpl implements ValidateTokenVersionUseCase {

    private final LoadTokenVersionPort loadTokenVersionPort;

    @Override
    @Transactional(readOnly = true)
    public boolean execute(Long memberId, int tokenVersion) {
        return loadTokenVersionPort.findCurrentTokenVersion(memberId)
                .map(current -> current == tokenVersion)
                .orElse(false);
    }
}
