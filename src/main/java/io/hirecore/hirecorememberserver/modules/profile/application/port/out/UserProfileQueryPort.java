package io.hirecore.hirecorememberserver.modules.profile.application.port.out;

import io.hirecore.hirecorememberserver.modules.profile.application.port.out.dto.result.UserProfileSummaryResult;

import java.util.Optional;

/**
 * 사용자 프로필 요약 정보 조회를 위한 출력 포트.
 *
 * <p>인증된 사용자의 프로필 요약(공개 코드, 닉네임, 프로필 이미지)을
 * {@code memberAccountId} 기준으로 조회합니다.</p>
 */
public interface UserProfileQueryPort {
    Optional<UserProfileSummaryResult> findUserProfileSummary(Long memberAccountId);
}
