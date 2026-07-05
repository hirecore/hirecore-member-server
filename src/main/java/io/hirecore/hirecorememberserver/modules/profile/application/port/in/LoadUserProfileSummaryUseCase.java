package io.hirecore.hirecorememberserver.modules.profile.application.port.in;

// 인증 사용자 프로필 요약 조회
public interface LoadUserProfileSummaryUseCase {

    Response execute(Long memberAccountId, String email);

    // 인증 사용자 프로필 요약 응답
    record Response(
            String id,
            String email,
            String publicCode,
            String nickname,
            String profileImageUrl
    ) {}
}
