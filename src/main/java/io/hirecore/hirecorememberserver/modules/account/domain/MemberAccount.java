package io.hirecore.hirecorememberserver.modules.account.domain;

import com.github.f4b6a3.tsid.TsidCreator;
import io.hirecore.hirecorememberserver.sharedkernel.domain.event.MemberAccountProfileCreatedEvent;
import io.hirecore.hirecorememberserver.sharedkernel.domain.event.MemberAccountSocialAccountCreatedEvent;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.AbstractDomainEventPublisher;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import io.hirecore.hirecorememberserver.sharedkernel.domain.DomainAggregateRoot;
import io.hirecore.hirecorememberserver.modules.account.domain.exception.MemberAccountDomainException;
import io.hirecore.hirecorememberserver.modules.account.domain.exception.MemberAccountDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.account.domain.vo.MemberRole;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.account.domain.vo.SocialUserProfileInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberAccount extends AbstractDomainEventPublisher implements DomainAggregateRoot {
    private final Long id;
    private final String email;
    private final String password;
    private final MemberRole role;
    private final int tokenVersion;
    private final AuditingInfo auditingInfo;

    @Builder(access = AccessLevel.PUBLIC)
    private MemberAccount(
            Long id,
            String email,
            String password,
            MemberRole role,
            int tokenVersion,
            AuditingInfo auditingInfo
    ) {
        ensureInvariants(id, email, role, auditingInfo);

        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.tokenVersion = tokenVersion;
        this.auditingInfo = auditingInfo;
    }

    private static MemberAccount create(String email, String password, MemberRole role) {
        return MemberAccount.builder()
                .id(TsidCreator.getTsid().toLong())
                .email(email)
                .password(password)
                .role(role)
                .auditingInfo(AuditingInfo.create())
                .build();
    }

    // 소셜 계정 회원가입
    public static MemberAccount createWithSocial(
            String email,
            MemberRole role,
            SocialUserProfileInfo socialInfo
    ) {
        MemberAccount newMember = create(email, null, role);
        newMember.createSocialAccount(socialInfo);
        newMember.createProfile(newMember.getId(), email);
        return newMember;
    }
    // 프로필 생성
    private void createProfile(Long memberId, String email) {
        AssertionUtils.notNull(
                memberId,
                MemberAccountDomainExceptionCodeCluster.HiddenDetailResponse.ID_MISSING,
                MemberAccountDomainException::new
        );
        registerEvent(new MemberAccountProfileCreatedEvent(memberId, email));
    }

    // 소셜 계정 연결 이벤트 등록
    private void createSocialAccount(SocialUserProfileInfo socialUserProfileInfo) {
        ensureCreateSocialAccountInvariants(socialUserProfileInfo);

        registerEvent(new MemberAccountSocialAccountCreatedEvent(
                this.id,
                socialUserProfileInfo.provider(),
                socialUserProfileInfo.providerId(),
                socialUserProfileInfo.email(),
                socialUserProfileInfo.connectedAt(),
                socialUserProfileInfo.emailAgreed(),
                socialUserProfileInfo.profileNicknameAgreed()
        ));
    }

    // 도메인 객체 생성 불변성 검증
    private static void ensureInvariants(Long id, String email, MemberRole role, AuditingInfo auditingInfo) {
        AssertionUtils.notNull(
                id,
                MemberAccountDomainExceptionCodeCluster.HiddenDetailResponse.ID_MISSING,
                MemberAccountDomainException::new);

        AssertionUtils.notBlank(
                email,
                MemberAccountDomainExceptionCodeCluster.HiddenDetailResponse.EMAIL_MISSING,
                MemberAccountDomainException::new);

        AssertionUtils.notNull(
                role,
                MemberAccountDomainExceptionCodeCluster.HiddenDetailResponse.ROLE_MISSING,
                MemberAccountDomainException::new);

        AssertionUtils.notNull(
                auditingInfo,
                SharedKernelExceptionCodeCluster.HiddenDetailResponse.AUDITING_MISSING,
                SharedKernelException::new
        );
    }

    // 소셜 계정 연결 불변성 검증
    private static void ensureCreateSocialAccountInvariants(SocialUserProfileInfo socialUserProfileInfo) {
        AssertionUtils.isTrue(
                socialUserProfileInfo.emailAgreed(),
                MemberAccountDomainExceptionCodeCluster.HiddenDetailResponse.SOCIAL_LINK_WITHOUT_EMAIL_AGREED,
                MemberAccountDomainException::new
        );

        AssertionUtils.isTrue(
                socialUserProfileInfo.profileNicknameAgreed(),
                MemberAccountDomainExceptionCodeCluster.HiddenDetailResponse.SOCIAL_LINK_WITHOUT_PROFILE_NICKNAME_AGREED,
                MemberAccountDomainException::new
        );
    }
}
