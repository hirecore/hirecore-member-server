package io.hirecore.hirecorememberserver.modules.profile.application.port.in.dto.response;

/**
 * 현재 인증된 사용자의 프로필 요약 응답 DTO.
 *
 * @param id              회원 계정 고유 식별자 (Long → String 변환)
 * @param email           회원 이메일
 * @param publicCode      프로필 공개 코드 (8자리 고유 문자열)
 * @param nickname        회원 닉네임
 * @param profileImageUrl 프로필 이미지 저장 경로 (없으면 null)
 */
public record UserProfileSummaryResponse(
        String id,
        String email,
        String publicCode,
        String nickname,
        String profileImageUrl
) {}
