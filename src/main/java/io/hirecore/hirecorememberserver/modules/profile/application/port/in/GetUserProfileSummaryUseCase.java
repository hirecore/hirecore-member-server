package io.hirecore.hirecorememberserver.modules.profile.application.port.in;


import io.hirecore.hirecorememberserver.modules.profile.application.port.in.dto.response.UserProfileSummaryResponse;

/**
 * 현재 인증된 사용자의 프로필 요약 정보를 조회하는 유스케이스.
 *
 * <p>JWT에서 추출한 {@code memberAccountId}와 {@code email}을 받아,
 * 프로필 테이블에서 닉네임·공개코드·프로필 이미지를 조합하여 반환합니다.</p>
 */
public interface GetUserProfileSummaryUseCase {

    UserProfileSummaryResponse execute(Long memberAccountId, String email);
}
