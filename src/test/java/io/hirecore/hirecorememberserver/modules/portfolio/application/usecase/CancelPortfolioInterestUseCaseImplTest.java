package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.event.PortfolioInterestCancelledEvent;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@DisplayName("CancelPortfolioInterestUseCaseImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class CancelPortfolioInterestUseCaseImplTest {

    @InjectMocks
    private CancelPortfolioInterestUseCaseImpl sut;

    @Mock
    private LoadPortfolioPort loadPortfolioPort;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private static final Long OWNER_ID = 1L;
    private static final Long MEMBER_ACCOUNT_ID = 2L;
    private static final Long PORTFOLIO_ID = 9001L;
    private static final Long JOB_CATEGORY_ID = 9L;
    private static final Long THUMBNAIL_IMAGE_ID = 100L;
    private static final List<Long> CONTENT_IMAGE_IDS = List.of(101L, 102L);

    private static Portfolio portfolioOf(Long ownerId, Visibility visibility) {
        return Portfolio.create(
                ownerId,
                THUMBNAIL_IMAGE_ID,
                null,
                null,
                "포트폴리오 제목",
                "미리보기",
                null,
                JOB_CATEGORY_ID,
                null,
                "{\"type\":\"doc\"}",
                "<p>본문</p>",
                CONTENT_IMAGE_IDS,
                List.of(),
                List.of(),
                CollaborationType.PERSONAL,
                visibility
        );
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessTest {

        @Test
        @DisplayName("관심 해제 호출 시 PortfolioInterestCancelledEvent 가 발행된다")
        void should_publish_event_on_cancel() {
            // given
            Portfolio loaded = portfolioOf(OWNER_ID, Visibility.PUBLIC);
            given(loadPortfolioPort.findPortfolio(PORTFOLIO_ID)).willReturn(Optional.of(loaded));

            // when
            sut.execute(PORTFOLIO_ID, MEMBER_ACCOUNT_ID);

            // then
            ArgumentCaptor<PortfolioInterestCancelledEvent> captor = ArgumentCaptor.forClass(PortfolioInterestCancelledEvent.class);
            then(applicationEventPublisher).should().publishEvent(captor.capture());
            assertThat(captor.getValue().portfolioId()).isEqualTo(loaded.getId());
            assertThat(captor.getValue().memberAccountId()).isEqualTo(MEMBER_ACCOUNT_ID);
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailureTest {

        @Test
        @DisplayName("존재하지 않는 포트폴리오면 INTEREST_PORTFOLIO_NOT_FOUND 예외를 던지고 이벤트는 발행되지 않는다")
        void should_throw_when_portfolio_not_found() {
            // given
            given(loadPortfolioPort.findPortfolio(PORTFOLIO_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.execute(PORTFOLIO_ID, MEMBER_ACCOUNT_ID))
                    .isInstanceOf(PortfolioApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioApplicationExceptionCodeCluster.DetailResponse.INTEREST_PORTFOLIO_NOT_FOUND.getErrorCode());

            then(applicationEventPublisher).should(never()).publishEvent(any());
        }
    }
}
