package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioContentJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJobCategoryJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity.PortfolioTagJpaEntity;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioContentJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioJobCategoryJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.mapper.PortfolioTagJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository.PortfolioJobCategoryJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository.PortfolioJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.repository.PortfolioTagJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
    private PortfolioContentJpaEntityMapper portfolioContentMapper;

    @Mock
    private PortfolioJobCategoryJpaEntityMapper portfolioJobCategoryMapper;

    @Mock
    private PortfolioTagJpaEntityMapper portfolioTagMapper;

    @Mock
    private PortfolioJpaCommandRepository portfolioRepository;

    @Mock
    private PortfolioJobCategoryJpaCommandRepository portfolioJobCategoryRepository;

    @Mock
    private PortfolioTagJpaCommandRepository portfolioTagRepository;

    private static Portfolio buildPortfolio() {
        return Portfolio.create(
                1L, 9L, null, 100L, 8001L, 7001L,
                "title", "{}", "<p>본문</p>", List.of(), null,
                List.of("Spring", "DDD"), CollaborationType.TEAM, Visibility.PUBLIC
        );
    }

    @Test
    @DisplayName("portfolio → content → jobCategory → tags 순으로 저장하고 입력 도메인 그대로 반환한다")
    void should_save_aggregate_in_correct_order_and_return_input() {
        // given
        Portfolio domain = buildPortfolio();
        PortfolioJpaEntity portfolioEntity = mock(PortfolioJpaEntity.class);
        PortfolioJpaEntity savedPortfolioEntity = mock(PortfolioJpaEntity.class);
        PortfolioContentJpaEntity contentEntity = mock(PortfolioContentJpaEntity.class);
        PortfolioJobCategoryJpaEntity jobCategoryEntity = mock(PortfolioJobCategoryJpaEntity.class);
        PortfolioTagJpaEntity tagEntity1 = mock(PortfolioTagJpaEntity.class);
        PortfolioTagJpaEntity tagEntity2 = mock(PortfolioTagJpaEntity.class);

        given(portfolioMapper.toJpaEntity(domain)).willReturn(portfolioEntity);
        given(portfolioContentMapper.toJpaEntity(domain.getPortfolioContent())).willReturn(contentEntity);
        given(portfolioRepository.save(portfolioEntity)).willReturn(savedPortfolioEntity);
        given(portfolioJobCategoryMapper.toJpaEntity(domain.getPortfolioJobCategory())).willReturn(jobCategoryEntity);
        given(portfolioTagMapper.toJpaEntity(domain.getPortfolioTags().get(0))).willReturn(tagEntity1);
        given(portfolioTagMapper.toJpaEntity(domain.getPortfolioTags().get(1))).willReturn(tagEntity2);

        // when
        Portfolio result = sut.save(domain);

        // then
        assertThat(result).isSameAs(domain);

        // 1. content 가 portfolioEntity에 동기화됨
        then(portfolioEntity).should().syncPortfolioContent(contentEntity);

        // 2. portfolio 저장
        then(portfolioRepository).should().save(portfolioEntity);

        // 3. 저장된 portfolio가 jobCategory, tags에 부착되고 각각 저장됨
        then(jobCategoryEntity).should().attachPortfolio(savedPortfolioEntity);
        then(portfolioJobCategoryRepository).should().save(jobCategoryEntity);

        then(tagEntity1).should().attachPortfolio(savedPortfolioEntity);
        then(tagEntity2).should().attachPortfolio(savedPortfolioEntity);

        ArgumentCaptor<PortfolioTagJpaEntity> tagCaptor = ArgumentCaptor.forClass(PortfolioTagJpaEntity.class);
        then(portfolioTagRepository).should(org.mockito.Mockito.times(2)).save(tagCaptor.capture());
        assertThat(tagCaptor.getAllValues()).containsExactly(tagEntity1, tagEntity2);
    }

    @Test
    @DisplayName("태그가 없으면 portfolioTagRepository는 호출되지 않는다")
    void should_skip_tag_save_when_no_tags() {
        // given — 태그 비어있는 포트폴리오
        Portfolio domain = Portfolio.create(
                1L, 9L, null, null, null, null,
                "title", "{}", "<p>본문</p>", List.of(), null,
                List.of(), CollaborationType.PERSONAL, Visibility.PRIVATE
        );

        PortfolioJpaEntity portfolioEntity = mock(PortfolioJpaEntity.class);
        PortfolioJpaEntity savedPortfolioEntity = mock(PortfolioJpaEntity.class);
        PortfolioContentJpaEntity contentEntity = mock(PortfolioContentJpaEntity.class);
        PortfolioJobCategoryJpaEntity jobCategoryEntity = mock(PortfolioJobCategoryJpaEntity.class);

        given(portfolioMapper.toJpaEntity(domain)).willReturn(portfolioEntity);
        given(portfolioContentMapper.toJpaEntity(domain.getPortfolioContent())).willReturn(contentEntity);
        given(portfolioRepository.save(portfolioEntity)).willReturn(savedPortfolioEntity);
        given(portfolioJobCategoryMapper.toJpaEntity(domain.getPortfolioJobCategory())).willReturn(jobCategoryEntity);

        // when
        sut.save(domain);

        // then
        then(portfolioTagRepository).shouldHaveNoInteractions();
        then(portfolioJobCategoryRepository).should().save(any());
    }
}
