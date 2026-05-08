package io.hirecore.hirecorememberserver.common.web.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("@TsidId 어노테이션 통합 테스트")
class TsidIdTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    record TsidIdHolder(@TsidId Long id) {}

    record TsidIdListHolder(
            @TsidId Long id,
            @TsidIds List<Long> ids
    ) {}

    @Nested
    @DisplayName("직렬화 (Long → JSON)")
    class SerializeTest {

        @Test
        @DisplayName("19자리 TSID Long 값을 JSON String 으로 출력한다")
        void should_serialize_19_digit_long_as_string() throws Exception {
            TsidIdHolder holder = new TsidIdHolder(1234567890123456789L);

            String json = objectMapper.writeValueAsString(holder);

            assertThat(json).isEqualTo("{\"id\":\"1234567890123456789\"}");
        }

        @Test
        @DisplayName("작은 Long 값도 JSON String 으로 일관되게 출력한다")
        void should_serialize_small_long_as_string() throws Exception {
            TsidIdHolder holder = new TsidIdHolder(100L);

            String json = objectMapper.writeValueAsString(holder);

            assertThat(json).isEqualTo("{\"id\":\"100\"}");
        }

        @Test
        @DisplayName("null 값은 JSON null 로 출력한다")
        void should_serialize_null_as_json_null() throws Exception {
            TsidIdHolder holder = new TsidIdHolder(null);

            String json = objectMapper.writeValueAsString(holder);

            assertThat(json).isEqualTo("{\"id\":null}");
        }
    }

    @Nested
    @DisplayName("역직렬화 (JSON → Long)")
    class DeserializeTest {

        @Test
        @DisplayName("JSON String 입력을 Long 으로 변환한다")
        void should_deserialize_string_to_long() throws Exception {
            String json = "{\"id\":\"1234567890123456789\"}";

            TsidIdHolder holder = objectMapper.readValue(json, TsidIdHolder.class);

            assertThat(holder.id()).isEqualTo(1234567890123456789L);
        }

        @Test
        @DisplayName("JSON Number 입력도 호환성 차원에서 Long 으로 변환한다")
        void should_deserialize_number_to_long() throws Exception {
            String json = "{\"id\":100}";

            TsidIdHolder holder = objectMapper.readValue(json, TsidIdHolder.class);

            assertThat(holder.id()).isEqualTo(100L);
        }

        @Test
        @DisplayName("JSON null 입력은 null 로 처리한다")
        void should_deserialize_json_null_to_null() throws Exception {
            String json = "{\"id\":null}";

            TsidIdHolder holder = objectMapper.readValue(json, TsidIdHolder.class);

            assertThat(holder.id()).isNull();
        }

        @Test
        @DisplayName("빈 문자열은 null 로 처리한다")
        void should_treat_blank_string_as_null() throws Exception {
            String json = "{\"id\":\"\"}";

            TsidIdHolder holder = objectMapper.readValue(json, TsidIdHolder.class);

            assertThat(holder.id()).isNull();
        }

        @Test
        @DisplayName("문자열 앞뒤 공백을 trim 한 뒤 변환한다")
        void should_trim_whitespace_before_parsing() throws Exception {
            String json = "{\"id\":\"  100  \"}";

            TsidIdHolder holder = objectMapper.readValue(json, TsidIdHolder.class);

            assertThat(holder.id()).isEqualTo(100L);
        }

        @Test
        @DisplayName("유효하지 않은 문자열 형식은 예외를 발생시킨다")
        void should_throw_for_invalid_string_format() {
            String json = "{\"id\":\"not-a-number\"}";

            assertThatThrownBy(() -> objectMapper.readValue(json, TsidIdHolder.class))
                    .isInstanceOf(com.fasterxml.jackson.databind.exc.InvalidFormatException.class);
        }

        @Test
        @DisplayName("List<Long> 의 각 원소도 String / Number 모두 수용한다")
        void should_deserialize_list_elements() throws Exception {
            String json = "{\"id\":\"100\",\"ids\":[\"1234567890123456789\",100]}";

            TsidIdListHolder holder = objectMapper.readValue(json, TsidIdListHolder.class);

            assertThat(holder.ids()).containsExactly(1234567890123456789L, 100L);
        }
    }

    @Nested
    @DisplayName("Round-trip (직렬화 → 역직렬화)")
    class RoundTripTest {

        @Test
        @DisplayName("19자리 Long 값을 직렬화 후 역직렬화하면 동일한 값이 보존된다 (정밀도 손실 없음)")
        void should_preserve_long_value_through_round_trip() throws Exception {
            TsidIdHolder original = new TsidIdHolder(1234567890123456789L);

            String json = objectMapper.writeValueAsString(original);
            TsidIdHolder restored = objectMapper.readValue(json, TsidIdHolder.class);

            assertThat(restored.id()).isEqualTo(original.id());
        }
    }
}
