package io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.JpaAuditingConfig;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.QueryDslConfig;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AuditingJpaInfo;
import io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.entity.ProfileJpaEntity;
import io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.entity.vo.ProfileImageJpaInfo;
import io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.entity.vo.PublicCodeJpaInfo;
import io.hirecore.hirecorememberserver.modules.profile.application.port.out.dto.result.UserProfileSummaryResult;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link UserProfileJpaQueryAdapter} 통합 테스트.
 *
 * <p>profile 모듈이 자체 소유하는 {@code profiles} 테이블에서
 * Spring Data Repository를 통해 프로필 요약 정보를 정확히 조회하는지 검증합니다.</p>
 */
@DisplayName("UserProfileJpaQueryAdapter 통합 테스트")
@DataJpaTest
@Import({JpaAuditingConfig.class, QueryDslConfig.class, UserProfileJpaQueryAdapter.class})
class UserProfileJpaQueryAdapterTest {

    private static final Long PROFILE_ID = 1L;
    private static final Long MEMBER_ACCOUNT_ID = 1001L;
    private static final String PUBLIC_CODE = "Ab3Xy9Zq";
    private static final String NICKNAME = "my-nickname";
    private static final String PROFILE_IMAGE_PATH = "/images/profile/abc123.png";

    @Autowired
    private UserProfileJpaQueryAdapter adapter;

    @Autowired
    private EntityManager entityManager;

    private void insertProfile(Long memberAccountId, String publicCode, String nickname, String imagePath) {
        Instant now = Instant.now();
        ProfileJpaEntity entity = ProfileJpaEntity.builder()
                .id(PROFILE_ID)
                .memberAccountId(memberAccountId)
                .publicCodeInfo(new PublicCodeJpaInfo(publicCode))
                .nickname(nickname)
                .profileImageInfo(new ProfileImageJpaInfo(null, null, imagePath))
                .auditingInfo(new AuditingJpaInfo(now, now))
                .build();
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
    }

    @Nested
    @DisplayName("findUserProfileSummary")
    class FindByMemberAccountIdTest {

        @Test
        @DisplayName("프로필이 존재하면 publicCode, 닉네임, 프로필 이미지 경로를 반환한다")
        void should_return_profile_summary_when_exists() {
            // given
            insertProfile(MEMBER_ACCOUNT_ID, PUBLIC_CODE, NICKNAME, PROFILE_IMAGE_PATH);

            // when
            Optional<UserProfileSummaryResult> result = adapter.findUserProfileSummary(MEMBER_ACCOUNT_ID);

            // then
            assertThat(result).hasValueSatisfying(profile -> {
                assertThat(profile.publicCode()).isEqualTo(PUBLIC_CODE);
                assertThat(profile.nickname()).isEqualTo(NICKNAME);
                assertThat(profile.storageProfileImagePath()).isEqualTo(PROFILE_IMAGE_PATH);
            });
        }

        @Test
        @DisplayName("프로필 이미지가 없는 경우 storageProfileImagePath가 null로 반환된다")
        void should_return_null_image_path_when_no_image() {
            // given
            insertProfile(MEMBER_ACCOUNT_ID, PUBLIC_CODE, NICKNAME, null);

            // when
            Optional<UserProfileSummaryResult> result = adapter.findUserProfileSummary(MEMBER_ACCOUNT_ID);

            // then
            assertThat(result).hasValueSatisfying(profile -> {
                assertThat(profile.nickname()).isEqualTo(NICKNAME);
                assertThat(profile.storageProfileImagePath()).isNull();
            });
        }

        @Test
        @DisplayName("프로필이 없는 회원 ID로 조회하면 빈 Optional을 반환한다")
        void should_return_empty_when_profile_not_found() {
            // when
            Optional<UserProfileSummaryResult> result = adapter.findUserProfileSummary(99999L);

            // then
            assertThat(result).isEmpty();
        }
    }
}
