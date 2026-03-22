package io.hirecore.hirecorememberserver.modules.profile.application.usecase;

import io.hirecore.hirecorememberserver.common.adapter.out.persistence.exception.DataConsistencyException;
import io.hirecore.hirecorememberserver.modules.profile.application.port.in.GetUserProfileSummaryUseCase;
import io.hirecore.hirecorememberserver.modules.profile.application.port.in.dto.response.UserProfileSummaryResponse;
import io.hirecore.hirecorememberserver.modules.profile.application.port.out.UserProfileQueryPort;
import io.hirecore.hirecorememberserver.modules.profile.application.port.out.dto.result.UserProfileSummaryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 현재 인증된 사용자 프로필 요약 조회 유스케이스 구현체.
 *
 * <p>JWT에서 추출한 회원 ID와 이메일을 기반으로,
 * 프로필 테이블에서 공개 코드·닉네임·프로필 이미지 경로를 조회하여
 * 사용자 프로필 요약 정보를 조합해 반환합니다.</p>
 *
 * <p>프로필 정보가 존재하지 않는 경우 {@link DataConsistencyException}을 발생시킵니다.
 * 정상적인 회원 가입 플로우에서는 {@code MemberRegisteredEvent}에 의해
 * 프로필이 반드시 생성되므로, 프로필 미존재는 데이터 정합성 오류로 간주합니다.</p>
 */
@Service
@RequiredArgsConstructor
public class GetUserProfileSummaryUseCaseImpl implements GetUserProfileSummaryUseCase {

    private final UserProfileQueryPort userProfileQueryPort;

    @Override
    public UserProfileSummaryResponse execute(Long memberAccountId, String email) {
        UserProfileSummaryResult profile = userProfileQueryPort.findUserProfileSummary(memberAccountId)
                .orElseThrow(() -> new DataConsistencyException(
                        String.format(
                                "데이터 정합성 오류: MemberAccount(ID: %d)에 대한 프로필이 존재하지 않습니다.",
                                memberAccountId
                        )
                ));

        return new UserProfileSummaryResponse(
                String.valueOf(memberAccountId),
                email,
                profile.publicCode(),
                profile.nickname(),
                profile.storageProfileImagePath()
        );
    }
}
