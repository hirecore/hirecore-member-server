package io.hirecore.hirecorememberserver.modules.account.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * POST /api/auth/login/user/{provider} — 소셜 로그인 endpoint 의 web 측 contract.
 */
public class SocialLoginApi {

    private SocialLoginApi() {}

    public record Request(
            @NotBlank(message = "인가 코드는 필수 값입니다.")
            String authorizationCode
    ) {}
}
