package io.hirecore.hirecorememberserver.common.adapter.out.persistence.jpa;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Persistable;

/**
 * <h3>수동 ID 할당 엔티티를 위한 영속성 최적화 기본 클래스</h3>
 * <p>
 * 식별자를 수동으로 할당하는 Aggregate Root 엔티티에서 발생하는
 * 불필요한 JPA merge(SELECT) 쿼리를 방지하고 persist(INSERT)를 강제합니다.
 * 도메인 이벤트 발행 기능을 포함합니다.
 * </p>
 */
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class AbstractPersistableAggregateRoot<ID>
        extends AbstractJpaEntityEventPublisher
        implements Persistable<ID> {

    @Builder.Default
    @Transient
    private boolean isNew = true;

    /**
     * <h3>엔티티의 신규 생성 여부를 판별하여 JPA의 영속화 동작을 최적화합니다.</h3>
     * <p>
     *  Spring Data JPA의 {@code save()} 메서드는 엔티티의 상태에 따라 다르게 동작합니다.<br>
     *  식별자({@code @Id})가 존재하지 않으면 {@code EntityManager.persist()}(INSERT)를 호출하지만,
     *  식별자가 이미 존재하면 기존 데이터로 간주하여 {@code EntityManager.merge()}(SELECT 후 UPDATE/INSERT)를 호출합니다.
     * </p>
     *
     * <ul>[문제 상황 및 도입 의도]
     *  <li>
     *      현재 도메인 모델은 애플리케이션에서 식별자(ID)를 수동으로 할당하는 구조를 가집니다.
     *      따라서 영속화되기 전의 순수한 새 객체임에도 {@code @Id} 필드에 값이 존재하게 되며,
     *      이는 JPA가 불필요한 <b>SELECT 쿼리(merge)</b>를 먼저 실행하게 만드는 성능 저하의 원인이 됩니다.
     *      (불필요한 SELECT 쿼리 후, INSERT 작업 수행)
     *  </li>
     * </ul>
     *
     * <ul>[개선점]
     *  <li>
     *      본 메서드를 오버라이딩하여 생성 시점의 {@code true} 상태를 반환하게 함으로써,
     *      무거운 {@code merge()} 연산 대신 <b>{@code persist()} 연산이 즉시 수행되도록 강제</b>합니다.
     *      이를 통해 불필요한 데이터베이스 조회 비용을 제거하고 <b>INSERT 성능을 극대화</b>할 수 있습니다.
     *  </li>
     * </ul>
     *
     * @return 엔티티가 데이터베이스에 한 번도 저장되지 않은 순수한 새 객체인 경우 {@code true}
     * @see org.springframework.data.domain.Persistable#isNew()
     */
    @Override
    public boolean isNew() {
        return this.isNew;
    }

    /**
     * DB에 저장이 완료되거나(PostPersist), DB에서 조회가 완료되면(PostLoad)
     * 더 이상 새로운 객체가 아니므로 상태를 false로 전환합니다.
     */
    @PostPersist
    @PostLoad
    protected void markNotNew() {
        this.isNew = false;
    }
}
