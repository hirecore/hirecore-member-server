package io.hirecore.hirecorememberserver.modules.account.application.port.in.dto.request;

import io.hirecore.hirecorememberserver.modules.account.application.exception.SocialAccountApplicationException;
import io.hirecore.hirecorememberserver.modules.account.application.exception.SocialAccountApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.common.utils.AssertionUtils;

public record SocialLoginCommand(
        String provider,
        String authorizationCode
) {
    public SocialLoginCommand {
        // 기존의 DomainException에서 ApplicationException으로 변경 적용
        AssertionUtils.notBlank(
                provider,
                SocialAccountApplicationExceptionCodeCluster.DetailResponse.PROVIDER_MISSING,
                SocialAccountApplicationException::new
        );

        AssertionUtils.notBlank(
                authorizationCode,
                SocialAccountApplicationExceptionCodeCluster.DetailResponse.AUTHORIZATION_CODE_MISSING,
                SocialAccountApplicationException::new
        );
    }
}
