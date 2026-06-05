package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository.PortfolioJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.PortfolioTag;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@DisplayName("PortfolioJpaCommandAdapter 단위 테스트")
@ExtendWith(MockitoExtension.class)
class PortfolioJpaCommandAdapterTest {

    @InjectMocks
    private PortfolioJpaCommandAdapter sut;

    @Mock
    private PortfolioJpaEntityMapper portfolioMapper;

    @Mock
    private PortfolioJpaCommandRepository portfolioRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private static Portfolio buildPortfolio() {
        return Portfolio.create(
                1L, 100L, 8001L, 7001L,
                "title", "본문", null,
                9L, null,
                "{}", "<p>본문</p>",
                List.of(),
                List.of(),
                List.of(PortfolioTag.create("Spring", 0), PortfolioTag.create("DDD", 1)),
                CollaborationType.TEAM, Visibility.PUBLIC
        );
    }

    @Test
    @DisplayName("save 는 매퍼로 그래프 변환을 위임하고 단일 repository.save 로 영속화한다")
    void should_delegate_to_mapper_and_save_once() {
        // given
        Portfolio domain = buildPortfolio();
        PortfolioJpaEntity portfolioEntity = mock(PortfolioJpaEntity.class);
        given(portfolioMapper.toJpaEntity(domain)).willReturn(portfolioEntity);

        // when
        Portfolio result = sut.save(domain);

        // then
        assertThat(result).isSameAs(domain);
        then(portfolioMapper).should().toJpaEntity(domain);
        then(portfolioRepository).should().save(portfolioEntity);
    }

    @Test
    @DisplayName("update 는 매퍼로 그래프 변환 후 markPersisted 를 표기하고 merge 경로로 진입시킨다")
    void should_mark_persisted_and_save_on_update() {
        // given
        Portfolio domain = buildPortfolio();
        PortfolioJpaEntity portfolioEntity = mock(PortfolioJpaEntity.class);
        given(portfolioMapper.toJpaEntity(domain)).willReturn(portfolioEntity);

        // when
        sut.update(domain);

        // then
        then(portfolioMapper).should().toJpaEntity(domain);
        then(portfolioEntity).should().markPersisted();
        then(portfolioRepository).should().save(portfolioEntity);
    }
}
