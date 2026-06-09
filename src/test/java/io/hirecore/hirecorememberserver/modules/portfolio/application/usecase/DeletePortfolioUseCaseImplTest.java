package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.DeletePortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.PublishDomainEventsPort;
import io.hirecore.hirecorememberserver.sharedkernel.domain.AbstractDomainEventPublisher;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@DisplayName("DeletePortfolioUseCaseImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class DeletePortfolioUseCaseImplTest {

    @InjectMocks
    private DeletePortfolioUseCaseImpl sut;

    @Mock
    private LoadPortfolioPort loadPortfolioPort;

    @Mock
    private DeletePortfolioPort deletePortfolioPort;

    @Mock
    private PublishDomainEventsPort publishDomainEventsPort;

    private static final Long OWNER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;
    private static final Long PORTFOLIO_ID = 9001L;
    private static final Long JOB_CATEGORY_LEAF_ID = 9L;
    private static final Long THUMBNAIL_IMAGE_ID = 100L;
    private static final List<Long> CONTENT_IMAGE_IDS = List.of(101L, 102L);

    private static Portfolio portfolioOf(Long ownerId) {
        return Portfolio.create(
                ownerId,
                THUMBNAIL_IMAGE_ID,
                null,
                null,
                "포트폴리오 제목",
                "미리보기",
                null,
                JOB_CATEGORY_LEAF_ID,
                null,
                "{\"type\":\"doc\"}",
                "<p>본문</p>",
                CONTENT_IMAGE_IDS,
                List.of(),
                List.of(),
                CollaborationType.PERSONAL,
                Visibility.PUBLIC
        );
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessTest {

        @Test
        @DisplayName("작성자 본인이 호출하면 deletePort 호출 후 publishAll 이 위임된다")
        void should_delete_and_publish_when_owner() {
            // given
            Portfolio loaded = portfolioOf(OWNER_ID);
            given(loadPortfolioPort.findPortfolio(PORTFOLIO_ID)).willReturn(Optional.of(loaded));

            // when
            sut.execute(PORTFOLIO_ID, OWNER_ID);

            // then
            then(deletePortfolioPort).should().delete(loaded);
            then(publishDomainEventsPort).should().publishAll(loaded);
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailureTest {

        @Test
        @DisplayName("존재하지 않는 포트폴리오면 PORTFOLIO_NOT_FOUND 응용 예외를 던지고 후속 호출은 없다")
        void should_throw_when_portfolio_not_found() {
            // given
            given(loadPortfolioPort.findPortfolio(PORTFOLIO_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.execute(PORTFOLIO_ID, OWNER_ID))
                    .isInstanceOf(PortfolioApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_NOT_FOUND.getErrorCode());

            then(deletePortfolioPort).should(never()).delete(any());
            then(publishDomainEventsPort).should(never()).publishAll(any(AbstractDomainEventPublisher.class));
        }

        @Test
        @DisplayName("비소유자가 호출하면 AR 이 던진 PORTFOLIO_FORBIDDEN 도메인 예외가 전파되고 후속 호출은 없다")
        void should_propagate_domain_exception_when_non_owner() {
            // given
            Portfolio loaded = portfolioOf(OWNER_ID);
            given(loadPortfolioPort.findPortfolio(PORTFOLIO_ID)).willReturn(Optional.of(loaded));

            // when & then
            assertThatThrownBy(() -> sut.execute(PORTFOLIO_ID, OTHER_USER_ID))
                    .isInstanceOf(PortfolioDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioDomainExceptionCodeCluster.DetailResponse.PORTFOLIO_FORBIDDEN.getErrorCode());

            then(deletePortfolioPort).should(never()).delete(any());
            then(publishDomainEventsPort).should(never()).publishAll(any(AbstractDomainEventPublisher.class));
        }
    }
}
