package io.hirecore.hirecorememberserver.sharedkernel.application.port.out;

import java.util.Collection;
import java.util.Map;

/**
 * 다른 BC 가 자기소개서의 "제목" 만 조회하기 위한 read-only port.
 *
 * <p>coverletter 도메인 전체가 아닌 표시용 식별 정보(제목) 만 sharedkernel 계약으로 노출한다.
 * 구현은 coverletter BC 의 {@code adapter/in/shared/} 에서 제공된다.</p>
 */
public interface LoadCoverLetterTitlePort {

    /**
     * 주어진 자기소개서 ID 들에 대해 제목을 한 번에 조회한다.
     *
     * @param coverLetterIds 조회할 자기소개서 ID 컬렉션 (null 또는 빈 컬렉션 허용)
     * @return id → title 매핑. 존재하지 않는 id 는 키가 포함되지 않는다. 빈 입력 시 빈 맵 반환.
     */
    Map<Long, String> findTitleMapByIds(Collection<Long> coverLetterIds);
}
