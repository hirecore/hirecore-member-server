package io.hirecore.hirecorememberserver.modules.profile.application.usecase;

import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.exception.DataConsistencyException;
import io.hirecore.hirecorememberserver.modules.profile.application.port.in.LoadUserProfileSummaryUseCase;
import io.hirecore.hirecorememberserver.modules.profile.application.port.out.LoadUserProfileSummaryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 인증 사용자 프로필 요약 조회 (프로필 미존재 시 정합성 오류로 간주)
@Service
@RequiredArgsConstructor
public class LoadUserProfileSummaryUseCaseImpl implements LoadUserProfileSummaryUseCase {

    private final LoadUserProfileSummaryPort userProfileQueryPort;

    @Override
    @Transactional(readOnly = true)
    public LoadUserProfileSummaryUseCase.Response execute(Long memberAccountId, String email) {
        LoadUserProfileSummaryPort.Result profile = userProfileQueryPort.findUserProfileSummary(memberAccountId)
                .orElseThrow(() -> new DataConsistencyException(
                        String.format(
                                "데이터 정합성 오류: MemberAccount(ID: %d)에 대한 프로필이 존재하지 않습니다.",
                                memberAccountId
                        )
                ));

        return new LoadUserProfileSummaryUseCase.Response(
                String.valueOf(memberAccountId),
                email,
                profile.publicCode(),
                profile.nickname(),
                profile.storageProfileImagePath()
        );
    }
}
