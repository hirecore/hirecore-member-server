package io.hirecore.hirecorememberserver.modules.category.application.port.in.dto.response;

public record JobCategoryNodeResponse(
        Long id,
        Integer depth,
        Integer sortOrder,
        Long parentId,
        String categoryName,
        String categoryCode,
        Boolean allowsCustomInput
) {
}