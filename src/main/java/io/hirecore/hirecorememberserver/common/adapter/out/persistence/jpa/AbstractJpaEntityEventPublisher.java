package io.hirecore.hirecorememberserver.common.adapter.out.persistence.jpa;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Spring Data JPA의 도메인 이벤트 발행 기능을 제공하는 JPA 엔티티 기반 추상 클래스입니다.
 *
 * <p>{@link org.springframework.data.domain.DomainEvents @DomainEvents}를 통해
 * {@code Repository.save()} 호출 시 Spring이 이벤트를 자동으로 발행합니다.
 * 도메인 POJO에서 발생한 이벤트는 {@link #recordPersistenceEvent(Object)}로 전달받습니다.</p>
 */
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractJpaEntityEventPublisher {

    @Builder.Default
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
