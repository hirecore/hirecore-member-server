package io.hirecore.hirecorememberserver.modules.account.domain;

import com.github.f4b6a3.tsid.TsidCreator;
import io.hirecore.hirecorememberserver.common.domain.exception.CommonDomainException;
import io.hirecore.hirecorememberserver.common.domain.exception.DomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.AbstractDomainEventPublisher;
import io.hirecore.hirecorememberserver.sharedkernel.vo.AuditingInfo;
import io.hirecore.hirecorememberserver.sharedkernel.DomainAggregateRoot;
import io.hirecore.hirecorememberserver.modules.account.domain.exception.SocialAccountDomainException;
import io.hirecore.hirecorememberserver.modules.account.domain.exception.SocialAccountDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.vo.OAuth2Provider;
import io.hirecore.hirecorememberserver.common.utils.AssertionUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * 소셜 계정(Social Account) Aggregate Root.
 *
 * <p>회원({@link MemberAccount})과 연동된 소셜 로그인 계정 정보를 관리합니다.
 * OAuth2 제공자, 제공자 식별자, 이메일, 동의 내역 등을 불변으로 유지합니다.</p>
 */
@Getter
public class SocialAccount extends AbstractDomainEventPublisher implements DomainAggregateRoot {
    private final Long id;
    private final Long memberAccountId;
    private final OAuth2Provider provider;
    private final String providerId;
    private final String email;
    private final Instant connectedAt;
    private final Boolean emailAgreed;
    private final Boolean profileNicknameAgreed;
    private final AuditingInfo auditingInfo;

    /**
     * [복원용 빌더]
     * 데이터베이스 등 외부 인프라에서 조회된 데이터를 도메인 객체로 복원할 때만 사용해야 합니다.
     * Application 계층에서의 임의 호출은 ArchUnit 테스트에 의해 차단됩니다.
     */
    @Builder(access = AccessLevel.PUBLIC)
    private SocialAccount(
            Long id,
            Long memberAccountId,
            OAuth2Provider provider,
            String providerId,
            String email,
            Instant connectedAt,
            Boolean emailAgreed,
            Boolean profileNicknameAgreed,
            AuditingInfo auditingInfo
    ) {
        ensureInvariants(
                id,
                memberAccountId,
                provider,
                providerId,
                email,
                connectedAt,
                emailAgreed,
                profileNicknameAgreed,
                auditingInfo
        );

        this.id = id;
        this.memberAccountId = memberAccountId;
        this.provider = provider;
        this.providerId = providerId;
        this.email = email;
        this.emailAgreed = emailAgreed;
        this.profileNicknameAgreed = profileNicknameAgreed;
        this.connectedAt = connectedAt;
        this.auditingInfo = auditingInfo;
    }

    public static SocialAccount create(
            Long memberAccountId,
            OAuth2Provider provider,
            String providerId,
            String email,
            Instant connectedAt,
            Boolean isEmailAgreed,
            Boolean isProfileNicknameAgreed
    ) {
        return SocialAccount.builder()
                .id(TsidCreator.getTsid().toLong())
                .memberAccountId(memberAccountId)
                .provider(provider)
                .providerId(providerId)
                .email(email)
                .connectedAt(connectedAt)
                .emailAgreed(isEmailAgreed)
                .profileNicknameAgreed(isProfileNicknameAgreed)
                .auditingInfo(AuditingInfo.create())
                .build();
    }

    private static void ensureInvariants(
            Long id,
            Long memberAccountId,
            OAuth2Provider provider,
            String providerId,
            String email,
            Instant connectedAt,
            Boolean emailAgreed,
            Boolean profileNicknameAgreed,
            AuditingInfo auditingInfo
    ) {
        AssertionUtils.notNull(
                id,
                SocialAccountDomainExceptionCodeCluster.HiddenDetailResponse.ID_MISSING,
                SocialAccountDomainException::new);
        AssertionUtils.notNull(
                memberAccountId,
                SocialAccountDomainExceptionCodeCluster.HiddenDetailResponse.MEMBER_ACCOUNT_ID_MISSING,
                SocialAccountDomainException::new);
        AssertionUtils.notNull(
                provider,
                SocialAccountDomainExceptionCodeCluster.HiddenDetailResponse.PROVIDER_MISSING,
                SocialAccountDomainException::new);
        AssertionUtils.notNull(
                providerId,
                SocialAccountDomainExceptionCodeCluster.HiddenDetailResponse.PROVIDER_ID_MISSING,
                SocialAccountDomainException::new);
        AssertionUtils.notNull(
                email,
                SocialAccountDomainExceptionCodeCluster.HiddenDetailResponse.EMAIL_MISSING,
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
        AssertionUtils.notNull(
                auditingInfo,
                DomainExceptionCodeCluster.HiddenDetailResponse.AUDITING_MISSING,
                CommonDomainException::new
        );
    }
}
