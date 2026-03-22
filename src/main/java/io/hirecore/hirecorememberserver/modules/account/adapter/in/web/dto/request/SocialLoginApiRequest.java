package io.hirecore.hirecorememberserver.modules.account.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SocialLoginApiRequest(
        @NotBlank(message = "인가 코드는 필수 값입니다.")
        String authorizationCode
){
}
