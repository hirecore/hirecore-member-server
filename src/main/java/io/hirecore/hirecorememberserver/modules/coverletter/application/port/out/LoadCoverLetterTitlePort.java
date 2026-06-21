package io.hirecore.hirecorememberserver.modules.coverletter.application.port.out;

import java.util.Collection;
import java.util.Map;

public interface LoadCoverLetterTitlePort {

    /**
     * 주어진 id 들에 대응하는 자기소개서 제목을 한 번의 조회로 적재한다.
     * 존재하지 않는 id 는 결과 맵에 포함되지 않는다.
     */
    Map<Long, String> findTitleMapByIds(Collection<Long> coverLetterIds);
}
