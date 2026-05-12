package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request;

public record PortfolioTagCommand(
        String userInputTag,
        Integer sortOrder
) {
}
