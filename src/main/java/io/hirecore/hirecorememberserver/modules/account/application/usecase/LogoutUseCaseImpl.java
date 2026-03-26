package io.hirecore.hirecorememberserver.modules.account.application.usecase;

import io.hirecore.hirecorememberserver.modules.account.application.MemberAccountCommandService;
import io.hirecore.hirecorememberserver.modules.account.application.port.in.LogoutUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 로그아웃 유스케이스 구현체.
 *
 * <p>회원의 토큰 버전을 증가시켜 기존에 발급된 모든 JWT를 무효화합니다.
 * {@code JwtAuthenticationFilter}에서 토큰의 버전과 DB의 버전을 비교하여
 * 불일치 시 인증을 거부합니다.</p>
 */
@Service
@RequiredArgsConstructor
public class LogoutUseCaseImpl implements LogoutUseCase {

    private final MemberAccountCommandService memberAccountCommandService;

    @Override
    public void execute(Long memberId) {
        memberAccountCommandService.incrementTokenVersion(memberId);
    }
}
