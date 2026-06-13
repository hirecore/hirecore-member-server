package io.hirecore.hirecorememberserver.sharedkernel.application.cursor;

import io.hirecore.hirecorememberserver.sharedkernel.application.exception.SharedKernelApplicationException;
import io.hirecore.hirecorememberserver.sharedkernel.application.exception.SharedKernelApplicationExceptionCodeCluster;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

/**
 * 시간 기반 정렬 페이지네이션을 위한 opaque 커서 토큰.
 *
 * <p>구조: {@code (Instant time, Long id)} 두 값을 {@code "{micros}_{id}"} 형태로 직렬화한 뒤 URL-safe Base64 로 인코딩한다.
 * 같은 시각의 row 들을 정렬상 strict 하게 잘라내기 위해 id 를 tie-break 으로 결합한다.
 * 클라이언트는 토큰의 내부 구조를 알 필요 없이 응답에서 받은 값을 다음 요청에 그대로 echo 한다.</p>
 *
 * <p>{@code time} 의 의미는 호출 측 도메인이 정의한다 (예: {@code Portfolio.updatedAt} 그 자체일 수도 있고,
 * 여러 자식 집계의 GREATEST 결과인 "effective updatedAt" 일 수도 있다).
 * "effective" 라는 명칭은 컬럼 그 자체가 아니라 호출 측에서 계산되어 정렬 키로 채택된 값임을 강조한다.</p>
 *
 * <p>마이크로초까지 보존하는 이유: DB 컬럼이 {@code DATETIME(6)} 인 경우 마이크로초 정밀도이고,
 * 밀리초 단위로 절삭하면 같은 밀리초의 다른 마이크로초 row 들을 잘못된 쪽으로 분류해
 * 같은 항목이 다시 응답되거나 누락되는 가능성이 생긴다.</p>
 *
 * <p>점수(score) 기반 정렬 등 시간이 아닌 키로 페이지네이션 하려면 별도 cursor 타입을 정의해 분리한다.</p>
 */
public record EffectiveTimeCursor(
        Instant time,
        Long id
) {

    private static final String DELIMITER = "_";
    private static final long MICROS_PER_SECOND = 1_000_000L;
    private static final long NANOS_PER_MICRO = 1_000L;

    public String encode() {
        long micros = toEpochMicros(time);
        String raw = micros + DELIMITER + id;
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static EffectiveTimeCursor decode(String token) {
        try {
            String raw = new String(
                    Base64.getUrlDecoder().decode(token),
                    StandardCharsets.UTF_8
            );

            String[] parts = raw.split(DELIMITER);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid cursor format");
            }

            long micros = Long.parseLong(parts[0]);
            long parsedId = Long.parseLong(parts[1]);
            return new EffectiveTimeCursor(fromEpochMicros(micros), parsedId);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new SharedKernelApplicationException(
                    SharedKernelApplicationExceptionCodeCluster.DetailResponse.INFINITE_SCROLL_CURSOR_INVALID
            );
        }
    }

    private static long toEpochMicros(Instant instant) {
        long secondsAsMicros = Math.multiplyExact(instant.getEpochSecond(), MICROS_PER_SECOND);
        long nanosAsMicros = instant.getNano() / NANOS_PER_MICRO;
        return Math.addExact(secondsAsMicros, nanosAsMicros);
    }

    private static Instant fromEpochMicros(long micros) {
        long seconds = Math.floorDiv(micros, MICROS_PER_SECOND);
        int nanos = (int) (Math.floorMod(micros, MICROS_PER_SECOND) * NANOS_PER_MICRO);
        return Instant.ofEpochSecond(seconds, nanos);
    }
}
