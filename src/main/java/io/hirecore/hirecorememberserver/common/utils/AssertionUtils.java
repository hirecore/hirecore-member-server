package io.hirecore.hirecorememberserver.common.utils;

import java.time.Instant;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * 도메인 불변성(Invariants) 및 데이터 유효성 검증을 위한 공통 유틸리티 클래스입니다.
 * 검증 실패 시 전달받은 함수형 인터페이스를 통해 특화된 예외를 발생시킵니다.
 */
public class AssertionUtils {

    /**
     * 대상 객체가 null이 아님을 보장합니다.
     *
     * @param <C> 에러 코드의 타입 (DomainExceptionCode, ApplicationExceptionCode 등)
     * @param <E> 발생시킬 예외의 타입
     */
    public static <C, T, E extends RuntimeException> void notNull(
            T object,
            C errorCode,
            Function<C, E> exceptionFunction) {

        if (object == null) {
            throw exceptionFunction.apply(errorCode);
        }
    }

    /**
     * 대상 문자열이 null이거나 공백(blank)이 아님을 보장합니다.
     */
    public static <C, E extends RuntimeException> void notBlank(
            String str,
            C errorCode,
            Function<C, E> exceptionFunction) {

        if (str == null || str.isBlank()) {
            throw exceptionFunction.apply(errorCode);
        }
    }

    /**
     * 두 객체가 논리적으로 동등한지(equals) 보장합니다.
     */
    public static <C, T, E extends RuntimeException> void isEqual(
            T actual,
            T expected,
            C errorCode,
            Function<C, E> exceptionFunction) {

        if (!Objects.equals(actual, expected)) {
            throw exceptionFunction.apply(errorCode);
        }
    }

    /**
     * 주어진 조건식이 참(true)임을 보장합니다.
     */
    public static <C, E extends RuntimeException> void isTrue(
            boolean expression,
            C errorCode,
            Function<C, E> exceptionFunction) {

        if (!expression) {
            throw exceptionFunction.apply(errorCode);
        }
    }

    /**
     * 문자열이 지정된 Enum 클래스의 유효한 상수임을 보장합니다. (대소문자 무관)
     */
    public static <C, EnumT extends Enum<EnumT>, E extends RuntimeException> void isValidEnum(
            Class<EnumT> enumClass,
            String value,
            C errorCode,
            Function<C, E> exceptionFunction) {

        if (value == null || value.isBlank()) {
            throw exceptionFunction.apply(errorCode);
        }

        try {
            Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw exceptionFunction.apply(errorCode);
        }
    }

    /**
     * 대상 문자열이 주어진 정규표현식 패턴과 일치함을 보장합니다.
     */
    public static <C, E extends RuntimeException> void matches(
            String str,
            String regex,
            C errorCode,
            Function<C, E> exceptionFunction) {

        if (str == null || !Pattern.matches(regex, str)) {
            throw exceptionFunction.apply(errorCode);
        }
    }

    /**
     * 지정된 시간(Instant)이 미래가 아님(과거 또는 현재)을 보장합니다.
     */
    public static <C, E extends RuntimeException> void isPastOrPresent(
            Instant time,
            C errorCode,
            Function<C, E> exceptionFunction) {

        if (time == null || time.isAfter(Instant.now())) {
            throw exceptionFunction.apply(errorCode);
        }
    }
}
