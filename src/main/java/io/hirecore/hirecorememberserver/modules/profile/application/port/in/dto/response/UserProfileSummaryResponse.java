package io.hirecore.hirecorememberserver.modules.profile.application.port.in.dto.response;

// 인증 사용자 프로필 요약 응답 DTO
public record UserProfileSummaryResponse(
        String id,
        String email,
        String publicCode,
        String nickname,
        String profileImageUrl
) {}
