package io.hirecore.hirecorememberserver.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring 스케줄러 활성화.
 *
 * <p>현재 단일 인스턴스 운영을 전제로 분산락(ShedLock 등) 없이 동작합니다.
 * 다중 인스턴스 도입 시점에 분산락을 함께 도입해야 동일 시각에 여러 인스턴스가
 * 같은 작업을 중복 실행하는 사고를 막을 수 있습니다.</p>
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
