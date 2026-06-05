package io.hirecore.hirecorememberserver.sharedkernel.adapter.out.event;

import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.PublishDomainEventsPort;
import io.hirecore.hirecorememberserver.sharedkernel.domain.AbstractDomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * {@link PublishDomainEventsPort} 의 Spring {@link ApplicationEventPublisher} 기반 구현.
 *
 * <p>도메인 객체에 누적된 이벤트를 모두 꺼내 Spring 의 이벤트 발행 메커니즘으로 위임한다.
 * 응용 계층은 본 어댑터를 통해 발행만 호출할 뿐 Spring 인프라에 직접 의존하지 않는다.</p>
 */
@Component
@RequiredArgsConstructor
public class SpringDomainEventPublisherAdapter implements PublishDomainEventsPort {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publishAll(AbstractDomainEventPublisher source) {
        source.pollAllEvents().forEach(applicationEventPublisher::publishEvent);
    }
}
