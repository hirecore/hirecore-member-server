package io.hirecore.hirecorememberserver.modules.profile.adapter.in.web;

import io.hirecore.hirecorememberserver.common.adapter.in.security.principal.AuthPrincipal;
import io.hirecore.hirecorememberserver.modules.profile.adapter.in.web.dto.response.UserProfileSummaryApiResponse;
import io.hirecore.hirecorememberserver.modules.profile.adapter.in.web.mapper.UserProfileQueryWebMapper;
import io.hirecore.hirecorememberserver.modules.profile.application.port.in.GetUserProfileSummaryUseCase;
import io.hirecore.hirecorememberserver.modules.profile.application.port.in.dto.response.UserProfileSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증된 사용자의 프로필 관련 조회 엔드포인트를 담당하는 컨트롤러.
 *
 * <p>프론트엔드에서 페이지 진입 시 로그인 상태 및 사용자 기본 정보를
 * 확인하는 데 사용됩니다.</p>
 */
@RestController
@RequestMapping("/api/user/profile")
@RequiredArgsConstructor
public class UserProfileQueryController {

    private final GetUserProfileSummaryUseCase getUserProfileSummaryUseCase;
    private final UserProfileQueryWebMapper userProfileQueryWebMapper;

    @GetMapping("/summary")
    public ResponseEntity<UserProfileSummaryApiResponse> getUserProfileSummary(
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        UserProfileSummaryResponse response = getUserProfileSummaryUseCase.execute(
                principal.id(), principal.email()
        );
        return ResponseEntity.ok(userProfileQueryWebMapper.toApiResponse(response));
    }
}
