package io.hirecore.hirecorememberserver.modules.profile.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public final class UserProfileDetail implements ProfileDetail {
    private final Long profileId;
    private final String marketingEmail;

    /**
     * [복원용 빌더]
     * 데이터베이스 등 외부 인프라에서 조회된 데이터를 도메인 객체로 복원할 때만 사용해야 합니다.
     * Application 계층에서의 임의 호출은 ArchUnit 테스트에 의해 차단됩니다.
     */
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
