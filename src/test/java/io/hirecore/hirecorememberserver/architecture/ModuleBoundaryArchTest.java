package io.hirecore.hirecorememberserver.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "io.hirecore.hirecorememberserver",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class ModuleBoundaryArchTest {
    @ArchTest
    static final ArchRule rootCommonShouldNotDependOnModules =
            noClasses()
                    .that().resideInAPackage("io.hirecore.hirecorememberserver.common..")
                    .should().dependOnClassesThat().resideInAPackage("io.hirecore.hirecorememberserver.modules..")
                    .allowEmptyShould(false)
                    .because("Common 패키지는 전역적인 공통 로직이므로 특정 비즈니스 모듈에 의존해서는 안 됩니다.");
}
