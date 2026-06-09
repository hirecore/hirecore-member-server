package io.hirecore.hirecorememberserver.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.ResolveTokenPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.ValidateTokenVersionPort;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
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

    /**
     * sharedkernel out port 의 구현체는 데이터 소유 BC 의 {@code adapter/in/shared/} 안에서만 정의되어야 한다.
     *
     * <p>호출자 관점 명명(예: profile 안의 PortfolioQueryAdapter)으로 어댑터를 만들거나
     * 같은 sharedkernel 포트에 호출자별 어댑터를 추가하는 패턴이 자라나면 Spring 빈 충돌
     * ({@code NoUniqueBeanDefinitionException}) 위험을 키운다. 위치를 못 박아 회귀를 차단한다.</p>
     *
     * <p>예외:
     * <ul>
     *   <li>sharedkernel 자체 인프라 어댑터 (예: {@code SpringDomainEventPublisherAdapter}) — BC 비종속.</li>
     *   <li>{@link ResolveTokenPort}, {@link ValidateTokenVersionPort} — 보안/JWT 인프라 어댑터 위치 정리는 #327 에서 다룬다.</li>
     * </ul></p>
     */
    @ArchTest
    static final ArchRule sharedKernelOutPortImplementationsShouldResideInAdapterInShared =
            classes()
                    .that().implement(resideInAPackage("io.hirecore.hirecorememberserver.sharedkernel.application.port.out.."))
                    .and().resideOutsideOfPackage("io.hirecore.hirecorememberserver.sharedkernel..")
                    .and().areNotAssignableTo(ResolveTokenPort.class)
                    .and().areNotAssignableTo(ValidateTokenVersionPort.class)
                    .should().resideInAPackage("io.hirecore.hirecorememberserver.modules.*.adapter.in.shared..")
                    .because("sharedkernel cross-BC 포트의 구현체는 데이터 소유 BC 의 adapter/in/shared 에서만 정의되어야 합니다. 호출자별 어댑터 추가로 인한 Spring 빈 충돌을 사전 차단합니다.");
}
