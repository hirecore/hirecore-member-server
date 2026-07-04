package io.hirecore.hirecorememberserver.modules.category.adapter.in.web.dto;

import java.util.List;

// 직무 카테고리 목록 조회 응답 contract
public class JobCategoriesApi {

    private JobCategoriesApi() {}

    public record Response(
            List<JobCategoryApi.Response> categories
    ) {}
}
