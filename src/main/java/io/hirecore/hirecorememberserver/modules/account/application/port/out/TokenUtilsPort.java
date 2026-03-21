package io.hirecore.hirecorememberserver.modules.account.application.port.out;

import io.hirecore.hirecorememberserver.modules.account.application.port.in.dto.response.PairTokenResponse;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.dto.request.TokenClaimsRequest;

public interface TokenUtilsPort {
    PairTokenResponse issueTokenPair(TokenClaimsRequest authPrincipal);
}
