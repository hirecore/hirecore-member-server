package io.hirecore.hirecorememberserver.modules.account.application.port.in.dto.response.fixture;

import io.hirecore.hirecorememberserver.modules.account.application.port.in.dto.response.PairTokenResponse;

public class PairTokenResponseFixture {

    public static final String DEFAULT_ACCESS_TOKEN = "test-access-token";
    public static final String DEFAULT_REFRESH_TOKEN = "test-refresh-token";

    public static PairTokenResponse createValidResponse() {
        return new PairTokenResponse(DEFAULT_ACCESS_TOKEN, DEFAULT_REFRESH_TOKEN);
    }

    public static PairTokenResponse createResponse(String accessToken, String refreshToken) {
        return new PairTokenResponse(accessToken, refreshToken);
    }
}
