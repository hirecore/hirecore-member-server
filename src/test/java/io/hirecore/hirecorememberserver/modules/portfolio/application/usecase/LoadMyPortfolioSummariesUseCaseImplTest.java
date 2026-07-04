package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.assembler.MyPortfolioSummariesAssembler;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadMyPortfolioSummariesUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadCoverLetterTitlePort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadImageUrlPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadJobCategoryPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadResumeTitlePort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.dto.response.PortfolioJobCategoryHierarchyResult;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@DisplayName("LoadMyPortfolioSummariesUseCaseImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class LoadMyPortfolioSummariesUseCaseImplTest {

    private LoadMyPortfolioSummariesUseCaseImpl sut;

    @Mock
    private LoadPortfolioPort loadPortfolioPort;

    @Mock
    private LoadResumeTitlePort loadResumeTitlePort;

    @Mock
    private LoadCoverLetterTitlePort loadCoverLetterTitlePort;

    @Mock
    private LoadJobCategoryPort loadJobCategoryPort;

    @Mock
    private LoadImageUrlPort loadImageUrlPort;

    // 어셈블러는 실제 구현을 목 포트로 배선 — execute 를 통한 응답 조립 동작을 그대로 검증
    @BeforeEach
    void setUp() {
        MyPortfolioSummariesAssembler assembler = new MyPortfolioSummariesAssembler(
                loadResumeTitlePort,
                loadCoverLetterTitlePort,
                loadJobCategoryPort,
                loadImageUrlPort
        );
        sut = new LoadMyPortfolioSummariesUseCaseImpl(loadPortfolioPort, assembler);
    }

    private static final Long VIEWER_ID = 1L;
    private static final Long JOB_CATEGORY_ID = 9001L;

    private static Portfolio portfolioOf(
            Long thumbnailImageId,
            Long resumeId,
            Long coverLetterId
    ) {
        return Portfolio.create(
                VIEWER_ID,
                thumbnailImageId,
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
                Visibility.PUBLIC
        );
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessTest {

        @Test
        @DisplayName("보유 포트폴리오가 없으면 빈 items 를 반환하고 외부 조회를 호출하지 않는다")
        void should_return_empty_when_no_portfolios() {
            // given
            given(loadPortfolioPort.findAllByMemberAccountIdOrderByUpdatedAtDesc(VIEWER_ID))
                    .willReturn(List.of());

            // when
            LoadMyPortfolioSummariesUseCase.Response response = sut.execute(VIEWER_ID);

            // then
            assertThat(response.items()).isEmpty();
            then(loadResumeTitlePort).should(never()).findTitleMapByIds(any());
            then(loadCoverLetterTitlePort).should(never()).findTitleMapByIds(any());
            then(loadJobCategoryPort).should(never()).findJobCategoryHierarchy(anyLong());
            then(loadImageUrlPort).should(never()).findUrlById(anyLong());
        }

        @Test
        @DisplayName("연결된 이력서/자기소개서의 제목을 합성해 응답한다 (썸네일 URL 도 함께 해소)")
        void should_compose_titles_and_thumbnail_url() {
            // given
            Long resumeId = 7100L;
            Long coverLetterId = 7200L;
            Long thumbnailImageId = 5100L;
            Portfolio loaded = portfolioOf(thumbnailImageId, resumeId, coverLetterId);

            given(loadPortfolioPort.findAllByMemberAccountIdOrderByUpdatedAtDesc(VIEWER_ID))
                    .willReturn(List.of(loaded));
            given(loadResumeTitlePort.findTitleMapByIds(Set.of(resumeId)))
                    .willReturn(Map.of(resumeId, "백엔드 신입 이력서"));
            given(loadCoverLetterTitlePort.findTitleMapByIds(Set.of(coverLetterId)))
                    .willReturn(Map.of(coverLetterId, "B사 지원용 자소서"));
            given(loadJobCategoryPort.findJobCategoryHierarchy(JOB_CATEGORY_ID))
                    .willReturn(List.of(
                            new PortfolioJobCategoryHierarchyResult(1001L, 1L, "DEV", "개발"),
                            new PortfolioJobCategoryHierarchyResult(JOB_CATEGORY_ID, 2L, "DEV_BACKEND", "백엔드")
                    ));
            given(loadImageUrlPort.findUrlById(thumbnailImageId))
                    .willReturn(Optional.of("https://cdn.example.com/portfolio/thumbnail/abc.webp"));

            // when
            LoadMyPortfolioSummariesUseCase.Response response = sut.execute(VIEWER_ID);

            // then
            assertThat(response.items()).hasSize(1);
            LoadMyPortfolioSummariesUseCase.Response.Item item = response.items().getFirst();
            assertThat(item.portfolioId()).isEqualTo(loaded.getId());
            assertThat(item.linkedResume()).isNotNull();
            assertThat(item.linkedResume().title()).isEqualTo("백엔드 신입 이력서");
            assertThat(item.linkedCoverLetter()).isNotNull();
            assertThat(item.linkedCoverLetter().title()).isEqualTo("B사 지원용 자소서");
            assertThat(item.thumbnail()).isNotNull();
            assertThat(item.thumbnail().imageId()).isEqualTo(thumbnailImageId);
            assertThat(item.thumbnail().imageUrl()).isEqualTo("https://cdn.example.com/portfolio/thumbnail/abc.webp");
            assertThat(item.jobCategories()).extracting("categoryCode").containsExactly("DEV", "DEV_BACKEND");
        }

        @Test
        @DisplayName("연결이 없는 포트폴리오는 linkedResume / linkedCoverLetter 가 null 로 응답된다")
        void should_return_null_links_when_not_linked() {
            // given
            Portfolio loaded = portfolioOf(null, null, null);

            given(loadPortfolioPort.findAllByMemberAccountIdOrderByUpdatedAtDesc(VIEWER_ID))
                    .willReturn(List.of(loaded));
            given(loadResumeTitlePort.findTitleMapByIds(Set.of())).willReturn(Map.of());
            given(loadCoverLetterTitlePort.findTitleMapByIds(Set.of())).willReturn(Map.of());
            given(loadJobCategoryPort.findJobCategoryHierarchy(JOB_CATEGORY_ID))
                    .willReturn(List.of(
                            new PortfolioJobCategoryHierarchyResult(JOB_CATEGORY_ID, 1L, "DEV", "개발")
                    ));

            // when
            LoadMyPortfolioSummariesUseCase.Response response = sut.execute(VIEWER_ID);

            // then
            LoadMyPortfolioSummariesUseCase.Response.Item item = response.items().getFirst();
            assertThat(item.linkedResume()).isNull();
            assertThat(item.linkedCoverLetter()).isNull();
            assertThat(item.thumbnail()).isNull();
            then(loadImageUrlPort).should(never()).findUrlById(anyLong());
        }

        @Test
        @DisplayName("제목 합성 맵에 항목이 없으면 해당 링크 필드는 null 로 응답된다 (resume 데이터가 사라진 경우)")
        void should_return_null_when_title_not_found_in_map() {
            // given
            Long resumeId = 7100L;
            Portfolio loaded = portfolioOf(null, resumeId, null);

            given(loadPortfolioPort.findAllByMemberAccountIdOrderByUpdatedAtDesc(VIEWER_ID))
                    .willReturn(List.of(loaded));
            given(loadResumeTitlePort.findTitleMapByIds(Set.of(resumeId))).willReturn(Map.of());
            given(loadCoverLetterTitlePort.findTitleMapByIds(Set.of())).willReturn(Map.of());
            given(loadJobCategoryPort.findJobCategoryHierarchy(JOB_CATEGORY_ID))
                    .willReturn(List.of(
                            new PortfolioJobCategoryHierarchyResult(JOB_CATEGORY_ID, 1L, "DEV", "개발")
                    ));

            // when
            LoadMyPortfolioSummariesUseCase.Response response = sut.execute(VIEWER_ID);

            // then
            assertThat(response.items().getFirst().linkedResume()).isNull();
        }

        @Test
        @DisplayName("여러 포트폴리오의 연결 ID 들을 중복 없이 bulk 조회한다")
        void should_bulk_collect_distinct_ids_across_portfolios() {
            // given
            Long sharedResumeId = 7100L;
            Long otherResumeId = 7200L;
            Portfolio first = portfolioOf(null, sharedResumeId, null);
            Portfolio second = portfolioOf(null, sharedResumeId, null);
            Portfolio third = portfolioOf(null, otherResumeId, null);

            given(loadPortfolioPort.findAllByMemberAccountIdOrderByUpdatedAtDesc(VIEWER_ID))
                    .willReturn(List.of(first, second, third));
            given(loadJobCategoryPort.findJobCategoryHierarchy(JOB_CATEGORY_ID))
                    .willReturn(List.of(
                            new PortfolioJobCategoryHierarchyResult(JOB_CATEGORY_ID, 1L, "DEV", "개발")
                    ));
            given(loadResumeTitlePort.findTitleMapByIds(Set.of(sharedResumeId, otherResumeId)))
                    .willReturn(Map.of(
                            sharedResumeId, "공통 이력서",
                            otherResumeId, "다른 이력서"
                    ));
            given(loadCoverLetterTitlePort.findTitleMapByIds(Set.of())).willReturn(Map.of());

            // when
            LoadMyPortfolioSummariesUseCase.Response response = sut.execute(VIEWER_ID);

            // then
            assertThat(response.items()).hasSize(3);
            assertThat(response.items())
                    .extracting(item -> item.linkedResume().title())
                    .containsExactly("공통 이력서", "공통 이력서", "다른 이력서");
        }
    }
}
