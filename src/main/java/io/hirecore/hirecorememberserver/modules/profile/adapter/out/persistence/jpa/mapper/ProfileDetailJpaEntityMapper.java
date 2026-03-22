package io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.entity.ProfileDetailJpaEntity;
import io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.entity.UserProfileDetailJpaEntity;
import io.hirecore.hirecorememberserver.modules.profile.domain.ProfileDetail;
import io.hirecore.hirecorememberserver.modules.profile.domain.UserProfileDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * {@link ProfileDetail} 다형성 타입과 {@link ProfileDetailJpaEntity} 다형성 타입 간의 변환을 담당합니다.
 *
 * <p>sealed interface 기반의 도메인 다형성과 JPA 상속 기반의 엔티티 다형성을
 * {@code instanceof} 패턴 매칭으로 연결합니다.
 * 새로운 프로필 타입(AdminProfileDetail 등) 추가 시 이 클래스에만 분기를 추가하면 됩니다.</p>
 */
@Component
@RequiredArgsConstructor
public class ProfileDetailJpaEntityMapper {

    private final UserProfileDetailJpaEntityMapper userProfileDetailJpaEntityMapper;

    public ProfileDetailJpaEntity toJpaEntity(ProfileDetail detail) {
        if (detail == null) {
            return null;
        }

        if (detail instanceof UserProfileDetail userProfileDetail) {
            return userProfileDetailJpaEntityMapper.toJpaEntity(userProfileDetail);
        }

        throw new IllegalArgumentException(
                "Unsupported ProfileDetail type: " + detail.getClass().getName()
        );
    }

    public ProfileDetail toDomain(ProfileDetailJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        if (jpaEntity instanceof UserProfileDetailJpaEntity userProfileDetailJpaEntity) {
            return userProfileDetailJpaEntityMapper.toDomain(userProfileDetailJpaEntity);
        }

        throw new IllegalArgumentException(
                "Unsupported ProfileDetailJpaEntity type: " + jpaEntity.getClass().getName()
        );
    }
}
