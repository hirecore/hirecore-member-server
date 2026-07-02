package io.hirecore.hirecorememberserver.sharedkernel.domain.event;

import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster.HiddenDetailResponse;

/**
 * 신규 회원이 소셜 계정으로 등록되었을 때 발행되는 도메인 이벤트입니다.
 *
 * <p>회원 Aggregate Root가 소셜 계정 연동 시 생성하며,
 * 소셜 계정 이벤트 핸들러가 구독하여 소셜 계정 정보를 별도로 저장합니다.</p>
 */
public record MemberAccountProfileCreatedEvent(
        Long memberAccountId,
        String email
) {
    public MemberAccountProfileCreatedEvent {
        AssertionUtils.notNull(memberAccountId, HiddenDetailResponse.MEMBER_ACCOUNT_ID_MISSING, SharedKernelException::new);
        AssertionUtils.notBlank(email, HiddenDetailResponse.EMAIL_MISSING, SharedKernelException::new);

    }
}
