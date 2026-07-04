package io.hirecore.hirecorememberserver.modules.profile.application.port.out.dto.result;

// 프로필 요약 조회 결과
public record UserProfileSummaryResult(
        String publicCode,
        String nickname,
        String storageProfileImagePath
) {}
