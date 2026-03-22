package io.hirecore.hirecorememberserver.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(
        packages = "io.hirecore.hirecorememberserver",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class HexagonalArchitectureArchTest {

    /**
     * 의도: 의존성 역전 원칙(DIP)을 강제하여 외부(Adapter)가 내부(Domain)를 향하도록 합니다.
     */
    @ArchTest
    static final ArchRule hexagonalArchitectureDependencies =
            layeredArchitecture()
                    .consideringAllDependencies()
                    // 1. 계층 정의
                    .layer("Adapter").definedBy("io.hirecore.hirecorememberserver.modules.*.adapter..")
                    .layer("Application").definedBy("io.hirecore.hirecorememberserver.modules.*.application..")
                    .layer("Domain").definedBy("io.hirecore.hirecorememberserver.modules.*.domain..")

                    // 2. 의존성 규칙 정의 (접근 허용 주체만 명시)
                    // [Domain]은 가장 안쪽에 있으므로 Application과 Adapter만 접근 가능합니다.
                    // (common 패키지 등 외부 계층이 Domain을 직접 호출하는 것을 막습니다)
                    .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Adapter")

                    // [Application]은 오직 Adapter에서만 진입할 수 있습니다.
                    // (이 규칙 덕분에 Domain 계층이 Application을 역방향으로 참조하는 것이 자동 차단됩니다)
                    .whereLayer("Application").mayOnlyBeAccessedByLayers("Adapter")

                    // [Adapter]는 최외곽이므로 그 어떤 내부 계층도 Adapter에 접근할 수 없습니다.
                    // (이 규칙 덕분에 Application이나 Domain이 Adapter를 참조하는 DIP 위반이 완벽히 차단됩니다)
                    .whereLayer("Adapter").mayNotBeAccessedByAnyLayer()

                    .because("헥사고날 아키텍처의 의존성 역전 원칙(DIP)에 따라 내부 계층은 외부 계층을 알 수 없어야 합니다.");
}
