package io.hirecore.hirecorememberserver.architecture;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;

/**
 * JPA 엔티티/관계 설정 컨벤션을 강제하는 ArchUnit 규칙.
 *
 * <p>#107 머지로 정해진 컨벤션:</p>
 * <ul>
 *     <li>JPA 엔티티는 {@code adapter/out/persistence/jpa/entity} 패키지에만 위치</li>
 *     <li>{@code @OneToMany} 는 반드시 {@code mappedBy} 를 명시하여 양방향 관계의 자식 측에서 FK 를 소유하도록 함
 *         (단방향 {@code @OneToMany} 는 별도 조인 테이블을 만들거나 자식 측에 FK 가 모호해지므로 금지)</li>
 * </ul>
 */
@AnalyzeClasses(
        packages = "io.hirecore.hirecorememberserver",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class JpaConventionArchTest {

    @ArchTest
    static final ArchRule jpaEntitiesShouldResideInEntityPackage =
            classes()
                    .that().areAnnotatedWith(Entity.class)
                    .should().resideInAPackage("..adapter.out.persistence.jpa.entity..")
                    .because("JPA 엔티티는 헥사고날 어댑터의 영속 계층에만 존재해야 합니다.");

    @ArchTest
    static final ArchRule oneToManyMustDeclareMappedBy =
            fields()
                    .that().areAnnotatedWith(OneToMany.class)
                    .and(areInJpaEntity())
                    .should(declareMappedByOnOneToMany())
                    .because(
                            "단방향 @OneToMany 는 자식 측에 FK 가 모호해지거나 별도 조인 테이블이 생성되어 컨벤션에 어긋납니다. "
                            + "양방향 1:N 으로 표현하되 FK 는 항상 자식 측의 @ManyToOne 이 소유해야 하며, "
                            + "부모 측 @OneToMany 는 mappedBy 로 자식 측 필드명을 명시해야 합니다."
                    );

    private static DescribedPredicate<JavaField> areInJpaEntity() {
        return new DescribedPredicate<>("are declared in a JPA @Entity class") {
            @Override
            public boolean test(JavaField field) {
                JavaClass owner = field.getOwner();
                return owner.isAnnotatedWith(Entity.class);
            }
        };
    }

    private static ArchCondition<JavaField> declareMappedByOnOneToMany() {
        return new ArchCondition<>("declare non-empty mappedBy on @OneToMany") {
            @Override
            public void check(JavaField field, ConditionEvents events) {
                OneToMany annotation = field.tryGetAnnotationOfType(OneToMany.class).orElse(null);
                if (annotation == null) {
                    return;
                }
                if (annotation.mappedBy().isEmpty()) {
                    events.add(SimpleConditionEvent.violated(
                            field,
                            String.format(
                                    "%s.%s 의 @OneToMany 가 mappedBy 를 선언하지 않았습니다 (단방향 @OneToMany 금지)",
                                    field.getOwner().getName(),
                                    field.getName()
                            )
                    ));
                }
            }
        };
    }
}
