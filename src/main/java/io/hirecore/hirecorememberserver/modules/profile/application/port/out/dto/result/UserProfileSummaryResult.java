package io.hirecore.hirecorememberserver.modules.profile.application.port.out.dto.result;

/**
 * 프로필 요약 조회 결과.
 *
 * @param publicCode              사용자 고유 공개 코드 (8자리)
 * @param nickname                닉네임
 * @param storageProfileImagePath 프로필 이미지 저장 경로 (없으면 null)
 */
public record UserProfileSummaryResult(
        String publicCode,
        String nickname,
        String storageProfileImagePath
) {}
