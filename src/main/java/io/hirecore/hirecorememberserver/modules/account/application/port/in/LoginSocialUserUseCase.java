package io.hirecore.hirecorememberserver.modules.account.application.port.in;

import io.hirecore.hirecorememberserver.modules.account.application.exception.SocialAccountApplicationException;
import io.hirecore.hirecorememberserver.modules.account.application.exception.SocialAccountApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;

public interface LoginSocialUserUseCase {
    Response execute(Command command);

    record Command(
            String provider,
            String authorizationCode
    ) {
        public Command {
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

    // 로그인 결과 토큰 쌍
    record Response(
            String accessToken,
            String refreshToken
    ) {
    }
}
