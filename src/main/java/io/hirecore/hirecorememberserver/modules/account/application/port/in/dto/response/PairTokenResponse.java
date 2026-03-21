package io.hirecore.hirecorememberserver.modules.account.application.port.in.dto.response;

public record PairTokenResponse(
        String accessToken,
        String refreshToken
) {
}
