package io.hirecore.hirecorememberserver.modules.account.adapter.in.web;

import io.hirecore.hirecorememberserver.common.utils.AuthCookieUtils;
import io.hirecore.hirecorememberserver.modules.account.adapter.in.web.dto.request.SocialLoginApiRequest;
import io.hirecore.hirecorememberserver.modules.account.adapter.in.web.mapper.SocialLoginWebMapper;
import io.hirecore.hirecorememberserver.modules.account.application.port.in.LoginSocialUserUseCase;
import io.hirecore.hirecorememberserver.modules.account.application.port.in.LogoutUseCase;
import io.hirecore.hirecorememberserver.modules.account.application.port.in.dto.request.SocialLoginCommand;
import io.hirecore.hirecorememberserver.modules.account.application.port.in.dto.response.PairTokenResponse;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 인증 관련 엔드포인트를 담당하는 컨트롤러.
 *
 * <p>소셜 로그인과 로그아웃 기능을 제공합니다.</p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginSocialUserUseCase loginSocialUserUseCase;
    private final LogoutUseCase logoutUseCase;
    private final SocialLoginWebMapper socialLoginWebMapper;
    private final AuthCookieUtils authCookieUtils;

    @PostMapping("/login/user/{provider}")
    public ResponseEntity<Void> socialLogin(
            @PathVariable String provider,
            @Valid @RequestBody SocialLoginApiRequest request
    ) {
        SocialLoginCommand socialLoginCommand = socialLoginWebMapper.mapToSocialLoginCommand(provider, request);
        PairTokenResponse pairTokenResponse = loginSocialUserUseCase.execute(socialLoginCommand);

        ResponseCookie accessTokenCookie = authCookieUtils.createAccessTokenCookie(pairTokenResponse.accessToken());
        ResponseCookie refreshTokenCookie = authCookieUtils.createRefreshTokenCookie(pairTokenResponse.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        Cookie accessTokenCookie = WebUtils.getCookie(request, "accessToken");
        if (accessTokenCookie != null) {
            logoutUseCase.execute(accessTokenCookie.getValue());
        }

        ResponseCookie expiredAccessToken = authCookieUtils.createExpiredAccessTokenCookie();
        ResponseCookie expiredRefreshToken = authCookieUtils.createExpiredRefreshTokenCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expiredAccessToken.toString())
                .header(HttpHeaders.SET_COOKIE, expiredRefreshToken.toString())
                .build();
    }
}
