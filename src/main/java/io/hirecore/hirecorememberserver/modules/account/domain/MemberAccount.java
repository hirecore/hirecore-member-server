package io.hirecore.hirecorememberserver.modules.account.domain;

import com.github.f4b6a3.tsid.TsidCreator;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.CommonDomainException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.DomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.AbstractDomainEventPublisher;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import io.hirecore.hirecorememberserver.sharedkernel.domain.DomainAggregateRoot;
import io.hirecore.hirecorememberserver.modules.account.domain.exception.MemberAccountDomainException;
import io.hirecore.hirecorememberserver.modules.account.domain.exception.MemberAccountDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.account.domain.vo.MemberRole;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.account.domain.vo.SocialUserProfileInfo;
import io.hirecore.hirecorememberserver.sharedkernel.domain.event.MemberRegisteredEvent;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

/**
 * 회원 계정(Member Account) Aggregate Root.
 *
 * <p>회원의 식별자, 이메일, 권한({@link MemberRole}), 생명주기를 관리합니다.
 * 소셜 계정 연동 시 {@link MemberRegisteredEvent}를 발행합니다.</p>
 *
 * <p>불변식: 이메일·권한은 필수이며, 소셜 연동 시 이메일·닉네임 동의가 반드시 필요합니다.</p>
 */
@Getter
public class MemberAccount extends AbstractDomainEventPublisher implements DomainAggregateRoot {
    private final Long id;
    private final String email;
    private final String password;
    private final MemberRole role;
    private final int tokenVersion;
    private final AuditingInfo auditingInfo;

    /**
     * [복원용 빌더]
     * 데이터베이스 등 외부 인프라에서 조회된 데이터를 도메인 객체로 복원할 때만 사용해야 합니다.
     * Application 계층에서의 임의 호출은 ArchUnit 테스트에 의해 차단됩니다.
     */
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

    /**
     * 이메일 데이터는 적재하고, 패스워드는 null인 {@link MemberAccount}를 생성합니다.
     */
    public static MemberAccount create(String email, MemberRole role) {
        return MemberAccount.builder()
                .id(TsidCreator.getTsid().toLong())
                .email(email)
                .password(null)
                .role(role)
                .auditingInfo(AuditingInfo.create())
                .build();
    }

    /**
     * 소셜 로그인으로 신규 가입하는 회원을 생성하고, 소셜 계정 연동 이벤트를 등록합니다.
     */
    public static MemberAccount createWithSocialLink(
            String email,
            MemberRole role,
            SocialUserProfileInfo socialInfo
    ) {
        MemberAccount newMember = create(email, role);
        newMember.linkSocialAccount(socialInfo);
        return newMember;
    }

    /**
     * 소셜 계정을 연동하고 {@link MemberRegisteredEvent}를 등록합니다.
     */
    private void linkSocialAccount(SocialUserProfileInfo socialUserProfileInfo) {
        ensureSocialLinkInvariants(socialUserProfileInfo);

        registerEvent(new MemberRegisteredEvent(
                this.id,
                socialUserProfileInfo.provider(),
                socialUserProfileInfo.providerId(),
                socialUserProfileInfo.email(),
                socialUserProfileInfo.connectedAt(),
                socialUserProfileInfo.emailAgreed(),
                socialUserProfileInfo.profileNicknameAgreed()
        ));
    }

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
                DomainExceptionCodeCluster.HiddenDetailResponse.AUDITING_MISSING,
                CommonDomainException::new
        );
    }

    /**
     * {@link SocialUserProfileInfo} VO는 생성 시점에 필드 null 검증을 완료하므로,
     * 여기서는 소셜 연동에 필요한 Aggregate Root 수준의 비즈니스 규칙(동의 여부)만 검증합니다.
     */
    private static void ensureSocialLinkInvariants(SocialUserProfileInfo socialUserProfileInfo) {
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
