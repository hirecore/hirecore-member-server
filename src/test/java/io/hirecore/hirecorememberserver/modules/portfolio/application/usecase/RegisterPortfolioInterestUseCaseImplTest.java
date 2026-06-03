package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.ExistsPortfolioMemberInterestPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.event.PortfolioInterestRegisteredEvent;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioDomainExceptionCodeCluster;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@DisplayName("RegisterPortfolioInterestUseCaseImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class RegisterPortfolioInterestUseCaseImplTest {

    @InjectMocks
    private RegisterPortfolioInterestUseCaseImpl sut;

    @Mock
    private LoadPortfolioPort loadPortfolioPort;

    @Mock
    private ExistsPortfolioMemberInterestPort existsPortfolioMemberInterestPort;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private static final Long OWNER_ID = 1L;
    private static final Long VIEWER_ID = 2L;
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
        @DisplayName("비소유자가 공개 포트폴리오에 첫 관심 등록 시 PortfolioInterestRegisteredEvent 가 발행된다")
        void should_publish_event_when_first_register() {
            // given
            Portfolio loaded = portfolioOf(OWNER_ID, Visibility.PUBLIC);
            given(loadPortfolioPort.findPortfolio(PORTFOLIO_ID)).willReturn(Optional.of(loaded));
            given(existsPortfolioMemberInterestPort.exists(PORTFOLIO_ID, VIEWER_ID)).willReturn(false);

            // when
            sut.execute(PORTFOLIO_ID, VIEWER_ID);

            // then
            ArgumentCaptor<PortfolioInterestRegisteredEvent> captor = ArgumentCaptor.forClass(PortfolioInterestRegisteredEvent.class);
            then(applicationEventPublisher).should().publishEvent(captor.capture());
            assertThat(captor.getValue().portfolioId()).isEqualTo(loaded.getId());
            assertThat(captor.getValue().memberAccountId()).isEqualTo(VIEWER_ID);
        }

        @Test
        @DisplayName("이미 관심 등록된 상태라면 이벤트를 발행하지 않고 멱등 종료한다")
        void should_skip_when_already_registered() {
            // given
            Portfolio loaded = portfolioOf(OWNER_ID, Visibility.PUBLIC);
            given(loadPortfolioPort.findPortfolio(PORTFOLIO_ID)).willReturn(Optional.of(loaded));
            given(existsPortfolioMemberInterestPort.exists(PORTFOLIO_ID, VIEWER_ID)).willReturn(true);

            // when
            sut.execute(PORTFOLIO_ID, VIEWER_ID);

            // then
            then(applicationEventPublisher).should(never()).publishEvent(any());
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailureTest {

        @Test
        @DisplayName("존재하지 않는 포트폴리오면 INTEREST_PORTFOLIO_NOT_FOUND 예외를 던지고 후속 호출은 없다")
        void should_throw_when_portfolio_not_found() {
            // given
            given(loadPortfolioPort.findPortfolio(PORTFOLIO_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.execute(PORTFOLIO_ID, VIEWER_ID))
                    .isInstanceOf(PortfolioApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioApplicationExceptionCodeCluster.DetailResponse.INTEREST_PORTFOLIO_NOT_FOUND.getErrorCode());

            then(existsPortfolioMemberInterestPort).should(never()).exists(anyLong(), anyLong());
            then(applicationEventPublisher).should(never()).publishEvent(any());
        }

        @Test
        @DisplayName("본인이 등록한 포트폴리오에는 INTEREST_OWNER_NOT_ALLOWED 도메인 예외를 던진다")
        void should_throw_when_viewer_is_owner() {
            // given
            Portfolio loaded = portfolioOf(OWNER_ID, Visibility.PUBLIC);
            given(loadPortfolioPort.findPortfolio(PORTFOLIO_ID)).willReturn(Optional.of(loaded));
            given(existsPortfolioMemberInterestPort.exists(PORTFOLIO_ID, OWNER_ID)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> sut.execute(PORTFOLIO_ID, OWNER_ID))
                    .isInstanceOf(PortfolioDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioDomainExceptionCodeCluster.DetailResponse.INTEREST_OWNER_NOT_ALLOWED.getErrorCode());

            then(applicationEventPublisher).should(never()).publishEvent(any());
        }

        @Test
        @DisplayName("비공개 포트폴리오에 비소유자가 시도하면 INTEREST_PORTFOLIO_FORBIDDEN 도메인 예외를 던진다")
        void should_throw_when_portfolio_is_private() {
            // given
            Portfolio loaded = portfolioOf(OWNER_ID, Visibility.PRIVATE);
            given(loadPortfolioPort.findPortfolio(PORTFOLIO_ID)).willReturn(Optional.of(loaded));
            given(existsPortfolioMemberInterestPort.exists(PORTFOLIO_ID, VIEWER_ID)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> sut.execute(PORTFOLIO_ID, VIEWER_ID))
                    .isInstanceOf(PortfolioDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioDomainExceptionCodeCluster.DetailResponse.INTEREST_PORTFOLIO_FORBIDDEN.getErrorCode());

            then(applicationEventPublisher).should(never()).publishEvent(any());
        }
    }
}
