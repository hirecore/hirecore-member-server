package io.hirecore.hirecorememberserver.common.web.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * {@link TsidId} 가 부착된 Long 필드의 역직렬화를 담당합니다.
 *
 * <p>JSON String / Number / null 입력을 모두 안전하게 Long 으로 변환합니다.
 * 프로젝트의 {@code StrictJsonConfig} 가 {@code ALLOW_COERCION_OF_SCALARS=false} 로
 * 자동 변환을 차단하고 있으므로, ID 필드만은 본 deserializer 로 명시 처리합니다.</p>
 *
 * <ul>
 *     <li>{@code "9876543210987654321"} (JSON String) → {@code 9876543210987654321L}</li>
 *     <li>{@code 100} (JSON Number) → {@code 100L} — 작은 값(seed ID 등) 호환성 유지</li>
 *     <li>{@code null} → {@code null}</li>
 *     <li>그 외 형식 → {@link com.fasterxml.jackson.databind.JsonMappingException}</li>
 * </ul>
 */
public class TsidLongDeserializer extends JsonDeserializer<Long> {

    @Override
    public Long deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonToken token = parser.currentToken();

        if (token == JsonToken.VALUE_NULL) {
            return null;
        }

        if (token == JsonToken.VALUE_STRING) {
            String text = parser.getText();
            if (text == null || text.isBlank()) {
                return null;
            }
            try {
                return Long.parseLong(text.trim());
            } catch (NumberFormatException e) {
                return (Long) context.handleWeirdStringValue(
                        Long.class,
                        text,
                        "유효한 Long 형식이 아닙니다."
                );
            }
        }

        if (token == JsonToken.VALUE_NUMBER_INT) {
            return parser.getLongValue();
        }

        return (Long) context.handleUnexpectedToken(Long.class, parser);
    }

    @Override
    public Long getNullValue(DeserializationContext context) {
        return null;
    }
}
