package io.hirecore.hirecorememberserver.modules.category.application.port.in;

import java.util.List;

public interface LoadJobCategoryTreeUseCase {
    List<Response> execute(Integer maxDepth);

    record Response(
            Long id,
            Integer depth,
            Integer sortOrder,
            Long parentId,
            String categoryName,
            String categoryCode,
            Boolean allowsCustomInput
    ) {
    }
}
