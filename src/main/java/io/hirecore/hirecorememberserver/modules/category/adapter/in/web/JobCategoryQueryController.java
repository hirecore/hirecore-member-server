package io.hirecore.hirecorememberserver.modules.category.adapter.in.web;

import io.hirecore.hirecorememberserver.modules.category.adapter.in.web.dto.JobCategoriesApi;
import io.hirecore.hirecorememberserver.modules.category.adapter.in.web.dto.JobCategoryApi;
import io.hirecore.hirecorememberserver.modules.category.adapter.in.web.mapper.JobCategoryWebMapper;
import io.hirecore.hirecorememberserver.modules.category.application.port.in.LoadJobCategoryTreeUseCase;
import io.hirecore.hirecorememberserver.modules.category.application.port.in.dto.response.JobCategoryResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Validated
public class JobCategoryQueryController {

    private final LoadJobCategoryTreeUseCase loadJobCategoryTreeUseCase;
    private final JobCategoryWebMapper jobCategoryWebMapper;

    @GetMapping
    public ResponseEntity<JobCategoriesApi.Response> getCategories(
            @RequestParam(name="max-depth", required=true) @Min(1) @Max(3) Integer maxDepth
    ) {
        List<JobCategoryResponse> applicationResponse = loadJobCategoryTreeUseCase.execute(maxDepth);
        List<JobCategoryApi.Response> apiResponses = jobCategoryWebMapper.toApiResponses(applicationResponse);

        return ResponseEntity.ok().body(new JobCategoriesApi.Response(apiResponses));
    }
}
