package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.response;

import lombok.Builder;

@Builder
public record PortfolioContentResponse(
        String json,
        String html
) {
}
