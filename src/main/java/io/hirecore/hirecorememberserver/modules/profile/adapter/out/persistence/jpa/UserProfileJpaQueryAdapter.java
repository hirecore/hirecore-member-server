package io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.profile.adapter.out.persistence.jpa.repository.ProfileJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.profile.application.port.out.LoadUserProfileSummaryPort;
import io.hirecore.hirecorememberserver.modules.profile.application.port.out.dto.result.UserProfileSummaryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * {@link LoadUserProfileSummaryPort}의 JPA 구현체.
 *
 * <p>profile 모듈이 자체 소유하는 {@code profiles} 테이블에서
 * 프로필 요약 정보를 직접 조회합니다.</p>
 */
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
