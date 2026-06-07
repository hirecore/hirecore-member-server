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
 * TSID 기반 64-bit Long ID 의 <strong>컬렉션</strong>(예: {@code List<Long>}, {@code Long[]}) 필드를
 * JSON 와이어 위에서 String 배열로 표현하기 위한 컴포지션 어노테이션입니다.
 *
 * <p>{@link TsidId} 가 단일 Long 필드용이라면, 본 어노테이션은 그 컬렉션 버전입니다. Jackson 의
 * {@code using} 은 컬렉션 자체를 deserializer 에 보내려 하므로, 원소 단위로 적용되는
 * {@code contentUsing} / {@code contentSerializer} 를 사용합니다.</p>
 *
 * <pre>{@code
 * public record Request(
 *         @TsidIds List<Long> contentImageIds
 * ) {}
 *
 * // 직렬화 결과: {"contentImageIds":["101","102"]}
 * // 역직렬화 입력: {"contentImageIds":["101","102"]} 또는 [101, 102]
 * }</pre>
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(contentUsing = ToStringSerializer.class)
@JsonDeserialize(contentUsing = TsidLongDeserializer.class)
public @interface TsidIds {}
