package io.hirecore.hirecorememberserver.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;

@AnalyzeClasses(
        packages = "io.hirecore.hirecorememberserver",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class DDDArchTest {

    @ArchTest
    static final ArchRule modulesShouldBeIndependent =
            SlicesRuleDefinition.slices()
                    .matching("io.hirecore.hirecorememberserver.modules.(*)..")
//                      .should().beFreeOfCycles()   // 순환 참조만 금지 (단방향 참조 허용)
                    .should().notDependOnEachOther() // 순환·단방향 참조 모두 금지 (완전한 모듈 격리)
                    .as("Bounded Contexts must not depend on each other")
                    .because("비즈니스 모듈 간의 강한 결합도를 낮추고 마이크로서비스로의 확장성을 보장하기 위해 슬라이스 간 직접 참조를 금지합니다.");
}
