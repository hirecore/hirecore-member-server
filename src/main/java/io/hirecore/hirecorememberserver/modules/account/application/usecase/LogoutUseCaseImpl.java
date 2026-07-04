package io.hirecore.hirecorememberserver.modules.account.application.usecase;

import io.hirecore.hirecorememberserver.modules.account.application.MemberAccountCommandService;
import io.hirecore.hirecorememberserver.modules.account.application.port.in.LogoutUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 로그아웃 (토큰 버전 증가로 모든 JWT 무효화)
@Service
@RequiredArgsConstructor
public class LogoutUseCaseImpl implements LogoutUseCase {

    private final MemberAccountCommandService memberAccountCommandService;

    @Override
    public void execute(Long memberId) {
        memberAccountCommandService.incrementTokenVersion(memberId);
    }
}
