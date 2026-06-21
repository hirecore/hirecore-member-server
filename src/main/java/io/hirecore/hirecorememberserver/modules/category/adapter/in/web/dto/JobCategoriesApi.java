package io.hirecore.hirecorememberserver.modules.category.adapter.in.web.dto;

import java.util.List;

/**
 * GET /api/job-categories — 직무 카테고리 목록 조회 endpoint 의 web 측 contract.
 */
public class JobCategoriesApi {

    private JobCategoriesApi() {}

    public record Response(
            List<JobCategoryApi.Response> categories
    ) {}
}
