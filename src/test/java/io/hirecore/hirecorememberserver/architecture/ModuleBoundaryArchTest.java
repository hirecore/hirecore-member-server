package io.hirecore.hirecorememberserver.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

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

    /**
     * BC 간 직접 참조 금지.
     *
     * <p>한 BC 가 다른 BC 의 클래스를 직접 import 하면 헥사고날 경계가 무너집니다.
     * cross-BC 능력이 필요하면 {@code sharedkernel/application/port/out} 의 포트 추상화와
     * 대상 BC 의 {@code adapter/in/shared} 어댑터를 통해 의존성 역전으로 표현해야 합니다.</p>
     */
    @ArchTest
    static final ArchRule modulesShouldNotDependOnEachOtherDirectly =
            slices()
                    .matching("io.hirecore.hirecorememberserver.modules.(*)..")
                    .should().notDependOnEachOther()
                    .because("BC 간 직접 참조는 헥사고날 경계 위반입니다. cross-BC 능력은 sharedkernel 포트와 대상 BC 의 in/shared 어댑터를 통해 의존성 역전으로 표현하세요.");
}
