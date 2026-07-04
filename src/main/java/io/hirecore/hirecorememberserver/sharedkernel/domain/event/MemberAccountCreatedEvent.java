package io.hirecore.hirecorememberserver.sharedkernel.domain.event;

import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster.HiddenDetailResponse;

// 신규 소셜 회원 등록 시 발행
public record MemberAccountCreatedEvent(
        Long memberAccountId,
        String email
) {
    public MemberAccountCreatedEvent {
        AssertionUtils.notNull(memberAccountId, HiddenDetailResponse.MEMBER_ACCOUNT_ID_MISSING, SharedKernelException::new);
        AssertionUtils.notBlank(email, HiddenDetailResponse.EMAIL_MISSING, SharedKernelException::new);

    }
}
