package io.hirecore.hirecorememberserver.modules.portfolio.domain;

import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.vo.PortfolioStatus;
import io.hirecore.hirecorememberserver.sharedkernel.domain.event.PortfolioImagesUnlinkedEvent;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ExternalLink;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Portfolio 도메인 단위 테스트")
class PortfolioTest {

    private static Portfolio createValid(String previewSummary, List<PortfolioTag> tags, List<ExternalLink> externalLinks) {
        return createValid("차세대 취업 사이트 개발 프로젝트", previewSummary, null, tags, externalLinks);
    }

    private static Portfolio createValid(
            String title,
            String previewSummary,
            String privateMemo,
            List<PortfolioTag> tags,
            List<ExternalLink> externalLinks
    ) {
        return Portfolio.create(
                1L,
                100L,
                null,
                null,
                title,
                previewSummary,
                privateMemo,
                10L,
                null,
                "{\"type\":\"doc\"}",
                "<p>본문</p>",
                List.of(),
                externalLinks,
                tags,
                CollaborationType.TEAM,
                Visibility.PUBLIC
        );
    }

    private static Portfolio createWithImages(Long thumbnailImageId, List<Long> contentImageIds) {
        return Portfolio.create(
                1L,
                thumbnailImageId,
                null,
                null,
                "title",
                "본문 미리보기",
                null,
                10L,
                null,
                "{\"type\":\"doc\"}",
                "<p>본문</p>",
                contentImageIds,
                List.of(),
                List.of(),
                CollaborationType.TEAM,
                Visibility.PUBLIC
        );
    }

    private static void modifyWithImages(Portfolio portfolio, Long thumbnailImageId, List<Long> contentImageIds) {
        portfolio.modify(
                1L,
                thumbnailImageId,
                null,
                null,
                "title",
                "본문 미리보기",
                null,
                10L,
                null,
                "{\"type\":\"doc\"}",
                "<p>본문</p>",
                contentImageIds,
                List.of(),
                List.of(),
                CollaborationType.TEAM,
                Visibility.PUBLIC
        );
    }

    @Nested
    @DisplayName("create() 팩토리 메서드")
    class CreateTest {

        @Test
        @DisplayName("유효한 입력으로 PUBLISHED 상태의 Portfolio를 생성한다")
        void should_create_portfolio_with_published_status() {
            Portfolio portfolio = createValid(
                    "본문 미리보기",
                    List.of(PortfolioTag.create("풀스택", 0), PortfolioTag.create("팀", 1)),
                    List.of()
            );

            assertThat(portfolio.getId()).isNotNull();
            assertThat(portfolio.getStatus()).isEqualTo(PortfolioStatus.PUBLISHED);
            assertThat(portfolio.getPortfolioJobCategory()).isNotNull();
            assertThat(portfolio.getPortfolioContent().getPortfolioId()).isEqualTo(portfolio.getId());
            assertThat(portfolio.getPortfolioTags()).hasSize(2);
            assertThat(portfolio.getAuditingInfo()).isNotNull();
            assertThat(portfolio.getPreviewSummary()).isEqualTo("본문 미리보기");
        }

        @Test
        @DisplayName("외부에서 주입한 portfolioTags를 그대로 보유한다")
        void should_hold_provided_tags() {
            List<PortfolioTag> tags = List.of(PortfolioTag.create("백엔드", 0));

            Portfolio portfolio = createValid("본문", tags, List.of());

            assertThat(portfolio.getPortfolioTags()).isSameAs(tags);
        }

        @Test
        @DisplayName("외부에서 주입한 externalLinks를 그대로 보유한다")
        void should_hold_provided_external_links() {
            List<ExternalLink> links = List.of(new ExternalLink("Repo", "https://github.com/example"));

            Portfolio portfolio = createValid("본문", List.of(), links);

            assertThat(portfolio.getExternalLinks()).isSameAs(links);
        }
    }

    @Nested
    @DisplayName("modify() 시 이미지 회수 이벤트 발행")
    class ModifyImagesUnlinkedEventTest {

        @Test
        @DisplayName("이미지를 추가만 하고 빠진 게 없으면 PortfolioImagesUnlinkedEvent 가 발행되지 않는다")
        void should_not_emit_event_when_only_added() {
            Portfolio portfolio = createWithImages(100L, List.of(10L, 20L));
            portfolio.pollAllEvents();

            modifyWithImages(portfolio, 100L, List.of(10L, 20L, 30L));

            assertThat(filterUnlinkedEvents(portfolio.pollAllEvents())).isEmpty();
        }

        @Test
        @DisplayName("이미지 변동이 전혀 없으면 PortfolioImagesUnlinkedEvent 가 발행되지 않는다")
        void should_not_emit_event_when_no_image_change() {
            Portfolio portfolio = createWithImages(100L, List.of(10L, 20L));
            portfolio.pollAllEvents();

            modifyWithImages(portfolio, 100L, List.of(10L, 20L));

            assertThat(filterUnlinkedEvents(portfolio.pollAllEvents())).isEmpty();
        }

        @Test
        @DisplayName("본문 이미지 일부가 빠지면 빠진 ID 만 담은 PortfolioImagesUnlinkedEvent 가 발행된다")
        void should_emit_event_with_removed_content_image_ids() {
            Portfolio portfolio = createWithImages(100L, List.of(10L, 20L, 30L));
            portfolio.pollAllEvents();

            modifyWithImages(portfolio, 100L, List.of(10L, 30L));

            List<PortfolioImagesUnlinkedEvent> events = filterUnlinkedEvents(portfolio.pollAllEvents());
            assertThat(events).hasSize(1);
            assertThat(events.get(0).portfolioId()).isEqualTo(portfolio.getId());
            assertThat(events.get(0).memberAccountId()).isEqualTo(portfolio.getMemberAccountId());
            assertThat(events.get(0).imageFileMetaIds()).containsExactly(20L);
        }

        @Test
        @DisplayName("썸네일이 교체되면 이전 thumbnailImageId 가 PortfolioImagesUnlinkedEvent 에 포함된다")
        void should_emit_event_with_old_thumbnail_when_replaced() {
            Portfolio portfolio = createWithImages(100L, List.of(10L, 20L));
            portfolio.pollAllEvents();

            modifyWithImages(portfolio, 101L, List.of(10L, 20L));

            List<PortfolioImagesUnlinkedEvent> events = filterUnlinkedEvents(portfolio.pollAllEvents());
            assertThat(events).hasSize(1);
            assertThat(events.get(0).imageFileMetaIds()).containsExactly(100L);
        }

        @Test
        @DisplayName("썸네일이 제거되면(null로 교체) 이전 thumbnailImageId 가 PortfolioImagesUnlinkedEvent 에 포함된다")
        void should_emit_event_with_old_thumbnail_when_removed() {
            Portfolio portfolio = createWithImages(100L, List.of());
            portfolio.pollAllEvents();

            modifyWithImages(portfolio, null, List.of());

            List<PortfolioImagesUnlinkedEvent> events = filterUnlinkedEvents(portfolio.pollAllEvents());
            assertThat(events).hasSize(1);
            assertThat(events.get(0).imageFileMetaIds()).containsExactly(100L);
        }

        @Test
        @DisplayName("본문 이미지가 교체되면 빠진 본문 ID 가 PortfolioImagesUnlinkedEvent 에 포함된다 (수정 = 사실상 삭제)")
        void should_emit_event_with_swapped_content_image() {
            Portfolio portfolio = createWithImages(100L, List.of(10L, 20L));
            portfolio.pollAllEvents();

            modifyWithImages(portfolio, 100L, List.of(10L, 30L));

            List<PortfolioImagesUnlinkedEvent> events = filterUnlinkedEvents(portfolio.pollAllEvents());
            assertThat(events).hasSize(1);
            assertThat(events.get(0).imageFileMetaIds()).containsExactly(20L);
        }

        @Test
        @DisplayName("썸네일과 본문 이미지가 동시에 빠지면 모두 한 이벤트의 imageFileMetaIds 에 합쳐서 담긴다")
        void should_emit_event_with_both_thumbnail_and_content_removals() {
            Portfolio portfolio = createWithImages(100L, List.of(10L, 20L));
            portfolio.pollAllEvents();

            modifyWithImages(portfolio, 200L, List.of(30L));

            List<PortfolioImagesUnlinkedEvent> events = filterUnlinkedEvents(portfolio.pollAllEvents());
            assertThat(events).hasSize(1);
            assertThat(events.get(0).imageFileMetaIds()).containsExactlyInAnyOrder(100L, 10L, 20L);
        }

        @SuppressWarnings("unchecked")
        private static List<PortfolioImagesUnlinkedEvent> filterUnlinkedEvents(Collection<Object> events) {
            return events.stream()
                    .filter(PortfolioImagesUnlinkedEvent.class::isInstance)
                    .map(e -> (PortfolioImagesUnlinkedEvent) e)
                    .toList();
        }
    }

    @Nested
    @DisplayName("modify() 시 직무카테고리 처리")
    class ModifyJobCategoryTest {

        private static void modifyJobCategory(Portfolio portfolio, Long jobCategoryId, String userInput) {
            portfolio.modify(
                    1L,
                    100L,
                    null,
                    null,
                    "title",
                    "본문 미리보기",
                    null,
                    jobCategoryId,
                    userInput,
                    "{\"type\":\"doc\"}",
                    "<p>본문</p>",
                    List.of(),
                    List.of(),
                    List.of(),
                    CollaborationType.TEAM,
                    Visibility.PUBLIC
            );
        }

        @Test
        @DisplayName("수정해도 직무카테고리 식별자(portfolioId)는 포트폴리오 ID로 유지된다")
        void should_keep_job_category_identity_as_portfolio_id() {
            Portfolio portfolio = createWithImages(100L, List.of());

            modifyJobCategory(portfolio, 20L, "백엔드");

            assertThat(portfolio.getPortfolioJobCategory().getPortfolioId()).isEqualTo(portfolio.getId());
        }

        @Test
        @DisplayName("직무카테고리가 그대로면 connectedAt 을 보존한다")
        void should_keep_connected_at_when_job_category_unchanged() {
            Portfolio portfolio = createWithImages(100L, List.of());
            Instant before = portfolio.getPortfolioJobCategory().getConnectedAt();

            modifyJobCategory(portfolio, 10L, null);

            assertThat(portfolio.getPortfolioJobCategory().getConnectedAt()).isEqualTo(before);
        }

        @Test
        @DisplayName("직무카테고리가 바뀌면 새 jobCategoryId/userInput 을 반영한다")
        void should_apply_changed_job_category() {
            Portfolio portfolio = createWithImages(100L, List.of());

            modifyJobCategory(portfolio, 20L, "프론트엔드");

            assertThat(portfolio.getPortfolioJobCategory().getLeafJobCategoryId()).isEqualTo(20L);
            assertThat(portfolio.getPortfolioJobCategory().getUserInput()).isEqualTo("프론트엔드");
        }
    }

    @Nested
    @DisplayName("delete() 영구 삭제")
    class DeleteTest {

        private static final Long OWNER_ID = 1L;
        private static final Long OTHER_USER_ID = 2L;

        @Test
        @DisplayName("작성자 본인이 호출하고 참조 이미지가 있으면 PortfolioImagesUnlinkedEvent 가 발행된다")
        void should_emit_event_with_all_referenced_image_ids_when_owner() {
            Portfolio portfolio = createWithImages(100L, List.of(10L, 20L));
            portfolio.pollAllEvents();

            portfolio.delete(OWNER_ID);

            List<PortfolioImagesUnlinkedEvent> events = filterUnlinkedEvents(portfolio.pollAllEvents());
            assertThat(events).hasSize(1);
            assertThat(events.get(0).imageFileMetaIds()).containsExactlyInAnyOrder(100L, 10L, 20L);
            assertThat(events.get(0).memberAccountId()).isEqualTo(OWNER_ID);
        }

        @Test
        @DisplayName("썸네일/본문 모두 비어 있으면 PortfolioImagesUnlinkedEvent 가 발행되지 않는다 (비공집합 invariant 보호)")
        void should_not_emit_event_when_no_referenced_images() {
            Portfolio portfolio = createWithImages(null, List.of());
            portfolio.pollAllEvents();

            portfolio.delete(OWNER_ID);

            List<PortfolioImagesUnlinkedEvent> events = filterUnlinkedEvents(portfolio.pollAllEvents());
            assertThat(events).isEmpty();
        }

        @Test
        @DisplayName("썸네일과 본문에 동일 imageId 가 중복으로 등장해도 한 번만 PortfolioImagesUnlinkedEvent 에 포함된다")
        void should_deduplicate_image_ids_in_event() {
            Portfolio portfolio = createWithImages(100L, List.of(100L, 200L));
            portfolio.pollAllEvents();

            portfolio.delete(OWNER_ID);

            List<PortfolioImagesUnlinkedEvent> events = filterUnlinkedEvents(portfolio.pollAllEvents());
            assertThat(events).hasSize(1);
            assertThat(events.get(0).imageFileMetaIds()).containsExactlyInAnyOrder(100L, 200L);
        }

        @Test
        @DisplayName("비소유자가 호출하면 PORTFOLIO_FORBIDDEN 도메인 예외를 던지고 이벤트는 발행되지 않는다")
        void should_throw_when_non_owner() {
            Portfolio portfolio = createWithImages(100L, List.of(10L));
            portfolio.pollAllEvents();

            assertThatThrownBy(() -> portfolio.delete(OTHER_USER_ID))
                    .isInstanceOf(PortfolioDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioDomainExceptionCodeCluster.DetailResponse.PORTFOLIO_DELETE_DENIED.getErrorCode());

            List<PortfolioImagesUnlinkedEvent> events = filterUnlinkedEvents(portfolio.pollAllEvents());
            assertThat(events).isEmpty();
        }

        @SuppressWarnings("unchecked")
        private static List<PortfolioImagesUnlinkedEvent> filterUnlinkedEvents(Collection<Object> events) {
            return events.stream()
                    .filter(PortfolioImagesUnlinkedEvent.class::isInstance)
                    .map(e -> (PortfolioImagesUnlinkedEvent) e)
                    .toList();
        }
    }

    @Nested
    @DisplayName("invariant 검증")
    class InvariantTest {

        @Test
        @DisplayName("previewSummary가 PREVIEW_SUMMARY_MAX_LENGTH를 초과하면 PREVIEW_SUMMARY_TOO_LONG 예외가 발생한다")
        void should_throw_when_preview_summary_exceeds_max_length() {
            String overflow = "가".repeat(Portfolio.PREVIEW_SUMMARY_MAX_LENGTH + 1);

            assertThatThrownBy(() -> createValid(overflow, List.of(), List.of()))
                    .isInstanceOf(PortfolioDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.PREVIEW_SUMMARY_TOO_LONG.getErrorCode());
        }

        @Test
        @DisplayName("previewSummary가 비어있으면 PREVIEW_SUMMARY_MISSING 예외가 발생한다")
        void should_throw_when_preview_summary_is_blank() {
            assertThatThrownBy(() -> createValid("   ", List.of(), List.of()))
                    .isInstanceOf(PortfolioDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.PREVIEW_SUMMARY_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("title이 TITLE_MAX_LENGTH를 초과하면 TITLE_TOO_LONG 예외가 발생한다")
        void should_throw_when_title_exceeds_max_length() {
            String overflowTitle = "가".repeat(Portfolio.TITLE_MAX_LENGTH + 1);

            assertThatThrownBy(() -> createValid(overflowTitle, "본문 미리보기", null, List.of(), List.of()))
                    .isInstanceOf(PortfolioDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.TITLE_TOO_LONG.getErrorCode());
        }

        @Test
        @DisplayName("privateMemo가 PRIVATE_MEMO_MAX_LENGTH를 초과하면 PRIVATE_MEMO_TOO_LONG 예외가 발생한다")
        void should_throw_when_private_memo_exceeds_max_length() {
            String overflowMemo = "가".repeat(Portfolio.PRIVATE_MEMO_MAX_LENGTH + 1);

            assertThatThrownBy(() -> createValid("제목", "본문 미리보기", overflowMemo, List.of(), List.of()))
                    .isInstanceOf(PortfolioDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.PRIVATE_MEMO_TOO_LONG.getErrorCode());
        }

        @Test
        @DisplayName("externalLinks 개수가 EXTERNAL_LINKS_MAX_COUNT를 초과하면 EXTERNAL_LINKS_TOO_MANY 예외가 발생한다")
        void should_throw_when_external_links_exceed_max_count() {
            List<ExternalLink> overflowLinks = java.util.stream.IntStream.range(0, Portfolio.EXTERNAL_LINKS_MAX_COUNT + 1)
                    .mapToObj(i -> new ExternalLink("Repo" + i, "https://example.com/" + i))
                    .toList();

            assertThatThrownBy(() -> createValid("본문 미리보기", List.of(), overflowLinks))
                    .isInstanceOf(PortfolioDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.EXTERNAL_LINKS_TOO_MANY.getErrorCode());
        }

        @Test
        @DisplayName("portfolioTags 개수가 PORTFOLIO_TAGS_MAX_COUNT를 초과하면 PORTFOLIO_TAGS_TOO_MANY 예외가 발생한다")
        void should_throw_when_portfolio_tags_exceed_max_count() {
            List<PortfolioTag> overflowTags = java.util.stream.IntStream.range(0, Portfolio.PORTFOLIO_TAGS_MAX_COUNT + 1)
                    .mapToObj(i -> PortfolioTag.create("태그" + i, i))
                    .toList();

            assertThatThrownBy(() -> createValid("본문 미리보기", overflowTags, List.of()))
                    .isInstanceOf(PortfolioDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.PORTFOLIO_TAGS_TOO_MANY.getErrorCode());
        }
    }
}
