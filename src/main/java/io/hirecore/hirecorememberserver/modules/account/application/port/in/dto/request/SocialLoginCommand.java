package io.hirecore.hirecorememberserver.modules.account.application.port.in.dto.request;

import io.hirecore.hirecorememberserver.modules.account.application.exception.SocialAccountApplicationException;
import io.hirecore.hirecorememberserver.modules.account.application.exception.SocialAccountApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;

public record SocialLoginCommand(
        String provider,
        String authorizationCode
) {
    public SocialLoginCommand {
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
