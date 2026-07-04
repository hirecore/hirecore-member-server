package io.hirecore.hirecorememberserver.modules.account.domain;

import com.github.f4b6a3.tsid.TsidCreator;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.AbstractDomainEventPublisher;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import io.hirecore.hirecorememberserver.sharedkernel.domain.DomainAggregateRoot;
import io.hirecore.hirecorememberserver.modules.account.domain.exception.SocialAccountDomainException;
import io.hirecore.hirecorememberserver.modules.account.domain.exception.SocialAccountDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.OAuth2Provider;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

// 소셜 계정 Aggregate Root
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

    // 복원용 빌더 (인프라 조회 데이터 → 도메인, 앱계층 호출은 ArchUnit 차단)
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
                SharedKernelExceptionCodeCluster.HiddenDetailResponse.AUDITING_MISSING,
                SharedKernelException::new
        );
    }
}
