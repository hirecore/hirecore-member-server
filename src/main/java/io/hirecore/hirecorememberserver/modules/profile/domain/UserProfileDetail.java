package io.hirecore.hirecorememberserver.modules.profile.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public final class UserProfileDetail implements ProfileDetail {
    private final Long profileId;
    private final String marketingEmail;

    // 복원용 빌더 (인프라 조회 데이터 → 도메인, 앱계층 호출은 ArchUnit 차단)
    @Builder(access = AccessLevel.PUBLIC)
    private UserProfileDetail(
            Long profileId,
            String marketingEmail
    ) {
        ensureInvariants(profileId);

        this.profileId = profileId;
        this.marketingEmail = marketingEmail;
    }

    public static UserProfileDetail create(Long profileId, String marketingEmail) {
        return UserProfileDetail.builder().profileId(profileId).marketingEmail(marketingEmail).build();
    }

    // VALIDATION
    private static void ensureInvariants(Long profileId) {
    }
}
