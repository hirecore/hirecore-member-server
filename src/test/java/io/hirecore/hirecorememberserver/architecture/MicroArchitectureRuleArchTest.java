package io.hirecore.hirecorememberserver.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.core.domain.JavaCall.Predicates.target;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.properties.HasName.Predicates.name;
import static com.tngtech.archunit.core.domain.properties.HasOwner.Predicates.With.owner;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "io.hirecore.hirecorememberserver",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class MicroArchitectureRuleArchTest {

    /**
     * <ol>
     * <li>
     *     도메인 빌더 접근제어
     *  <ul>
     *     <li>
     *         domain계층과 Driving adapter 하위 디렉토리인 mapper를 제외한 다른 패키지에서는
     *         도메인 패키지에 속한 클래스의 builder() 메서드를 호출해서는 안 되는 규칙을 가집니다.
     *     </li>
     *  </ul>
     * </li>
     *
     * <li>
     *     거짓 양성 방어: allowEmptyShould(false)
     *  <ul>
     *     <li>
     *          builder 메소드가 프로젝트에서 완전히 제거되어 테스트가 무조건 성공하는 현상을 방지합니다.
     *     </li>
     *  </ul>
     * </li>
     */
    @ArchTest
    static final ArchRule domainBuilderShouldBeEncapsulated =
            noClasses()
                    .that().resideOutsideOfPackages(
                            "io.hirecore.hirecorememberserver.modules.*.domain..",
                            "io.hirecore.hirecorememberserver.modules.*.adapter.out.persistence.*.mapper.."
                    )
                    .should().callMethodWhere(
                            target(name("builder"))
                                    .and(target(owner(resideInAPackage("io.hirecore.hirecorememberserver.modules.*.domain.."))))
                    )
                    .allowEmptyShould(false)
                    .because(
                            "도메인 객체의 캡슐화를 훼손하지 않도록, Service 등 외부 계층에서는 빌더 대신 의미가 부여된 정적 팩토리 메서드를 사용해야 합니다."
                    );
}
