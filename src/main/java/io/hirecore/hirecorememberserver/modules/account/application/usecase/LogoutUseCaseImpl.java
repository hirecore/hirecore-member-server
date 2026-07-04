package io.hirecore.hirecorememberserver.modules.account.application.usecase;

import io.hirecore.hirecorememberserver.modules.account.application.port.in.LogoutUseCase;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.IncrementTokenVersionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 로그아웃 (토큰 버전 증가로 모든 JWT 무효화)
@Service
@RequiredArgsConstructor
public class LogoutUseCaseImpl implements LogoutUseCase {

    private final IncrementTokenVersionPort incrementTokenVersionPort;

    @Override
    @Transactional
    public void execute(Long memberId) {
        incrementTokenVersionPort.incrementTokenVersion(memberId);
    }
}
