package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.assembler.PortfolioDetailAssembler;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPortfolioDetailUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.ExistsPortfolioMemberInterestPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.ExistsPortfolioMemberViewPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadCoverLetterContentSharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadJobCategorySharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadProfileNicknameSharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadResumeContentSharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.PublishDomainEventsSharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.dto.response.CoverLetterContentResult;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.dto.response.PortfolioJobCategoryHierarchyResult;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.dto.response.ResumeContentResult;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@DisplayName("LoadPortfolioDetailUseCaseImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class LoadPortfolioDetailUseCaseImplTest {

    private LoadPortfolioDetailUseCaseImpl sut;

    @Mock private LoadPortfolioPort loadPortfolioPort;
    @Mock private LoadProfileNicknameSharedPort loadProfileNicknamePort;
    @Mock private LoadJobCategorySharedPort loadJobCategoryPort;
    @Mock private LoadResumeContentSharedPort loadResumeContentPort;
    @Mock private LoadCoverLetterContentSharedPort loadCoverLetterContentPort;
    @Mock private ExistsPortfolioMemberViewPort existsPortfolioMemberViewPort;
    @Mock private ExistsPortfolioMemberInterestPort existsPortfolioMemberInterestPort;
    @Mock private PublishDomainEventsSharedPort publishDomainEventsPort;

    // 어셈블러는 실제 구현을 목 포트로 배선 — execute 를 통한 응답 조립 동작을 그대로 검증
    @BeforeEach
    void setUp() {
        PortfolioDetailAssembler portfolioDetailAssembler = new PortfolioDetailAssembler(
                loadPortfolioPort,
                loadJobCategoryPort,
                loadResumeContentPort,
                loadCoverLetterContentPort
        );
        sut = new LoadPortfolioDetailUseCaseImpl(
                loadPortfolioPort,
                loadProfileNicknamePort,
                existsPortfolioMemberViewPort,
                existsPortfolioMemberInterestPort,
                publishDomainEventsPort,
                portfolioDetailAssembler
        );
    }

    private static final Long OWNER_ID = 1L;
    private static final Long OTHER_VIEWER_ID = 2L;
    private static final Long PORTFOLIO_ID = 9001L;
    private static final Long JOB_CATEGORY_ID = 9L;
    private static final Long RESUME_ID = 7100L;
    private static final Long COVER_LETTER_ID = 7200L;
    private static final String NICKNAME = "hirecore_user";

    private static Portfolio portfolioOf(
            Long ownerId,
            Visibility visibility,
            Long resumeId,
            Long coverLetterId
    ) {
        return Portfolio.create(
                ownerId,
                null,
                coverLetterId,
                resumeId,
                "포트폴리오 제목",
                "미리보기",
                null,
                JOB_CATEGORY_ID,
                null,
                "{\"type\":\"doc\"}",
                "<p>본문</p>",
                List.of(),
                List.of(),
                List.of(),
                CollaborationType.PERSONAL,
                visibility
        );
    }

    private void stubCommonPorts() {
        given(loadProfileNicknamePort.findNickname(OWNER_ID)).willReturn(Optional.of(NICKNAME));
        given(loadJobCategoryPort.findJobCategoryHierarchy(JOB_CATEGORY_ID))
                .willReturn(List.of(new PortfolioJobCategoryHierarchyResult(JOB_CATEGORY_ID, 1L, "DEV", "개발")));
    }

    @Nested
    @DisplayName("연결 자원 본문 노출 매트릭스")
    class LinkedResourceVisibilityMatrixTest {

        @Test
        @DisplayName("자원이 PUBLIC 이면 viewer 가 누구든 본문이 노출된다")
        void should_expose_content_when_resource_public() {
            // given - viewer 는 자원 소유자가 아님
            Portfolio portfolio = portfolioOf(OWNER_ID, Visibility.PUBLIC, RESUME_ID, COVER_LETTER_ID);
            given(loadPortfolioPort.findById(PORTFOLIO_ID)).willReturn(Optional.of(portfolio));
            stubCommonPorts();
            given(existsPortfolioMemberViewPort.exists(portfolio.getId(), OTHER_VIEWER_ID)).willReturn(true);
            given(existsPortfolioMemberInterestPort.exists(portfolio.getId(), OTHER_VIEWER_ID)).willReturn(false);
            given(loadResumeContentPort.findById(RESUME_ID))
                    .willReturn(Optional.of(new ResumeContentResult(
                            RESUME_ID, "이력서 제목", OWNER_ID, Visibility.PUBLIC, "{\"r\":1}", "<p>이력서</p>"
                    )));
            given(loadCoverLetterContentPort.findById(COVER_LETTER_ID))
                    .willReturn(Optional.of(new CoverLetterContentResult(
                            COVER_LETTER_ID, "자소서 제목", OWNER_ID, Visibility.PUBLIC, "{\"c\":1}", "<p>자소서</p>"
                    )));

            // when
            LoadPortfolioDetailUseCase.Response response = sut.execute(PORTFOLIO_ID, OTHER_VIEWER_ID);

            // then
            assertThat(response.linkedResume()).isNotNull();
            assertThat(response.linkedResume().content()).isNotNull();
            assertThat(response.linkedResume().content().html()).isEqualTo("<p>이력서</p>");
            assertThat(response.linkedCoverLetter()).isNotNull();
            assertThat(response.linkedCoverLetter().content()).isNotNull();
            assertThat(response.linkedCoverLetter().content().html()).isEqualTo("<p>자소서</p>");
        }

        @Test
        @DisplayName("자원이 PRIVATE 이고 viewer 가 자원 소유자가 아니면 content 가 null 로 노출된다 (메타만 노출)")
        void should_hide_content_when_resource_private_and_viewer_not_owner() {
            // given
            Portfolio portfolio = portfolioOf(OWNER_ID, Visibility.PUBLIC, RESUME_ID, COVER_LETTER_ID);
            given(loadPortfolioPort.findById(PORTFOLIO_ID)).willReturn(Optional.of(portfolio));
            stubCommonPorts();
            given(existsPortfolioMemberViewPort.exists(portfolio.getId(), OTHER_VIEWER_ID)).willReturn(true);
            given(existsPortfolioMemberInterestPort.exists(portfolio.getId(), OTHER_VIEWER_ID)).willReturn(false);
            given(loadResumeContentPort.findById(RESUME_ID))
                    .willReturn(Optional.of(new ResumeContentResult(
                            RESUME_ID, "이력서 제목", OWNER_ID, Visibility.PRIVATE, "{\"r\":1}", "<p>이력서</p>"
                    )));
            given(loadCoverLetterContentPort.findById(COVER_LETTER_ID))
                    .willReturn(Optional.of(new CoverLetterContentResult(
                            COVER_LETTER_ID, "자소서 제목", OWNER_ID, Visibility.PRIVATE, "{\"c\":1}", "<p>자소서</p>"
                    )));

            // when
            LoadPortfolioDetailUseCase.Response response = sut.execute(PORTFOLIO_ID, OTHER_VIEWER_ID);

            // then
            assertThat(response.linkedResume()).isNotNull();
            assertThat(response.linkedResume().title()).isEqualTo("이력서 제목");
            assertThat(response.linkedResume().content()).isNull();
            assertThat(response.linkedCoverLetter()).isNotNull();
            assertThat(response.linkedCoverLetter().title()).isEqualTo("자소서 제목");
            assertThat(response.linkedCoverLetter().content()).isNull();
        }

        @Test
        @DisplayName("자원이 PRIVATE 이어도 viewer 가 자원 소유자이면 본문이 노출된다")
        void should_expose_content_when_resource_private_and_viewer_is_owner() {
            // given - viewer == 자원 소유자
            Portfolio portfolio = portfolioOf(OWNER_ID, Visibility.PRIVATE, RESUME_ID, COVER_LETTER_ID);
            given(loadPortfolioPort.findById(PORTFOLIO_ID)).willReturn(Optional.of(portfolio));
            stubCommonPorts();
            given(existsPortfolioMemberViewPort.exists(portfolio.getId(), OWNER_ID)).willReturn(true);
            given(loadResumeContentPort.findById(RESUME_ID))
                    .willReturn(Optional.of(new ResumeContentResult(
                            RESUME_ID, "이력서 제목", OWNER_ID, Visibility.PRIVATE, "{\"r\":1}", "<p>이력서</p>"
                    )));
            given(loadCoverLetterContentPort.findById(COVER_LETTER_ID))
                    .willReturn(Optional.of(new CoverLetterContentResult(
                            COVER_LETTER_ID, "자소서 제목", OWNER_ID, Visibility.PRIVATE, "{\"c\":1}", "<p>자소서</p>"
                    )));

            // when
            LoadPortfolioDetailUseCase.Response response = sut.execute(PORTFOLIO_ID, OWNER_ID);

            // then
            assertThat(response.linkedResume().content()).isNotNull();
            assertThat(response.linkedResume().content().html()).isEqualTo("<p>이력서</p>");
            assertThat(response.linkedCoverLetter().content()).isNotNull();
            assertThat(response.linkedCoverLetter().content().html()).isEqualTo("<p>자소서</p>");
        }

        @Test
        @DisplayName("연결된 자원 ID 가 없으면 linkedResume / linkedCoverLetter 가 null 이며 sharedkernel 포트는 호출되지 않는다")
        void should_return_null_when_linkage_absent() {
            // given - resumeId, coverLetterId 모두 null
            Portfolio portfolio = portfolioOf(OWNER_ID, Visibility.PUBLIC, null, null);
            given(loadPortfolioPort.findById(PORTFOLIO_ID)).willReturn(Optional.of(portfolio));
            stubCommonPorts();
            given(existsPortfolioMemberViewPort.exists(portfolio.getId(), OTHER_VIEWER_ID)).willReturn(true);
            given(existsPortfolioMemberInterestPort.exists(portfolio.getId(), OTHER_VIEWER_ID)).willReturn(false);

            // when
            LoadPortfolioDetailUseCase.Response response = sut.execute(PORTFOLIO_ID, OTHER_VIEWER_ID);

            // then
            assertThat(response.linkedResume()).isNull();
            assertThat(response.linkedCoverLetter()).isNull();
            then(loadResumeContentPort).should(never()).findById(anyLong());
            then(loadCoverLetterContentPort).should(never()).findById(anyLong());
        }

        @Test
        @DisplayName("연결 ID 는 있으나 자원이 삭제/미존재면 linkedResume / linkedCoverLetter 가 null 로 노출된다")
        void should_return_null_when_resource_missing() {
            // given
            Portfolio portfolio = portfolioOf(OWNER_ID, Visibility.PUBLIC, RESUME_ID, COVER_LETTER_ID);
            given(loadPortfolioPort.findById(PORTFOLIO_ID)).willReturn(Optional.of(portfolio));
            stubCommonPorts();
            given(existsPortfolioMemberViewPort.exists(portfolio.getId(), OTHER_VIEWER_ID)).willReturn(true);
            given(existsPortfolioMemberInterestPort.exists(portfolio.getId(), OTHER_VIEWER_ID)).willReturn(false);
            given(loadResumeContentPort.findById(RESUME_ID)).willReturn(Optional.empty());
            given(loadCoverLetterContentPort.findById(COVER_LETTER_ID)).willReturn(Optional.empty());

            // when
            LoadPortfolioDetailUseCase.Response response = sut.execute(PORTFOLIO_ID, OTHER_VIEWER_ID);

            // then
            assertThat(response.linkedResume()).isNull();
            assertThat(response.linkedCoverLetter()).isNull();
        }
    }

    @Nested
    @DisplayName("publisher.otherPortfolios 합성")
    class PublisherOtherPortfoliosTest {

        @Test
        @DisplayName("작성자의 PUBLIC 작품 중 본 포트폴리오를 제외한 전체가 updatedAt 내림차순으로 합성된다")
        void should_compose_other_public_portfolios() {
            // given - 본 포트폴리오 1건 + 다른 PUBLIC 작품 2건
            Portfolio portfolio = portfolioOf(OWNER_ID, Visibility.PUBLIC, null, null);
            Portfolio other1 = portfolioOf(OWNER_ID, Visibility.PUBLIC, null, null);
            Portfolio other2 = portfolioOf(OWNER_ID, Visibility.PUBLIC, null, null);
            given(loadPortfolioPort.findById(PORTFOLIO_ID)).willReturn(Optional.of(portfolio));
            stubCommonPorts();
            given(existsPortfolioMemberViewPort.exists(portfolio.getId(), OTHER_VIEWER_ID)).willReturn(true);
            given(existsPortfolioMemberInterestPort.exists(portfolio.getId(), OTHER_VIEWER_ID)).willReturn(false);
            given(loadPortfolioPort
                    .findAllPublicByMemberAccountIdExcludingOrderByUpdatedAtDesc(OWNER_ID, portfolio.getId()))
                    .willReturn(List.of(other1, other2));
            // 다른 K개 포트폴리오의 직무 계층은 단일 일괄 호출로 가져온다
            given(loadJobCategoryPort.findJobCategoryHierarchies(List.of(JOB_CATEGORY_ID, JOB_CATEGORY_ID)))
                    .willReturn(Map.of(JOB_CATEGORY_ID, List.of(
                            new PortfolioJobCategoryHierarchyResult(JOB_CATEGORY_ID, 1L, "DEV", "개발")
                    )));

            // when
            LoadPortfolioDetailUseCase.Response response = sut.execute(PORTFOLIO_ID, OTHER_VIEWER_ID);

            // then
            assertThat(response.publisher().otherPortfolios()).hasSize(2);
            assertThat(response.publisher().otherPortfolios())
                    .extracting("portfolioId")
                    .containsExactly(other1.getId(), other2.getId());
            assertThat(response.publisher().otherPortfolios().getFirst().jobCategories())
                    .extracting("categoryCode").containsExactly("DEV");
        }

        @Test
        @DisplayName("다른 PUBLIC 작품이 없으면 빈 배열로 응답된다 (객체는 유지)")
        void should_return_empty_array_when_no_other_public_portfolios() {
            // given - loadPortfoliosByMember 가 빈 리스트 반환 (Mockito 기본값)
            Portfolio portfolio = portfolioOf(OWNER_ID, Visibility.PUBLIC, null, null);
            given(loadPortfolioPort.findById(PORTFOLIO_ID)).willReturn(Optional.of(portfolio));
            stubCommonPorts();
            given(existsPortfolioMemberViewPort.exists(portfolio.getId(), OTHER_VIEWER_ID)).willReturn(true);
            given(existsPortfolioMemberInterestPort.exists(portfolio.getId(), OTHER_VIEWER_ID)).willReturn(false);
            given(loadPortfolioPort
                    .findAllPublicByMemberAccountIdExcludingOrderByUpdatedAtDesc(OWNER_ID, portfolio.getId()))
                    .willReturn(List.of());

            // when
            LoadPortfolioDetailUseCase.Response response = sut.execute(PORTFOLIO_ID, OTHER_VIEWER_ID);

            // then
            assertThat(response.publisher()).isNotNull();
            assertThat(response.publisher().otherPortfolios()).isEmpty();
        }
    }

    @Nested
    @DisplayName("접근 제어")
    class AccessControlTest {

        @Test
        @DisplayName("PRIVATE 포트폴리오를 비소유자가 조회하면 PORTFOLIO_FORBIDDEN")
        void should_throw_forbidden_when_private_and_not_owner() {
            // given
            Portfolio portfolio = portfolioOf(OWNER_ID, Visibility.PRIVATE, null, null);
            given(loadPortfolioPort.findById(PORTFOLIO_ID)).willReturn(Optional.of(portfolio));

            // when & then
            assertThatThrownBy(() -> sut.execute(PORTFOLIO_ID, OTHER_VIEWER_ID))
                    .isInstanceOf(PortfolioApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_FORBIDDEN.getErrorCode());
        }

        @Test
        @DisplayName("존재하지 않는 포트폴리오면 PORTFOLIO_NOT_FOUND")
        void should_throw_not_found_when_portfolio_missing() {
            // given
            given(loadPortfolioPort.findById(PORTFOLIO_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.execute(PORTFOLIO_ID, OWNER_ID))
                    .isInstanceOf(PortfolioApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_NOT_FOUND.getErrorCode());
        }
    }
}
