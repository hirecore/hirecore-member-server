package io.hirecore.hirecorememberserver.modules.category.application.port.in;

public interface LoadJobCategoryIdByCodeUseCase {
    Long execute(String categoryCode);
}
