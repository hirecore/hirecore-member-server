package io.hirecore.hirecorememberserver.modules.account.domain.vo;

import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.account.domain.exception.MemberAccountDomainException;
import io.hirecore.hirecorememberserver.modules.account.domain.exception.MemberAccountDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.account.domain.exception.SocialAccountDomainException;
import io.hirecore.hirecorememberserver.modules.account.domain.exception.SocialAccountDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.OAuth2Provider;

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

    // 소셜 계정 연동을 위해 이메일·프로필 닉네임 제공에 모두 동의했는지 검증
    public void ensureConsentedForLink() {
        AssertionUtils.isTrue(
                emailAgreed,
                MemberAccountDomainExceptionCodeCluster.HiddenDetailResponse.SOCIAL_LINK_WITHOUT_EMAIL_AGREED,
                MemberAccountDomainException::new
        );

        AssertionUtils.isTrue(
                profileNicknameAgreed,
                MemberAccountDomainExceptionCodeCluster.HiddenDetailResponse.SOCIAL_LINK_WITHOUT_PROFILE_NICKNAME_AGREED,
                MemberAccountDomainException::new
        );
    }
}
