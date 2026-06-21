package io.hirecore.hirecorememberserver.modules.profile.adapter.in.web.dto;

/**
 * GET /api/users/profile/summary — 프로필 요약 조회 endpoint 의 web 측 contract.
 */
public class UserProfileSummaryApi {

    private UserProfileSummaryApi() {}

    /**
     * 프로필 요약 조회 API 응답 DTO.
     *
     * @param id              회원 계정 고유 식별자 (Long → String 변환)
     * @param email           회원 이메일
     * @param publicCode      프로필 공개 코드 (8자리 고유 문자열)
     * @param nickname        회원 닉네임
     * @param profileImageUrl 프로필 이미지 저장 경로 (없으면 null)
     */
    public record Response(
            String id,
            String email,
            String publicCode,
            String nickname,
            String profileImageUrl
    ) {}
}
