package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioContentJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJobCategoryJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioTagJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioContentJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioJobCategoryJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioTagJpaEntityMapper;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@DisplayName("PortfolioJpaCommandAdapter 단위 테스트")
@ExtendWith(MockitoExtension.class)
class PortfolioJpaCommandAdapterTest {

    @InjectMocks
    private PortfolioJpaCommandAdapter sut;

    @Mock
    private PortfolioJpaEntityMapper portfolioMapper;

    @Mock
    private PortfolioContentJpaEntityMapper portfolioContentMapper;

    @Mock
    private PortfolioJobCategoryJpaEntityMapper portfolioJobCategoryMapper;

    @Mock
    private PortfolioTagJpaEntityMapper portfolioTagMapper;

    @Mock
    private PortfolioJpaCommandRepository portfolioRepository;

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
    @DisplayName("Aggregate Root에 자식 엔티티들을 부착한 뒤 단일 save 호출로 cascade 영속화한다")
    void should_attach_children_and_save_aggregate_in_single_call() {
        // given
        Portfolio domain = buildPortfolio();
        PortfolioJpaEntity portfolioEntity = mock(PortfolioJpaEntity.class);
        PortfolioContentJpaEntity contentEntity = mock(PortfolioContentJpaEntity.class);
        PortfolioJobCategoryJpaEntity jobCategoryEntity = mock(PortfolioJobCategoryJpaEntity.class);
        PortfolioTagJpaEntity tagEntity1 = mock(PortfolioTagJpaEntity.class);
        PortfolioTagJpaEntity tagEntity2 = mock(PortfolioTagJpaEntity.class);

        given(portfolioMapper.toJpaEntity(domain)).willReturn(portfolioEntity);
        given(portfolioContentMapper.toJpaEntity(domain.getPortfolioContent())).willReturn(contentEntity);
        given(portfolioJobCategoryMapper.toJpaEntity(domain.getPortfolioJobCategory())).willReturn(jobCategoryEntity);
        given(portfolioTagMapper.toJpaEntity(domain.getPortfolioTags().get(0))).willReturn(tagEntity1);
        given(portfolioTagMapper.toJpaEntity(domain.getPortfolioTags().get(1))).willReturn(tagEntity2);

        // when
        Portfolio result = sut.save(domain);

        // then
        assertThat(result).isSameAs(domain);

        // 자식들이 Aggregate Root 헬퍼를 통해 부착됨
        then(portfolioEntity).should().syncPortfolioContent(contentEntity);
        then(portfolioEntity).should().addPortfolioTag(tagEntity1);
        then(portfolioEntity).should().addPortfolioTag(tagEntity2);

        // 단일 save 호출 — cascade로 자식 영속화 위임
        then(portfolioRepository).should().save(portfolioEntity);
    }

    @Test
    @DisplayName("태그가 없으면 addPortfolioTag는 호출되지 않는다")
    void should_not_invoke_add_tag_when_no_tags() {
        // given — 태그 비어있는 포트폴리오
        Portfolio domain = Portfolio.create(
                1L, null, null, null,
                "title", "본문", null,
                9L, null,
                "{}", "<p>본문</p>",
                List.of(),
                List.of(),
                List.of(),
                CollaborationType.PERSONAL, Visibility.PRIVATE
        );

        PortfolioJpaEntity portfolioEntity = mock(PortfolioJpaEntity.class);
        PortfolioContentJpaEntity contentEntity = mock(PortfolioContentJpaEntity.class);
        PortfolioJobCategoryJpaEntity jobCategoryEntity = mock(PortfolioJobCategoryJpaEntity.class);

        given(portfolioMapper.toJpaEntity(domain)).willReturn(portfolioEntity);
        given(portfolioContentMapper.toJpaEntity(domain.getPortfolioContent())).willReturn(contentEntity);
        given(portfolioJobCategoryMapper.toJpaEntity(domain.getPortfolioJobCategory())).willReturn(jobCategoryEntity);

        // when
        sut.save(domain);

        // then
        then(portfolioEntity).should(never()).addPortfolioTag(org.mockito.ArgumentMatchers.any());
        then(portfolioRepository).should().save(portfolioEntity);
    }
}
