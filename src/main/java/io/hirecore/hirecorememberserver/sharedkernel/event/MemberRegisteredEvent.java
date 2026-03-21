package io.hirecore.hirecorememberserver.sharedkernel.event;

import io.hirecore.hirecorememberserver.common.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.exception.SharedKernelException;
import io.hirecore.hirecorememberserver.sharedkernel.exception.SharedKernelExceptionCodeCluster.HiddenDetailResponse;
import io.hirecore.hirecorememberserver.sharedkernel.vo.OAuth2Provider;

import java.time.Instant;

/**
 * 신규 회원이 소셜 계정으로 등록되었을 때 발행되는 도메인 이벤트입니다.
 *
 * <p>회원 Aggregate Root가 소셜 계정 연동 시 생성하며,
 * 소셜 계정 이벤트 핸들러가 구독하여 소셜 계정 정보를 별도로 저장합니다.</p>
 */
public record MemberRegisteredEvent(
        Long memberAccountId,
        OAuth2Provider provider,
        String providerId,
        String email,
        Instant socialConnectedAt,
        boolean socialEmailAgreed,
        boolean socialNicknameAgreed
) {
    public MemberRegisteredEvent {
        AssertionUtils.notNull(memberAccountId, HiddenDetailResponse.MEMBER_ACCOUNT_ID_MISSING, SharedKernelException::new);
        AssertionUtils.notNull(provider, HiddenDetailResponse.PROVIDER_MISSING, SharedKernelException::new);
        AssertionUtils.notBlank(providerId, HiddenDetailResponse.PROVIDER_ID_MISSING, SharedKernelException::new);
        AssertionUtils.notBlank(email, HiddenDetailResponse.EMAIL_MISSING, SharedKernelException::new);
        AssertionUtils.notNull(socialConnectedAt, HiddenDetailResponse.SOCIAL_CONNECTED_AT_MISSING, SharedKernelException::new);
    }
}
