package io.hirecore.hirecorememberserver.modules.account.application.port.out.dto.response;

import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.account.application.exception.SocialAccountApplicationException;
import io.hirecore.hirecorememberserver.modules.account.application.exception.SocialAccountApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.OAuth2Provider;

import java.time.Instant;

/**
 * 외부 소셜 API로부터 획득한 사용자 프로필 정보를 담는 출력 전용 DTO(Result) 객체입니다.
 * <p>
 * 애플리케이션 계층 내부로 데이터가 유입되기 전(생성 시점)에 필수 값을 검증하여
 * 도메인 계층이 오염되는 것을 방지합니다.
 * </p>
 */
public record SocialUserProfileResult(
        OAuth2Provider provider,
        String providerId,
        String email,
        String nickname,
        Instant connectedAt,
        Boolean emailAgreed,
        Boolean profileNicknameAgreed
) {
    public SocialUserProfileResult {
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
