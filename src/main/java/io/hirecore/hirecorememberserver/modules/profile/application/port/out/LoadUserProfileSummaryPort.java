package io.hirecore.hirecorememberserver.modules.profile.application.port.out;

import java.util.Optional;

public interface LoadUserProfileSummaryPort {

    Optional<Result> findUserProfileSummary(Long memberAccountId);

    // 프로필 요약 조회 결과
    record Result(
            String publicCode,
            String nickname,
            String storageProfileImagePath
    ) {}
}
