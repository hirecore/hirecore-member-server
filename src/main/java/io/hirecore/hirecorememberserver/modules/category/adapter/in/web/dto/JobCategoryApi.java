package io.hirecore.hirecorememberserver.modules.category.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.hirecore.hirecorememberserver.common.web.json.TsidId;

// 직무 카테고리 단건 항목 contract
public class JobCategoryApi {

    private JobCategoryApi() {}

    public record Response(
            @TsidId Long id,
            Integer depth,
            Integer sortOrder,
            @TsidId Long parentId,
            String categoryName,
            String categoryCode,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            Boolean allowsCustomInput
    ) {}
}
