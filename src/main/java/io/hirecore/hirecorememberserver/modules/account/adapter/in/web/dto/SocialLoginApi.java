package io.hirecore.hirecorememberserver.modules.account.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

// 소셜 로그인 web contract (POST /api/auth/login/user/{provider})
public class SocialLoginApi {

    private SocialLoginApi() {}

    public record Request(
            @NotBlank(message = "인가 코드는 필수 값입니다.")
            String authorizationCode
    ) {}
}
