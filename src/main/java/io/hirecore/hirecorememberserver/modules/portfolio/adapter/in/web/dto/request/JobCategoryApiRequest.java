package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record JobCategoryApiRequest(
        @NotBlank(message = "직무 카테고리 코드는 필수입니다.")
        String code,

        String customJobCategoryName
) {
}
