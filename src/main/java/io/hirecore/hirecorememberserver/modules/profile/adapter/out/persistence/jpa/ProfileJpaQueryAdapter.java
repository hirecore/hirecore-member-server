package io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.repository.ProfileJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.profile.application.port.out.CheckProfilePublicCodePort;
import io.hirecore.hirecorememberserver.modules.profile.application.port.out.LoadProfileNicknamePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

// ProfileJpaQueryRepository JPA 구현
@Component
@RequiredArgsConstructor
public class ProfileJpaQueryAdapter
        implements CheckProfilePublicCodePort,
        LoadProfileNicknamePort
{

    private final ProfileJpaQueryRepository profileJpaQueryRepository;

    @Override
    public boolean existsByPublicCode(String code) {
        return profileJpaQueryRepository.existsByPublicCode(code);
    }

    @Override
    public Optional<String> findNickname(Long accountId) {
        return profileJpaQueryRepository.findNickname(accountId);
    }
}
