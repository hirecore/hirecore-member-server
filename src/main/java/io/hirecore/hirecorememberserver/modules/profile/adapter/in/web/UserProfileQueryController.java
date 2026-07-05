package io.hirecore.hirecorememberserver.modules.profile.adapter.in.web;

import io.hirecore.hirecorememberserver.sharedkernel.application.security.AuthPrincipal;
import io.hirecore.hirecorememberserver.modules.profile.adapter.in.web.dto.UserProfileSummaryApi;
import io.hirecore.hirecorememberserver.modules.profile.adapter.in.web.mapper.UserProfileWebMapper;
import io.hirecore.hirecorememberserver.modules.profile.application.port.in.LoadUserProfileSummaryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 인증 사용자 프로필 조회 컨트롤러
@RestController
@RequestMapping("/api/users/profile")
@RequiredArgsConstructor
public class UserProfileQueryController {

    private final LoadUserProfileSummaryUseCase loadUserProfileSummaryUseCase;
    private final UserProfileWebMapper userProfileWebMapper;

    @GetMapping("/summary")
    public ResponseEntity<UserProfileSummaryApi.Response> getUserProfileSummary(
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        LoadUserProfileSummaryUseCase.Response response = loadUserProfileSummaryUseCase.execute(
                principal.id(), principal.email()
        );
        return ResponseEntity.ok(userProfileWebMapper.toApiResponse(response));
    }
}
