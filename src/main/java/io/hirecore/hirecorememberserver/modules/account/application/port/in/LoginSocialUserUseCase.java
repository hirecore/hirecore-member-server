package io.hirecore.hirecorememberserver.modules.account.application.port.in;

import io.hirecore.hirecorememberserver.modules.account.application.exception.SocialAccountApplicationException;
import io.hirecore.hirecorememberserver.modules.account.application.exception.SocialAccountApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.account.application.port.in.dto.response.PairTokenResponse;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;

public interface LoginSocialUserUseCase {
    PairTokenResponse execute(Command command);

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
}
