package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.DecrementPortfolioInterestCountPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.DeletePortfolioMemberInterestPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@DisplayName("CancelPortfolioInterestUseCaseImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class CancelPortfolioInterestUseCaseImplTest {

    @InjectMocks
    private CancelPortfolioInterestUseCaseImpl sut;

    @Mock
    private DeletePortfolioMemberInterestPort deletePortfolioMemberInterestPort;

    @Mock
    private DecrementPortfolioInterestCountPort decrementPortfolioInterestCountPort;

    private static final Long PORTFOLIO_ID = 9001L;
    private static final Long MEMBER_ACCOUNT_ID = 2L;

    @Nested
    @DisplayName("성공 케이스")
    class SuccessTest {

        @Test
        @DisplayName("등록된 관심이 있어 실제 삭제가 발생하면 카운트 감소까지 호출된다")
        void should_decrement_when_actually_deleted() {
            // given
            given(deletePortfolioMemberInterestPort.deleteBy(PORTFOLIO_ID, MEMBER_ACCOUNT_ID)).willReturn(true);

            // when
            sut.execute(PORTFOLIO_ID, MEMBER_ACCOUNT_ID);

            // then
            then(deletePortfolioMemberInterestPort).should().deleteBy(PORTFOLIO_ID, MEMBER_ACCOUNT_ID);
            then(decrementPortfolioInterestCountPort).should().decrementInterestCountById(PORTFOLIO_ID);
        }

        @Test
        @DisplayName("등록된 관심이 없어 삭제 영향이 없으면 카운트 감소는 호출되지 않는다 (멱등)")
        void should_skip_decrement_when_nothing_deleted() {
            // given
            given(deletePortfolioMemberInterestPort.deleteBy(PORTFOLIO_ID, MEMBER_ACCOUNT_ID)).willReturn(false);

            // when
            sut.execute(PORTFOLIO_ID, MEMBER_ACCOUNT_ID);

            // then
            then(deletePortfolioMemberInterestPort).should().deleteBy(PORTFOLIO_ID, MEMBER_ACCOUNT_ID);
            then(decrementPortfolioInterestCountPort).should(never()).decrementInterestCountById(anyLong());
        }
    }
}
