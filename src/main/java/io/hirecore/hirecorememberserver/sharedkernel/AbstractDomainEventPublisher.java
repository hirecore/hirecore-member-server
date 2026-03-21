package io.hirecore.hirecorememberserver.sharedkernel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 도메인 이벤트를 수집하고 외부로 방출하기 위한 Aggregate Root 기반 추상 클래스입니다.
 *
 * <p>Spring 등 특정 프레임워크에 의존하지 않는 순수 POJO로 설계되었습니다.
 * 도메인 로직 수행 중 발생한 이벤트는 내부에 누적되며, Persistence Adapter가
 * {@link #pollAllEvents()}를 호출해 이벤트를 꺼내 발행하는 방식으로 동작합니다.</p>
 */
public abstract class AbstractDomainEventPublisher {

    private final transient List<Object> domainEvents = new ArrayList<>();

    /**
     * 도메인 이벤트를 내부에 등록합니다.
     * 하위 Aggregate에서만 호출할 수 있도록 {@code protected}로 제한합니다.
     *
     * @param event 등록할 도메인 이벤트 객체
     */
    protected void registerEvent(Object event) {
        if (event != null) {
            this.domainEvents.add(event);
        }
    }

    /**
     * 내부에 등록된 모든 도메인 이벤트를 반환하고 내부 목록을 비웁니다.
     * 중복 발행을 방지하기 위해 호출 즉시 이벤트 목록이 초기화됩니다.
     *
     * @return 등록된 도메인 이벤트의 불변 컬렉션
     */
    public Collection<Object> pollAllEvents() {
        if (domainEvents.isEmpty()) {
            return Collections.emptyList();
        }

        List<Object> currentEvents = new ArrayList<>(this.domainEvents);
        this.domainEvents.clear();

        return Collections.unmodifiableCollection(currentEvents);
    }
}
