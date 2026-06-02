package io.hirecore.hirecorememberserver.sharedkernel.application.port.out;

import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.dto.response.PortfolioJobCategoryHierarchyResult;

import java.util.List;

public interface LoadJobCategoryPort {
    Long findIdByCode(String categoryCode);
    List<PortfolioJobCategoryHierarchyResult> loadJobCategoryHierarchy(Long leafJobCategoryId);
}
