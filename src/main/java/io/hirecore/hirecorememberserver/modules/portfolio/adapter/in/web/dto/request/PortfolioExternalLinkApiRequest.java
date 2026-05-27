package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PortfolioExternalLinkApiRequest(
        @NotBlank(message = "외부 링크 라벨은 필수입니다.")
        String label,

        @NotBlank(message = "외부 링크 URL은 필수입니다.")
        @Pattern(regexp = "^https?://.+", message = "외부 링크 URL은 http:// 또는 https:// 로 시작해야 합니다.")
        String url
) {
}
