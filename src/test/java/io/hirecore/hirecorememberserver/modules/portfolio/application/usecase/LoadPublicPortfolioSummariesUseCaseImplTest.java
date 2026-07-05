package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import io.hirecore.hirecorememberserver.modules.portfolio.application.assembler.PublicPortfolioSummariesAssembler;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.LoadPublicPortfolioSummariesUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.FindInterestedPortfolioIdsPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPublicPortfolioSummaryPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPublicPortfolioSummaryPort.PublicPortfolioRow;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.sharedkernel.application.cursor.EffectiveTimeCursor;
import io.hirecore.hirecorememberserver.sharedkernel.application.exception.SharedKernelApplicationException;
import io.hirecore.hirecorememberserver.sharedkernel.application.exception.SharedKernelApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadImageUrlSharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadJobCategorySharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadProfileNicknameSharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.BDDMockito.then;

@DisplayName("LoadPublicPortfolioSummariesUseCaseImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class LoadPublicPortfolioSummariesUseCaseImplTest {

    private LoadPublicPortfolioSummariesUseCaseImpl sut;

    @Mock
    private LoadPublicPortfolioSummaryPort loadPublicPortfolioSummaryPort;

    @Mock
    private LoadJobCategorySharedPort loadJobCategorySharedPort;

    @Mock
    private LoadProfileNicknameSharedPort loadProfileNicknameSharedPort;

    @Mock
    private LoadImageUrlSharedPort loadImageUrlSharedPort;

    @Mock
    private FindInterestedPortfolioIdsPort findInterestedPortfolioIdsPort;

    // 어셈블러는 실제 구현을 목 포트로 배선 — execute 를 통한 응답 조립 동작을 그대로 검증
    @BeforeEach
    void setUp() {
        PublicPortfolioSummariesAssembler assembler = new PublicPortfolioSummariesAssembler(
                loadJobCategorySharedPort,
                loadProfileNicknameSharedPort,
                loadImageUrlSharedPort,
                findInterestedPortfolioIdsPort
        );
        sut = new LoadPublicPortfolioSummariesUseCaseImpl(loadPublicPortfolioSummaryPort, assembler);
    }

    private static final Long JOB_CATEGORY_ID = 9001L;

    private static Portfolio portfolioOf(Long memberAccountId, Long thumbnailImageId) {
        return Portfolio.create(
                memberAccountId,
                thumbnailImageId,
                null,
                null,
                "포트폴리오 제목",
                "미리보기 요약",
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

    private static PublicPortfolioRow rowOf(Portfolio portfolio, Instant effectiveUpdatedAt) {
        return new PublicPortfolioRow(portfolio, effectiveUpdatedAt);
    }

    @Nested
    @DisplayName("페이지네이션 동작")
    class PaginationTest {

        @Test
        @DisplayName("첫 페이지 요청 시 port 에 cursor 인자를 null 로 전달한다 (size + 1 limit 으로 hasNext 판정)")
        void should_call_port_with_null_cursor_on_first_page() {
            // given
            given(loadPublicPortfolioSummaryPort
                    .findPublicPortfoliosOrderByEffectiveUpdatedAtDesc(any(), any(), anyInt()))
                    .willReturn(List.of());

            // when
            sut.execute(null, 20, null);

            // then
            ArgumentCaptor<Instant> cursorEffectiveCaptor = ArgumentCaptor.forClass(Instant.class);
            ArgumentCaptor<Long> cursorIdCaptor = ArgumentCaptor.forClass(Long.class);
            ArgumentCaptor<Integer> limitCaptor = ArgumentCaptor.forClass(Integer.class);
            then(loadPublicPortfolioSummaryPort).should()
                    .findPublicPortfoliosOrderByEffectiveUpdatedAtDesc(
                            cursorEffectiveCaptor.capture(),
                            cursorIdCaptor.capture(),
                            limitCaptor.capture()
                    );
            assertThat(cursorEffectiveCaptor.getValue()).isNull();
            assertThat(cursorIdCaptor.getValue()).isNull();
            assertThat(limitCaptor.getValue()).isEqualTo(21);
        }

        @Test
        @DisplayName("결과가 비어있으면 hasNext=false, nextCursor=null 로 응답한다 (외부 BC 조회 호출 없음)")
        void should_return_empty_when_no_rows() {
            // given
            given(loadPublicPortfolioSummaryPort
                    .findPublicPortfoliosOrderByEffectiveUpdatedAtDesc(any(), any(), anyInt()))
                    .willReturn(List.of());

            // when
            LoadPublicPortfolioSummariesUseCase.Response response = sut.execute(null, 20, null);

            // then
            assertThat(response.items()).isEmpty();
            assertThat(response.pagination().hasNext()).isFalse();
            assertThat(response.pagination().nextCursor()).isNull();
            then(loadProfileNicknameSharedPort).should(never()).findNickname(anyLong());
            then(loadImageUrlSharedPort).should(never()).findUrlById(anyLong());
        }

        @Test
        @DisplayName("결과 개수가 size 이하면 hasNext=false 로 응답한다")
        void should_set_has_next_false_when_rows_le_size() {
            // given
            Portfolio p1 = portfolioOf(101L, null);
            given(loadPublicPortfolioSummaryPort
                    .findPublicPortfoliosOrderByEffectiveUpdatedAtDesc(any(), any(), anyInt()))
                    .willReturn(List.of(rowOf(p1, Instant.parse("2026-06-10T15:00:00Z"))));
            given(loadJobCategorySharedPort.findJobCategoryHierarchies(any()))
                    .willReturn(Map.of());
            given(loadProfileNicknameSharedPort.findNickname(101L)).willReturn(Optional.of("euncheol"));

            // when
            LoadPublicPortfolioSummariesUseCase.Response response = sut.execute(null, 20, null);

            // then
            assertThat(response.items()).hasSize(1);
            assertThat(response.pagination().hasNext()).isFalse();
            assertThat(response.pagination().nextCursor()).isNull();
        }

        @Test
        @DisplayName("결과 개수가 size + 1 이면 hasNext=true, nextCursor 인코딩, 마지막 1개는 응답에서 제외된다")
        void should_set_has_next_true_and_encode_cursor_when_rows_exceed_size() {
            // given
            int size = 2;
            Instant effective1 = Instant.parse("2026-06-10T15:00:00Z");
            Instant effective2 = Instant.parse("2026-06-10T14:00:00Z");
            Instant effective3Discarded = Instant.parse("2026-06-10T13:00:00Z");
            Portfolio p1 = portfolioOf(101L, null);
            Portfolio p2 = portfolioOf(102L, null);
            Portfolio p3 = portfolioOf(103L, null); // size + 1번째 — 응답에서 제외돼야 함
            given(loadPublicPortfolioSummaryPort
                    .findPublicPortfoliosOrderByEffectiveUpdatedAtDesc(any(), any(), eq(size + 1)))
                    .willReturn(List.of(
                            rowOf(p1, effective1),
                            rowOf(p2, effective2),
                            rowOf(p3, effective3Discarded)
                    ));
            given(loadJobCategorySharedPort.findJobCategoryHierarchies(any())).willReturn(Map.of());
            given(loadProfileNicknameSharedPort.findNickname(anyLong())).willReturn(Optional.of("nick"));

            // when
            LoadPublicPortfolioSummariesUseCase.Response response = sut.execute(null, size, null);

            // then
            assertThat(response.items()).hasSize(size);
            assertThat(response.items())
                    .extracting(LoadPublicPortfolioSummariesUseCase.Response.Item::portfolioId)
                    .containsExactly(p1.getId(), p2.getId());
            assertThat(response.pagination().hasNext()).isTrue();
            assertThat(response.pagination().nextCursor()).isNotNull();

            // nextCursor 는 잘라낸 후 마지막 항목(p2, effective2) 기준으로 인코딩되어야 한다 (p3 기준 X)
            EffectiveTimeCursor decoded =
                    EffectiveTimeCursor.decode(response.pagination().nextCursor());
            assertThat(decoded.id()).isEqualTo(p2.getId());
            assertThat(decoded.time()).isEqualTo(effective2);
        }
    }

    @Nested
    @DisplayName("커서 토큰 처리")
    class CursorTest {

        @Test
        @DisplayName("커서 토큰을 디코드해 port 에 (effectiveUpdatedAt, portfolioId) 인자로 전달한다")
        void should_pass_decoded_cursor_to_port() {
            // given
            Instant cursorEffective = Instant.parse("2026-06-10T15:00:00.123456Z");
            Long cursorPortfolioId = 5555L;
            String token = new EffectiveTimeCursor(cursorEffective, cursorPortfolioId).encode();

            given(loadPublicPortfolioSummaryPort
                    .findPublicPortfoliosOrderByEffectiveUpdatedAtDesc(any(), any(), anyInt()))
                    .willReturn(List.of());

            // when
            sut.execute(token, 20, null);

            // then
            ArgumentCaptor<Instant> effectiveCaptor = ArgumentCaptor.forClass(Instant.class);
            ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
            then(loadPublicPortfolioSummaryPort).should()
                    .findPublicPortfoliosOrderByEffectiveUpdatedAtDesc(
                            effectiveCaptor.capture(),
                            idCaptor.capture(),
                            anyInt()
                    );
            assertThat(effectiveCaptor.getValue()).isEqualTo(cursorEffective);
            assertThat(idCaptor.getValue()).isEqualTo(cursorPortfolioId);
        }

        @Test
        @DisplayName("빈 문자열 커서는 첫 페이지로 간주된다 (null 과 동일)")
        void should_treat_blank_cursor_as_first_page() {
            // given
            given(loadPublicPortfolioSummaryPort
                    .findPublicPortfoliosOrderByEffectiveUpdatedAtDesc(any(), any(), anyInt()))
                    .willReturn(List.of());

            // when
            sut.execute("   ", 20, null);

            // then
            ArgumentCaptor<Instant> effectiveCaptor = ArgumentCaptor.forClass(Instant.class);
            then(loadPublicPortfolioSummaryPort).should()
                    .findPublicPortfoliosOrderByEffectiveUpdatedAtDesc(
                            effectiveCaptor.capture(),
                            any(),
                            anyInt()
                    );
            assertThat(effectiveCaptor.getValue()).isNull();
        }

        @Test
        @DisplayName("잘못된 형식의 커서는 INFINITE_SCROLL_CURSOR_INVALID 예외를 던진다")
        void should_throw_when_cursor_invalid() {
            // given
            String invalidToken = "this-is-not-a-valid-base64-cursor!!!";

            // when & then
            ThrowableAssert.ThrowingCallable callable = () -> sut.execute(invalidToken, 20, null);
            assertThatThrownBy(callable)
                    .isInstanceOf(SharedKernelApplicationException.class)
                    .extracting("applicationExceptionCode")
                    .isEqualTo(SharedKernelApplicationExceptionCodeCluster
                            .DetailResponse.INFINITE_SCROLL_CURSOR_INVALID);
        }
    }

    @Nested
    @DisplayName("Item 합성")
    class ItemCompositionTest {

        @Test
        @DisplayName("효과적 updatedAt 은 port 가 계산한 값을 그대로 응답에 노출한다 (Portfolio 자체의 updatedAt 이 아님)")
        void should_expose_port_provided_effective_updated_at() {
            // given
            Portfolio portfolio = portfolioOf(101L, null);
            Instant effectiveFromPort = Instant.parse("2026-06-15T10:00:00Z");
            given(loadPublicPortfolioSummaryPort
                    .findPublicPortfoliosOrderByEffectiveUpdatedAtDesc(any(), any(), anyInt()))
                    .willReturn(List.of(rowOf(portfolio, effectiveFromPort)));
            given(loadJobCategorySharedPort.findJobCategoryHierarchies(any())).willReturn(Map.of());
            given(loadProfileNicknameSharedPort.findNickname(anyLong())).willReturn(Optional.of("euncheol"));

            // when
            LoadPublicPortfolioSummariesUseCase.Response response = sut.execute(null, 20, null);

            // then
            assertThat(response.items().getFirst().updatedAt()).isEqualTo(effectiveFromPort);
        }

        @Test
        @DisplayName("썸네일 미등록 포트폴리오는 thumbnail 이 null (이미지 URL 조회를 호출하지 않음)")
        void should_set_thumbnail_null_when_not_attached() {
            // given
            Portfolio noThumbnail = portfolioOf(101L, null);
            given(loadPublicPortfolioSummaryPort
                    .findPublicPortfoliosOrderByEffectiveUpdatedAtDesc(any(), any(), anyInt()))
                    .willReturn(List.of(rowOf(noThumbnail, Instant.parse("2026-06-10T15:00:00Z"))));
            given(loadJobCategorySharedPort.findJobCategoryHierarchies(any())).willReturn(Map.of());
            given(loadProfileNicknameSharedPort.findNickname(anyLong())).willReturn(Optional.of("nick"));

            // when
            LoadPublicPortfolioSummariesUseCase.Response response = sut.execute(null, 20, null);

            // then
            assertThat(response.items().getFirst().thumbnail()).isNull();
            then(loadImageUrlSharedPort).should(never()).findUrlById(anyLong());
        }

        @Test
        @DisplayName("썸네일이 있으나 URL 해소 실패 시 imageId 만 채우고 imageUrl 은 null")
        void should_keep_image_id_when_url_resolution_fails() {
            // given
            Long thumbnailImageId = 7777L;
            Portfolio withThumbnail = portfolioOf(101L, thumbnailImageId);
            given(loadPublicPortfolioSummaryPort
                    .findPublicPortfoliosOrderByEffectiveUpdatedAtDesc(any(), any(), anyInt()))
                    .willReturn(List.of(rowOf(withThumbnail, Instant.parse("2026-06-10T15:00:00Z"))));
            given(loadJobCategorySharedPort.findJobCategoryHierarchies(any())).willReturn(Map.of());
            given(loadProfileNicknameSharedPort.findNickname(anyLong())).willReturn(Optional.of("nick"));
            given(loadImageUrlSharedPort.findUrlById(thumbnailImageId)).willReturn(Optional.empty());

            // when
            LoadPublicPortfolioSummariesUseCase.Response response = sut.execute(null, 20, null);

            // then
            assertThat(response.items().getFirst().thumbnail()).isNotNull();
            assertThat(response.items().getFirst().thumbnail().imageId()).isEqualTo(thumbnailImageId);
            assertThat(response.items().getFirst().thumbnail().imageUrl()).isNull();
        }

        @Test
        @DisplayName("닉네임 해소 실패 시 nickname 은 null 로 응답된다 (예외 X)")
        void should_set_nickname_null_when_lookup_fails() {
            // given
            Portfolio portfolio = portfolioOf(101L, null);
            given(loadPublicPortfolioSummaryPort
                    .findPublicPortfoliosOrderByEffectiveUpdatedAtDesc(any(), any(), anyInt()))
                    .willReturn(List.of(rowOf(portfolio, Instant.parse("2026-06-10T15:00:00Z"))));
            given(loadJobCategorySharedPort.findJobCategoryHierarchies(any())).willReturn(Map.of());
            given(loadProfileNicknameSharedPort.findNickname(101L)).willReturn(Optional.empty());

            // when
            LoadPublicPortfolioSummariesUseCase.Response response = sut.execute(null, 20, null);

            // then
            assertThat(response.items().getFirst().nickname()).isNull();
        }

        @Test
        @DisplayName("viewerId 가 작성자와 동일한 포트폴리오는 isOwner=true, 다른 작성자는 isOwner=false")
        void should_mark_is_owner_per_viewer() {
            // given
            Long viewerId = 101L;
            Portfolio mine = portfolioOf(viewerId, null);     // 작성자 == viewer
            Portfolio others = portfolioOf(202L, null);       // 작성자 != viewer
            given(loadPublicPortfolioSummaryPort
                    .findPublicPortfoliosOrderByEffectiveUpdatedAtDesc(any(), any(), anyInt()))
                    .willReturn(List.of(
                            rowOf(mine, Instant.parse("2026-06-10T15:00:00Z")),
                            rowOf(others, Instant.parse("2026-06-10T14:00:00Z"))
                    ));
            given(loadJobCategorySharedPort.findJobCategoryHierarchies(any())).willReturn(Map.of());
            given(loadProfileNicknameSharedPort.findNickname(anyLong())).willReturn(Optional.of("nick"));
            given(findInterestedPortfolioIdsPort.findInterestedPortfolioIds(any(), eq(viewerId))).willReturn(Set.of());

            // when
            LoadPublicPortfolioSummariesUseCase.Response response = sut.execute(null, 20, viewerId);

            // then
            assertThat(response.items()).hasSize(2);
            assertThat(response.items().get(0).isOwner()).isTrue();
            assertThat(response.items().get(1).isOwner()).isFalse();
        }

        @Test
        @DisplayName("비로그인 호출(viewerId=null) 이면 모든 포트폴리오의 isOwner 가 false")
        void should_return_is_owner_false_when_not_logged_in() {
            // given
            Portfolio anyPortfolio = portfolioOf(101L, null);
            given(loadPublicPortfolioSummaryPort
                    .findPublicPortfoliosOrderByEffectiveUpdatedAtDesc(any(), any(), anyInt()))
                    .willReturn(List.of(rowOf(anyPortfolio, Instant.parse("2026-06-10T15:00:00Z"))));
            given(loadJobCategorySharedPort.findJobCategoryHierarchies(any())).willReturn(Map.of());
            given(loadProfileNicknameSharedPort.findNickname(anyLong())).willReturn(Optional.of("nick"));

            // when
            LoadPublicPortfolioSummariesUseCase.Response response = sut.execute(null, 20, null);

            // then
            assertThat(response.items().getFirst().isOwner()).isFalse();
        }

        @Test
        @DisplayName("여러 포트폴리오의 leafId 들을 모아 직무 계층은 일괄 조회로 가져온다")
        void should_batch_load_job_category_hierarchies() {
            // given
            Portfolio p1 = portfolioOf(101L, null);
            Portfolio p2 = portfolioOf(102L, null);
            given(loadPublicPortfolioSummaryPort
                    .findPublicPortfoliosOrderByEffectiveUpdatedAtDesc(any(), any(), anyInt()))
                    .willReturn(List.of(
                            rowOf(p1, Instant.parse("2026-06-10T15:00:00Z")),
                            rowOf(p2, Instant.parse("2026-06-10T14:00:00Z"))
                    ));
            given(loadJobCategorySharedPort.findJobCategoryHierarchies(any()))
                    .willReturn(Map.of(JOB_CATEGORY_ID, List.of(
                            new LoadJobCategorySharedPort.Result(1001L, 1L, "DEV", "개발"),
                            new LoadJobCategorySharedPort.Result(JOB_CATEGORY_ID, 2L, "DEV_BACKEND", "백엔드")
                    )));
            given(loadProfileNicknameSharedPort.findNickname(anyLong())).willReturn(Optional.of("nick"));

            // when
            LoadPublicPortfolioSummariesUseCase.Response response = sut.execute(null, 20, null);

            // then — 단일 호출로 끝남
            then(loadJobCategorySharedPort).should().findJobCategoryHierarchies(any());
            then(loadJobCategorySharedPort).should(never()).findJobCategoryHierarchy(anyLong());
            assertThat(response.items()).allSatisfy(item ->
                    assertThat(item.jobCategories())
                            .extracting("categoryCode")
                            .containsExactly("DEV", "DEV_BACKEND")
            );
        }

        @Test
        @DisplayName("로그인 사용자가 관심 등록한 포트폴리오는 isInterested=true, 아닌 것은 false")
        void should_mark_is_interested_per_viewer() {
            // given
            Long viewerId = 500L;
            Portfolio interested = portfolioOf(101L, null);
            Portfolio notInterested = portfolioOf(202L, null);
            given(loadPublicPortfolioSummaryPort
                    .findPublicPortfoliosOrderByEffectiveUpdatedAtDesc(any(), any(), anyInt()))
                    .willReturn(List.of(
                            rowOf(interested, Instant.parse("2026-06-10T15:00:00Z")),
                            rowOf(notInterested, Instant.parse("2026-06-10T14:00:00Z"))
                    ));
            given(loadJobCategorySharedPort.findJobCategoryHierarchies(any())).willReturn(Map.of());
            given(loadProfileNicknameSharedPort.findNickname(anyLong())).willReturn(Optional.of("nick"));
            given(findInterestedPortfolioIdsPort.findInterestedPortfolioIds(any(), eq(viewerId)))
                    .willReturn(Set.of(interested.getId()));

            // when
            LoadPublicPortfolioSummariesUseCase.Response response = sut.execute(null, 20, viewerId);

            // then
            assertThat(response.items().get(0).isInterested()).isTrue();
            assertThat(response.items().get(1).isInterested()).isFalse();
        }

        @Test
        @DisplayName("본인 글(isOwner=true)은 isInterested 가 null 이다 (관심 여부 판단 대상 아님)")
        void should_set_is_interested_null_for_owner() {
            // given
            Long viewerId = 101L;
            Portfolio mine = portfolioOf(viewerId, null);
            given(loadPublicPortfolioSummaryPort
                    .findPublicPortfoliosOrderByEffectiveUpdatedAtDesc(any(), any(), anyInt()))
                    .willReturn(List.of(rowOf(mine, Instant.parse("2026-06-10T15:00:00Z"))));
            given(loadJobCategorySharedPort.findJobCategoryHierarchies(any())).willReturn(Map.of());
            given(loadProfileNicknameSharedPort.findNickname(anyLong())).willReturn(Optional.of("nick"));
            given(findInterestedPortfolioIdsPort.findInterestedPortfolioIds(any(), eq(viewerId)))
                    .willReturn(Set.of());

            // when
            LoadPublicPortfolioSummariesUseCase.Response response = sut.execute(null, 20, viewerId);

            // then
            assertThat(response.items().getFirst().isOwner()).isTrue();
            assertThat(response.items().getFirst().isInterested()).isNull();
        }

        @Test
        @DisplayName("비로그인(viewerId=null)이면 isInterested 는 null 이고 관심 배치 조회를 호출하지 않는다")
        void should_set_is_interested_null_when_not_logged_in() {
            // given
            Portfolio portfolio = portfolioOf(101L, null);
            given(loadPublicPortfolioSummaryPort
                    .findPublicPortfoliosOrderByEffectiveUpdatedAtDesc(any(), any(), anyInt()))
                    .willReturn(List.of(rowOf(portfolio, Instant.parse("2026-06-10T15:00:00Z"))));
            given(loadJobCategorySharedPort.findJobCategoryHierarchies(any())).willReturn(Map.of());
            given(loadProfileNicknameSharedPort.findNickname(anyLong())).willReturn(Optional.of("nick"));

            // when
            LoadPublicPortfolioSummariesUseCase.Response response = sut.execute(null, 20, null);

            // then
            assertThat(response.items().getFirst().isInterested()).isNull();
            then(findInterestedPortfolioIdsPort).should(never()).findInterestedPortfolioIds(any(), any());
        }
    }
}
