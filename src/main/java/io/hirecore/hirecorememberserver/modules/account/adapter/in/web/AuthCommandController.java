package io.hirecore.hirecorememberserver.modules.account.adapter.in.web;

import io.hirecore.hirecorememberserver.sharedkernel.application.security.AuthPrincipal;
import io.hirecore.hirecorememberserver.common.security.utils.AuthCookieUtils;
import io.hirecore.hirecorememberserver.modules.account.adapter.in.web.dto.SocialLoginApi;
import io.hirecore.hirecorememberserver.modules.account.adapter.in.web.mapper.SocialLoginWebMapper;
import io.hirecore.hirecorememberserver.modules.account.application.port.in.LoginSocialUserUseCase;
import io.hirecore.hirecorememberserver.modules.account.application.port.in.LogoutUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

// 인증 컨트롤러 (소셜 로그인/로그아웃)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthCommandController {

    private final LoginSocialUserUseCase loginSocialUserUseCase;
    private final LogoutUseCase logoutUseCase;
    private final SocialLoginWebMapper socialLoginWebMapper;
    private final AuthCookieUtils authCookieUtils;

    @PostMapping("/login/user/{provider}")
    public ResponseEntity<Void> socialLogin(
            @PathVariable String provider,
            @Valid @RequestBody SocialLoginApi.Request request
    ) {
        LoginSocialUserUseCase.Command command = socialLoginWebMapper.toSocialLoginCommand(provider, request);
        LoginSocialUserUseCase.Response pairTokenResponse = loginSocialUserUseCase.execute(command);

        ResponseCookie accessTokenCookie = authCookieUtils.createAccessTokenCookie(pairTokenResponse.accessToken());
        ResponseCookie refreshTokenCookie = authCookieUtils.createRefreshTokenCookie(pairTokenResponse.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal AuthPrincipal principal) {
        logoutUseCase.execute(principal.id());

        ResponseCookie expiredAccessToken = authCookieUtils.createExpiredAccessTokenCookie();
        ResponseCookie expiredRefreshToken = authCookieUtils.createExpiredRefreshTokenCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expiredAccessToken.toString())
                .header(HttpHeaders.SET_COOKIE, expiredRefreshToken.toString())
                .build();
    }
}
