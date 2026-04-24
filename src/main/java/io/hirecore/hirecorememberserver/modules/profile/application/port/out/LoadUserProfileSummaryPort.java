package io.hirecore.hirecorememberserver.modules.profile.application.port.out;

import io.hirecore.hirecorememberserver.modules.profile.application.port.out.dto.result.UserProfileSummaryResult;

import java.util.Optional;

public interface LoadUserProfileSummaryPort {
    Optional<UserProfileSummaryResult> findUserProfileSummary(Long memberAccountId);
}
