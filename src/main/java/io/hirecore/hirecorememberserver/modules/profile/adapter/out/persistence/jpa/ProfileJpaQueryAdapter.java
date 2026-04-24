package io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.repository.ProfileJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.profile.application.port.out.CheckProfilePublicCodePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 프로필 조회 관련 {@link ProfileJpaQueryRepository}의 JPA 구현체.
 */
@Component
@RequiredArgsConstructor
public class ProfileJpaQueryAdapter implements CheckProfilePublicCodePort {

    private final ProfileJpaQueryRepository profileJpaQueryRepository;

    @Override
    public boolean existsByPublicCode(String code) {
        return profileJpaQueryRepository.existsByPublicCode(code);
    }
}
