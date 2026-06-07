package io.hirecore.hirecorememberserver.common.web.json;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TSID 기반 64-bit Long ID 필드를 JSON 와이어 위에서 String 으로 표현하기 위한 컴포지션 어노테이션입니다.
 *
 * <p>JavaScript 의 {@code Number.MAX_SAFE_INTEGER} 한도(2^53 - 1, 약 16자리)를 초과하는 TSID(보통 19자리) 값을
 * JSON number 로 직렬화하면 클라이언트의 {@code JSON.parse} 단계에서 정밀도 손실이 발생합니다.
 * 본 어노테이션을 부착한 필드는 직렬화 시 String 으로 출력되고, 역직렬화 시 String/Number 양쪽을 모두 수용합니다.</p>
 *
 * <p>동작 원리: {@link JacksonAnnotationsInside} 메타 어노테이션이 부착되어 있어, Jackson 이
 * 본 어노테이션을 만나면 내부의 {@link JsonSerialize} / {@link JsonDeserialize} 어노테이션을
 * 마치 직접 부착된 것처럼 펼쳐서 적용합니다.</p>
 *
 * <pre>{@code
 * public record Response(
 *         @TsidId Long portfolioId
 * ) {}
 *
 * // 직렬화 결과: {"portfolioId":"9876543210987654321"}
 * }</pre>
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = ToStringSerializer.class)
@JsonDeserialize(using = TsidLongDeserializer.class)
public @interface TsidId {}
