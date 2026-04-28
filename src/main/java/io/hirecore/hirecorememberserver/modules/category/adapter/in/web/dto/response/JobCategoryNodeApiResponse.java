package io.hirecore.hirecorememberserver.modules.category.adapter.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

public record JobCategoryNodeApiResponse(
        Long id,
        Integer depth,
        Integer sortOrder,
        Long parentId,
        String categoryName,
        String categoryCode,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        Boolean allowsCustomInput
){
}
