package io.hirecore.hirecorememberserver.sharedkernel.application.cursor;

import io.hirecore.hirecorememberserver.sharedkernel.application.exception.SharedKernelApplicationException;
import io.hirecore.hirecorememberserver.sharedkernel.application.exception.SharedKernelApplicationExceptionCodeCluster;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("EffectiveTimeCursor 단위 테스트")
class EffectiveTimeCursorTest {

    @Nested
    @DisplayName("인코드 / 디코드 round-trip")
    class RoundTripTest {

        @Test
        @DisplayName("인코드 후 디코드하면 (time, id) 가 동일하게 복원된다")
        void should_round_trip_basic() {
            // given
            Instant original = Instant.parse("2026-06-10T15:30:45.123456Z");
            Long id = 5234567890123456789L;
            EffectiveTimeCursor source = new EffectiveTimeCursor(original, id);

            // when
            String token = source.encode();
            EffectiveTimeCursor decoded = EffectiveTimeCursor.decode(token);

            // then
            assertThat(decoded.time()).isEqualTo(original);
            assertThat(decoded.id()).isEqualTo(id);
        }

        @Test
        @DisplayName("마이크로초 단위가 손실 없이 복원된다 (DATETIME(6) 정밀도 보존)")
        void should_preserve_microsecond_precision() {
            // given
            Instant withMicros = Instant.parse("2026-06-10T15:30:45.654321Z");
            EffectiveTimeCursor source = new EffectiveTimeCursor(withMicros, 1L);

            // when
            EffectiveTimeCursor decoded = EffectiveTimeCursor.decode(source.encode());

            // then
            assertThat(decoded.time().getEpochSecond()).isEqualTo(withMicros.getEpochSecond());
            assertThat(decoded.time().getNano()).isEqualTo(withMicros.getNano());
        }

        @Test
        @DisplayName("epoch 시각도 안전하게 round-trip 된다")
        void should_round_trip_epoch() {
            // given
            EffectiveTimeCursor source = new EffectiveTimeCursor(Instant.EPOCH, 0L);

            // when
            EffectiveTimeCursor decoded = EffectiveTimeCursor.decode(source.encode());

            // then
            assertThat(decoded.time()).isEqualTo(Instant.EPOCH);
            assertThat(decoded.id()).isEqualTo(0L);
        }

        @Test
        @DisplayName("매우 큰 TSID 도 손실 없이 round-trip 된다")
        void should_round_trip_large_tsid() {
            // given
            Long largeTsid = Long.MAX_VALUE;
            EffectiveTimeCursor source = new EffectiveTimeCursor(
                    Instant.parse("2026-06-10T15:00:00Z"),
                    largeTsid
            );

            // when
            EffectiveTimeCursor decoded = EffectiveTimeCursor.decode(source.encode());

            // then
            assertThat(decoded.id()).isEqualTo(largeTsid);
        }
    }

    @Nested
    @DisplayName("토큰 포맷")
    class TokenFormatTest {

        @Test
        @DisplayName("토큰은 URL-safe Base64 (padding 없음) 형식이다")
        void should_be_url_safe_base64_without_padding() {
            // given
            EffectiveTimeCursor source = new EffectiveTimeCursor(
                    Instant.parse("2026-06-10T15:00:00Z"),
                    5234567890123456789L
            );

            // when
            String token = source.encode();

            // then — URL-safe(+=>−, /=>_), padding 없음
            assertThat(token).doesNotContain("+", "/", "=");
        }
    }

    @Nested
    @DisplayName("잘못된 토큰 처리")
    class InvalidTokenTest {

        @Test
        @DisplayName("Base64 디코딩 자체가 실패하는 토큰은 INFINITE_SCROLL_CURSOR_INVALID 를 던진다")
        void should_throw_when_not_base64() {
            // given
            String notBase64 = "this is not base64 at all!!!";

            // when & then
            assertThatThrownBy(() -> EffectiveTimeCursor.decode(notBase64))
                    .isInstanceOf(SharedKernelApplicationException.class)
                    .extracting("applicationExceptionCode")
                    .isEqualTo(SharedKernelApplicationExceptionCodeCluster
                            .DetailResponse.INFINITE_SCROLL_CURSOR_INVALID);
        }

        @Test
        @DisplayName("delimiter(_) 가 없는 평문 토큰은 invalid 예외를 던진다")
        void should_throw_when_no_delimiter() {
            // given
            String noDelimiter = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString("nodelimiter".getBytes());

            // when & then
            assertThatThrownBy(() -> EffectiveTimeCursor.decode(noDelimiter))
                    .isInstanceOf(SharedKernelApplicationException.class);
        }

        @Test
        @DisplayName("delimiter 이후 id 영역이 숫자가 아니면 invalid 예외를 던진다")
        void should_throw_when_id_not_numeric() {
            // given
            String malformed = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString("12345_notanumber".getBytes());

            // when & then
            assertThatThrownBy(() -> EffectiveTimeCursor.decode(malformed))
                    .isInstanceOf(SharedKernelApplicationException.class);
        }

        @Test
        @DisplayName("delimiter 가 맨 앞에 위치하면 invalid 예외를 던진다 (time 영역 빈 케이스)")
        void should_throw_when_delimiter_at_start() {
            // given
            String leadingDelimiter = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString("_12345".getBytes());

            // when & then
            assertThatThrownBy(() -> EffectiveTimeCursor.decode(leadingDelimiter))
                    .isInstanceOf(SharedKernelApplicationException.class);
        }

        @Test
        @DisplayName("delimiter 가 맨 끝에 위치하면 invalid 예외를 던진다 (id 영역 빈 케이스)")
        void should_throw_when_delimiter_at_end() {
            // given
            String trailingDelimiter = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString("12345_".getBytes());

            // when & then
            assertThatThrownBy(() -> EffectiveTimeCursor.decode(trailingDelimiter))
                    .isInstanceOf(SharedKernelApplicationException.class);
        }
    }
}
