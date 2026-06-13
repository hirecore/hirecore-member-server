package io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * QueryDSL 의 {@link JPAQueryFactory} 를 컨테이너에 노출하는 sharedkernel 레벨 설정.
 * 모든 BC 의 어댑터가 동일한 팩토리 인스턴스를 주입받아 사용한다.
 */
@Configuration
public class QueryDslConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}
