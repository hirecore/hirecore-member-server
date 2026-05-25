package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JobCategoryApiRequest(
        @NotBlank(message = "직무 카테고리 코드는 필수입니다.")
        String code,

        @Size(max = 10, message = "사용자 입력 라벨은 10자를 초과할 수 없습니다.")
        String userInput
) {
}
