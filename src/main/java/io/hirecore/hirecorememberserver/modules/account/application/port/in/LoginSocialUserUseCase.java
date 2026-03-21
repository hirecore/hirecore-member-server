package io.hirecore.hirecorememberserver.modules.account.application.port.in;

import io.hirecore.hirecorememberserver.modules.account.application.port.in.dto.request.SocialLoginCommand;
import io.hirecore.hirecorememberserver.modules.account.application.port.in.dto.response.PairTokenResponse;

public interface LoginSocialUserUseCase {
    PairTokenResponse execute(SocialLoginCommand socialLoginCommand);
}
