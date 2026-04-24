package io.hirecore.hirecorememberserver.sharedkernel.application.port;

/**
 * 토큰 버전 검증을 위한 출력 포트.
 *
 * <p>JWT에 포함된 토큰 버전과 DB에 저장된 현재 버전을 비교하여
 * 로그아웃된(무효화된) 토큰인지 판별합니다.</p>
 */
public interface TokenVersionValidationPort {

    boolean isValidTokenVersion(Long memberId, int tokenVersion);
}
