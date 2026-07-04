package io.hirecore.hirecorememberserver.modules.profile.application.port.in;


import io.hirecore.hirecorememberserver.modules.profile.application.port.in.dto.response.UserProfileSummaryResponse;

// 인증 사용자 프로필 요약 조회
public interface LoadUserProfileSummaryUseCase {

    UserProfileSummaryResponse execute(Long memberAccountId, String email);
}
