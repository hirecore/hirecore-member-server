package io.hirecore.hirecorememberserver.modules.profile.domain;

import com.github.f4b6a3.tsid.TsidCreator;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelException;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.AbstractDomainEventPublisher;
import io.hirecore.hirecorememberserver.sharedkernel.domain.DomainAggregateRoot;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.profile.domain.exception.ProfileDomainException;
import io.hirecore.hirecorememberserver.modules.profile.domain.exception.ProfileDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.profile.domain.vo.ProfileImageInfo;
import io.hirecore.hirecorememberserver.modules.profile.domain.vo.PublicCodeInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Profile extends AbstractDomainEventPublisher implements DomainAggregateRoot {
    private final Long id;
    private final Long memberAccountId;
    private final ProfileDetail profileDetail;
    private final PublicCodeInfo publicCodeInfo;
    private final String nickname;
    private final ProfileImageInfo profileImageInfo;
    private final String phoneNumber;
    private final AuditingInfo auditingInfo;

    // 복원용 빌더 (인프라 조회 데이터 → 도메인, 앱계층 호출은 ArchUnit 차단)
    @Builder(access = AccessLevel.PUBLIC)
    private Profile(
            Long id,
            Long memberAccountId,
            PublicCodeInfo publicCodeInfo,
            String nickname,
            ProfileImageInfo profileImageInfo,
            String phoneNumber,
            AuditingInfo auditingInfo,
            ProfileDetail profileDetail
    ) {
        ensureInvariants(
                id,
                memberAccountId,
                publicCodeInfo,
                nickname,
                auditingInfo,
                profileDetail
        );

        this.id = id;
        this.memberAccountId = memberAccountId;
        this.publicCodeInfo = publicCodeInfo;
        this.nickname = nickname;
        this.profileImageInfo = profileImageInfo;
        this.phoneNumber = phoneNumber;
        this.auditingInfo = auditingInfo;
        this.profileDetail = profileDetail;
    }

    // 신규 생성 팩토리 (TSID 발급·기본값 초기화)
    public static Profile createUserProfile(
            Long memberAccountId,
            PublicCodeInfo publicCodeInfo,
            String nickname,
            String phoneNumber,
            String marketingEmail
    ) {
        Long profileId = TsidCreator.getTsid().toLong();
        return Profile.builder()
                .id(profileId)
                .memberAccountId(memberAccountId)
                .publicCodeInfo(publicCodeInfo)
                .nickname(nickname)
                .profileImageInfo(ProfileImageInfo.init())
                .phoneNumber(phoneNumber)
                .auditingInfo(AuditingInfo.create())
                .profileDetail(UserProfileDetail.create(profileId, marketingEmail))
                .build();
    }

    // VALIDATION
    private static void ensureInvariants(
            Long id,
            Long memberAccountId,
            PublicCodeInfo publicCodeInfo,
            String nickname,
            AuditingInfo auditingInfo,
            ProfileDetail profileDetail
    ) {
        AssertionUtils.notNull(
                id,
                ProfileDomainExceptionCodeCluster.HiddenDetailResponse.ID_MISSING,
                ProfileDomainException::new
        );
        AssertionUtils.notNull(
                memberAccountId,
                ProfileDomainExceptionCodeCluster.HiddenDetailResponse.MEMBER_ACCOUNT_ID_MISSING,
                ProfileDomainException::new
        );
        AssertionUtils.notNull(
                publicCodeInfo,
                ProfileDomainExceptionCodeCluster.HiddenDetailResponse.PUBLIC_CODE_INFO_NULL,
                ProfileDomainException::new
        );
        AssertionUtils.notNull(
                nickname,
                ProfileDomainExceptionCodeCluster.HiddenDetailResponse.NICKNAME_MISSING,
                ProfileDomainException::new
        );
        AssertionUtils.notNull(
                auditingInfo,
                SharedKernelExceptionCodeCluster.HiddenDetailResponse.AUDITING_MISSING,
                SharedKernelException::new
        );
        AssertionUtils.notNull(
                profileDetail,
                ProfileDomainExceptionCodeCluster.HiddenDetailResponse.USER_PROFILE_NULL,
                ProfileDomainException::new
        );
    }
}
