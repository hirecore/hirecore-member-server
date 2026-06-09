package io.hirecore.hirecorememberserver.modules.account.application.port.out;

import java.util.Optional;

/**
 * 회원의 현재 토큰 버전을 조회하는 account BC 내부 out-port.
 *
 * <p>JWT 의 token version claim 과 비교해 로그아웃/무효화 여부를 판별하는 UseCase 가 사용한다.</p>
 */
public interface LoadTokenVersionPort {
    Optional<Integer> findCurrentTokenVersion(Long memberId);
}
