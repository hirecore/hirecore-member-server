package io.hirecore.hirecorememberserver.sharedkernel.domain.event;

import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.OAuth2Provider;

import java.time.Instant;

public record MemberSocialSignedUpEvent(
         Long memberAccountId,
         OAuth2Provider provider,
         String providerId,
         String email,
         Instant socialConnectedAt,
         boolean socialEmailAgreed,
         boolean socialNicknameAgreed
) {
    public MemberSocialSignedUpEvent {
        AssertionUtils.notNull(memberAccountId, SharedKernelExceptionCodeCluster.HiddenDetailResponse.MEMBER_ACCOUNT_ID_MISSING, SharedKernelException::new);
        AssertionUtils.notNull(provider, SharedKernelExceptionCodeCluster.HiddenDetailResponse.PROVIDER_MISSING, SharedKernelException::new);
        AssertionUtils.notBlank(providerId, SharedKernelExceptionCodeCluster.HiddenDetailResponse.PROVIDER_ID_MISSING, SharedKernelException::new);
        AssertionUtils.notBlank(email, SharedKernelExceptionCodeCluster.HiddenDetailResponse.EMAIL_MISSING, SharedKernelException::new);
        AssertionUtils.notNull(socialConnectedAt, SharedKernelExceptionCodeCluster.HiddenDetailResponse.SOCIAL_CONNECTED_AT_MISSING, SharedKernelException::new);
    }
}