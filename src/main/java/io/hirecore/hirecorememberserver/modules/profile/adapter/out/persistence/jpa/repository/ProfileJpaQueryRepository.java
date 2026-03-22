package io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.entity.ProfileJpaEntity;
import io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.repository.projection.UserProfileSummaryProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProfileJpaQueryRepository extends Repository<ProfileJpaEntity, Long> {

    /**
     * 공개 코드의 존재 여부를 확인합니다.
     *
     * <p>{@code publicCode}는 {@code profiles} 기본 테이블의 컬럼이므로
     * JOINED 상속 전략에서 서브타입 JOIN 없이 기본 테이블만 조회됩니다.</p>
     */
    @Query(
        value = """
        SELECT IF( EXISTS (
                SELECT 1
                FROM profiles p
                WHERE p.public_code = :publicCode
            ), "true", "false"
        )
        """, nativeQuery = true
    )
    boolean existsByPublicCode(@Param("publicCode") String publicCode);

    Optional<ProfileJpaEntity> findByMemberAccountId(Long memberAccountId);

    @Query("""
        SELECT p.publicCodeInfo.publicCode AS publicCode,
               p.nickname AS nickname,
               p.profileImageInfo.storageProfileImagePath AS storageProfileImagePath
        FROM ProfileJpaEntity p
        WHERE p.memberAccountId = :memberAccountId
    """)
    Optional<UserProfileSummaryProjection> findUserProfileSummary(@Param("memberAccountId") Long memberAccountId);
}
