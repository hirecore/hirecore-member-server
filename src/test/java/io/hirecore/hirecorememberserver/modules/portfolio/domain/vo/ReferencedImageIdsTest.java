package io.hirecore.hirecorememberserver.modules.portfolio.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ReferencedImageIds 값 객체 단위 테스트")
class ReferencedImageIdsTest {

    @Nested
    @DisplayName("of()")
    class OfTest {

        @Test
        @DisplayName("썸네일과 본문 이미지를 순서 보존하여 모은다")
        void should_collect_in_order() {
            assertThat(ReferencedImageIds.of(100L, List.of(10L, 20L)).values())
                    .containsExactly(100L, 10L, 20L);
        }

        @Test
        @DisplayName("썸네일과 본문에 동일 ID 가 있으면 한 번만 포함한다")
        void should_deduplicate() {
            assertThat(ReferencedImageIds.of(100L, List.of(100L, 200L, 200L)).values())
                    .containsExactly(100L, 200L);
        }

        @Test
        @DisplayName("썸네일이 null 이면 본문 이미지만 모은다")
        void should_handle_null_thumbnail() {
            assertThat(ReferencedImageIds.of(null, List.of(10L, 20L)).values())
                    .containsExactly(10L, 20L);
        }

        @Test
        @DisplayName("썸네일과 본문이 모두 없으면 빈 집합이다")
        void should_be_empty_when_no_images() {
            ReferencedImageIds refs = ReferencedImageIds.of(null, null);

            assertThat(refs.values()).isEmpty();
            assertThat(refs.isEmpty()).isTrue();
        }
    }

    @Nested
    @DisplayName("minus()")
    class MinusTest {

        @Test
        @DisplayName("새 집합에 없는(더 이상 참조되지 않는) 이미지만 순서 보존하여 반환한다")
        void should_return_released_ids() {
            ReferencedImageIds oldRefs = ReferencedImageIds.of(100L, List.of(10L, 20L, 30L));
            ReferencedImageIds newRefs = ReferencedImageIds.of(100L, List.of(10L, 30L));

            assertThat(oldRefs.minus(newRefs)).containsExactly(20L);
        }

        @Test
        @DisplayName("썸네일과 본문이 함께 빠지면 모두 반환한다")
        void should_return_both_thumbnail_and_content_removals() {
            ReferencedImageIds oldRefs = ReferencedImageIds.of(100L, List.of(10L, 20L));
            ReferencedImageIds newRefs = ReferencedImageIds.of(200L, List.of(30L));

            assertThat(oldRefs.minus(newRefs)).containsExactly(100L, 10L, 20L);
        }

        @Test
        @DisplayName("빠진 게 없으면 빈 리스트를 반환한다")
        void should_return_empty_when_nothing_released() {
            ReferencedImageIds oldRefs = ReferencedImageIds.of(100L, List.of(10L));
            ReferencedImageIds newRefs = ReferencedImageIds.of(100L, List.of(10L, 20L));

            assertThat(oldRefs.minus(newRefs)).isEmpty();
        }
    }
}
