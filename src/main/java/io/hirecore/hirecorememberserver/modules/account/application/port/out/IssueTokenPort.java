package io.hirecore.hirecorememberserver.modules.account.application.port.out;

import io.hirecore.hirecorememberserver.modules.account.domain.vo.MemberRole;

public interface IssueTokenPort {
    Result issueTokenPair(Request request);

    record Request(
            Long id,
            String email,
            MemberRole role,
            int tokenVersion
    ) {
    }

    // 발급된 액세스/리프레시 토큰 쌍
    record Result(
            String accessToken,
            String refreshToken
    ) {
    }
}
