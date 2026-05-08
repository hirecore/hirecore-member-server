package io.hirecore.hirecorememberserver.sharedkernel.application.port.out;

public interface LoadJobCategoryIdByCodePort {
    Long findIdByCode(String categoryCode);
}
