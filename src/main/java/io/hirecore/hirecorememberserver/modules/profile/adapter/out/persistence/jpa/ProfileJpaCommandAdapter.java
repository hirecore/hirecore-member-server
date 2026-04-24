package io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.entity.ProfileJpaEntity;
import io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.mapper.ProfileJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.repository.ProfileJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.profile.application.port.out.SaveProfilePort;
import io.hirecore.hirecorememberserver.modules.profile.domain.Profile;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ProfileJpaCommandAdapter implements SaveProfilePort {
    private final ProfileJpaEntityMapper profileJpaEntityMapper;
    private final ProfileJpaCommandRepository profileJpaCommandRepository;

    @Override
    public Profile save(Profile profile) {
        ProfileJpaEntity profileJpaEntity = profileJpaEntityMapper.toJpaEntity(profile);
        ProfileJpaEntity save = profileJpaCommandRepository.save(profileJpaEntity);

        return profileJpaEntityMapper.toDomain(save);
    }
}
