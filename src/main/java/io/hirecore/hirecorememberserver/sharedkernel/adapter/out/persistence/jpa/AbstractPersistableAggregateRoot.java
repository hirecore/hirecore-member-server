package io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * <h3>Aggregate Root 엔티티를 위한 영속성 기본 클래스</h3>
 * <p>
 * {@link AbstractPersistableEntity}의 isNew() 최적화를 상속받으며,
 * Spring Data JPA의 도메인 이벤트 발행 기능을 추가로 제공합니다.
 * </p>
 *
 * <p>{@link DomainEvents @DomainEvents}를 통해
 * {@code Repository.save()} 호출 시 Spring이 이벤트를 자동으로 발행합니다.
 * 도메인 POJO에서 발생한 이벤트는 {@link #recordPersistenceEvent(Object)}로 전달받습니다.</p>
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class AbstractPersistableAggregateRoot<ID>
        extends AbstractPersistableEntity<ID> {

    @Transient
    private transient List<Object> domainEvents = new ArrayList<>();

    /**
     * POJO 도메인 객체에서 발생한 이벤트를 영속성 엔티티로 전이시킬 때 사용합니다.
     */
    public void recordPersistenceEvent(Object event) {
        if (event != null) {
            this.domainEvents.add(event);
        }
    }

    /**
     * Repository.save() 호출 시 Spring Data가 이 메서드를 찾아 이벤트를 발행합니다.
     */
    @DomainEvents
    protected Collection<Object> domainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    /**
     * 이벤트 발행 완료 후 리스트를 비워 중복 발행을 방지합니다.
     */
    @AfterDomainEventPublication
    protected void clearDomainEvents() {
        this.domainEvents.clear();
    }
}
