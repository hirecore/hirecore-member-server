package io.hirecore.hirecorememberserver.modules.profile.adapter.in.web.dto;

// 프로필 요약 조회 web contract (GET /api/users/profile/summary)
public class UserProfileSummaryApi {

    private UserProfileSummaryApi() {}

    // 프로필 요약 응답 DTO
    public record Response(
            String id,
            String email,
            String publicCode,
            String nickname,
            String profileImageUrl
    ) {}
}
