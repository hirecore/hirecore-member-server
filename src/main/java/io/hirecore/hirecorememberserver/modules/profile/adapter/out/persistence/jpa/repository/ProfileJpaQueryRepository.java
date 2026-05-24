package io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.repository;

import io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.entity.ProfileJpaEntity;
import io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.repository.projection.UserProfileSummaryProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProfileJpaQueryRepository extends Repository<ProfileJpaEntity, Long> {


    // find

    Optional<ProfileJpaEntity> findByMemberAccountId(Long memberAccountId);

    @Query("""
        SELECT p.nickname
        FROM ProfileJpaEntity p
        WHERE p.memberAccountId = :memberAccountId
    """)
    Optional<String> findNickname(@Param("memberAccountId") Long memberAccountId);

    @Query("""
        SELECT p.publicCodeInfo.publicCode AS publicCode,
               p.nickname AS nickname,
               p.profileImageInfo.storageProfileImagePath AS storageProfileImagePath
        FROM ProfileJpaEntity p
        WHERE p.memberAccountId = :memberAccountId
    """)
    Optional<UserProfileSummaryProjection> findUserProfileSummary(@Param("memberAccountId") Long memberAccountId);

    // exists
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
}
