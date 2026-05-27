package io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request;

public record PortfolioExternalLinkCommand(
        String label,
        String url
) {
}
