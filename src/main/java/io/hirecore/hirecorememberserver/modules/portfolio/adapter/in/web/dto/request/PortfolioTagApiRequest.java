package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record PortfolioTagApiRequest(
        @NotBlank(message = "태그 입력값은 필수입니다.")
        String userInputTag,

        @NotNull(message = "태그 정렬 순서는 필수입니다.")
        @PositiveOrZero(message = "태그 정렬 순서는 0 이상이어야 합니다.")
        Integer sortOrder
) {
}
