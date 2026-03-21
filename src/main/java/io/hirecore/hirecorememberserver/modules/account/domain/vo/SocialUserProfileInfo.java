package io.hirecore.hirecorememberserver.modules.account.domain.vo;

import io.hirecore.hirecorememberserver.common.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.account.domain.exception.SocialAccountDomainException;
import io.hirecore.hirecorememberserver.modules.account.domain.exception.SocialAccountDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.vo.OAuth2Provider;

import java.time.Instant;

public record SocialUserProfileInfo(
        OAuth2Provider provider,
        String providerId,
        String email,
        Instant connectedAt,
        Boolean emailAgreed,
        Boolean profileNicknameAgreed
) {
    public SocialUserProfileInfo {
        AssertionUtils.notNull(
                provider,
                SocialAccountDomainExceptionCodeCluster.HiddenDetailResponse.PROVIDER_MISSING,
                SocialAccountDomainException::new
        );

        AssertionUtils.notNull(
                providerId,
                SocialAccountDomainExceptionCodeCluster.HiddenDetailResponse.PROVIDER_ID_MISSING,
                SocialAccountDomainException::new);

        AssertionUtils.notNull(
                connectedAt,
                SocialAccountDomainExceptionCodeCluster.HiddenDetailResponse.CONNECTED_AT_MISSING,
                SocialAccountDomainException::new);

        AssertionUtils.notNull(
                emailAgreed,
                SocialAccountDomainExceptionCodeCluster.HiddenDetailResponse.CONSENT_INFO_MISSING,
                SocialAccountDomainException::new);

        AssertionUtils.notNull(
                profileNicknameAgreed,
                SocialAccountDomainExceptionCodeCluster.HiddenDetailResponse.CONSENT_INFO_MISSING,
                SocialAccountDomainException::new);
    }
}
