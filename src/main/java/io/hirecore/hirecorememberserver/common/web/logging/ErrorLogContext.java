package io.hirecore.hirecorememberserver.common.web.logging;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * HTTP 요청 정보만 담는 컨텍스트.
 * 서버 내부 진단 정보는 {@link InternalContext}에서 별도 관리합니다.
 */
@Getter
@Builder
@ToString
public class ErrorLogContext {
    private final String httpMethod;
    private final String requestUri;
    private final String queryString;
    private final String clientIp;
    private final String userAgent;
    private final String userPrincipal;
}
