package io.hirecore.hirecorememberserver.modules.account.application.port.out;

import io.hirecore.hirecorememberserver.modules.account.application.port.in.dto.response.PairTokenResponse;
import io.hirecore.hirecorememberserver.modules.account.domain.vo.MemberRole;

public interface IssueTokenPort {
    PairTokenResponse issueTokenPair(Request request);

    record Request(
            Long id,
            String email,
            MemberRole role,
            int tokenVersion
    ) {
    }
}
