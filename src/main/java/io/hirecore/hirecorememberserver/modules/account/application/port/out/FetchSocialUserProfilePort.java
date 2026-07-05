package io.hirecore.hirecorememberserver.modules.account.application.port.out;

import io.hirecore.hirecorememberserver.modules.account.application.exception.SocialAccountApplicationException;
import io.hirecore.hirecorememberserver.modules.account.application.exception.SocialAccountApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.OAuth2Provider;

import java.time.Instant;

public interface FetchSocialUserProfilePort {
    Result fetchByAuthorizationCode(String authorizationCode);

    // 외부 소셜 API 프로필 결과 DTO (유입 전 필수값 검증)
    record Result(
            OAuth2Provider provider,
            String providerId,
            String email,
            String nickname,
            Instant connectedAt,
            Boolean emailAgreed,
            Boolean profileNicknameAgreed
    ) {
        public Result {
            AssertionUtils.notNull(
                    provider,
                    SocialAccountApplicationExceptionCodeCluster.DetailResponse.USER_PROFILE_PROVIDER_MISSING,
                    SocialAccountApplicationException::new);

            AssertionUtils.notNull(
                    providerId,
                    SocialAccountApplicationExceptionCodeCluster.DetailResponse.USER_PROFILE_PROVIDER_ID_MISSING,
                    SocialAccountApplicationException::new);

            AssertionUtils.notBlank(
                    email,
                    SocialAccountApplicationExceptionCodeCluster.DetailResponse.USER_PROFILE_EMAIL_MISSING,
                    SocialAccountApplicationException::new);

            AssertionUtils.notBlank(
                    nickname,
                    SocialAccountApplicationExceptionCodeCluster.DetailResponse.USER_PROFILE_NICKNAME_MISSING,
                    SocialAccountApplicationException::new);

            AssertionUtils.notNull(
                    connectedAt,
                    SocialAccountApplicationExceptionCodeCluster.DetailResponse.USER_PROFILE_CONNECTED_AT_MISSING,
                    SocialAccountApplicationException::new);

            AssertionUtils.notNull(
                    emailAgreed,
                    SocialAccountApplicationExceptionCodeCluster.DetailResponse.USER_PROFILE_CONSENT_INFO_MISSING,
                    SocialAccountApplicationException::new);

            AssertionUtils.notNull(
                    profileNicknameAgreed,
                    SocialAccountApplicationExceptionCodeCluster.DetailResponse.USER_PROFILE_CONSENT_INFO_MISSING,
                    SocialAccountApplicationException::new);
        }
    }
}
