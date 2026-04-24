package io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.sharedkernel.application.port.TokenVersionValidationPort;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.repository.MemberAccountJpaQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenVersionJpaQueryAdapter implements TokenVersionValidationPort {

    private final MemberAccountJpaQueryRepository memberAccountJpaQueryRepository;

    @Override
    public boolean isValidTokenVersion(Long memberId, int tokenVersion) {
        return memberAccountJpaQueryRepository.findTokenVersionById(memberId)
                .map(currentVersion -> currentVersion == tokenVersion)
                .orElse(false);
    }
}
