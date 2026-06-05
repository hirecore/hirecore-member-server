package io.hirecore.hirecorememberserver.sharedkernel.application.port.out;

import io.hirecore.hirecorememberserver.sharedkernel.domain.AbstractDomainEventPublisher;

/**
 * 도메인 이벤트 발행을 응용 계층에서 인프라(Spring 등) 직접 의존 없이 호출하기 위한 out port.
 *
 * <p>호출 시점에 {@link AbstractDomainEventPublisher#pollAllEvents()} 의 결과를 모두 발행한다.
 * 영구화 트리거(`repository.save()`)가 없는 경로(예: readOnly 트랜잭션 안의 도메인 사건)에서
 * 명시적 발행 호출에 사용한다.</p>
 */
public interface PublishDomainEventsPort {

    void publishAll(AbstractDomainEventPublisher source);
}
