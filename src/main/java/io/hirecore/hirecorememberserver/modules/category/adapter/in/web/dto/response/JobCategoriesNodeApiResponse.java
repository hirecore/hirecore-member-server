package io.hirecore.hirecorememberserver.modules.category.adapter.in.web.dto.response;

import java.util.List;

public record JobCategoriesNodeApiResponse(
        List<JobCategoryNodeApiResponse> categories
){
}
