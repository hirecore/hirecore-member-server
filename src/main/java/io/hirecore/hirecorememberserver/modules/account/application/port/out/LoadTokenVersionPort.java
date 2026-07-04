package io.hirecore.hirecorememberserver.modules.account.application.port.out;

import java.util.Optional;

// 회원의 현재 토큰 버전 조회 out-port
public interface LoadTokenVersionPort {
    Optional<Integer> findCurrentTokenVersion(Long memberId);
}
