package io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.repository.ProfileJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.profile.application.port.out.LoadUserProfileSummaryPort;
import io.hirecore.hirecorememberserver.modules.profile.application.port.out.dto.result.UserProfileSummaryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

// LoadUserProfileSummaryPort JPA 구현 (profiles 테이블 조회)
@Component
@RequiredArgsConstructor
public class UserProfileJpaQueryAdapter implements LoadUserProfileSummaryPort {

    private final ProfileJpaQueryRepository profileJpaQueryRepository;

    @Override
    public Optional<UserProfileSummaryResult> findUserProfileSummary(Long memberAccountId) {
        return profileJpaQueryRepository.findUserProfileSummary(memberAccountId)
                .map(p -> new UserProfileSummaryResult(
                        p.getPublicCode(),
                        p.getNickname(),
                        p.getStorageProfileImagePath()
                ));
    }
}
