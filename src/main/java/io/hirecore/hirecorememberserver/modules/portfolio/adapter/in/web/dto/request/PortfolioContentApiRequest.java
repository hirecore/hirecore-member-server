package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record PortfolioContentApiRequest(
        @NotNull(message = "본문 JSON은 필수입니다.")
        Map<String, Object> json,

        @NotBlank(message = "본문 HTML은 필수입니다.")
        String html
) {
}
