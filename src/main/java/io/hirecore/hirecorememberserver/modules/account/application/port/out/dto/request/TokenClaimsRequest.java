package io.hirecore.hirecorememberserver.modules.account.application.port.out.dto.request;

import io.hirecore.hirecorememberserver.modules.account.domain.vo.MemberRole;

public record TokenClaimsRequest(
        Long id,
        String email,
        MemberRole role
){
}
