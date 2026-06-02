package io.hirecore.hirecorememberserver.modules.category.adapter.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.hirecore.hirecorememberserver.common.web.json.TsidId;

public record JobCategoryApiResponse(
        @TsidId Long id,
        Integer depth,
        Integer sortOrder,
        @TsidId Long parentId,
        String categoryName,
        String categoryCode,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        Boolean allowsCustomInput
){
}
