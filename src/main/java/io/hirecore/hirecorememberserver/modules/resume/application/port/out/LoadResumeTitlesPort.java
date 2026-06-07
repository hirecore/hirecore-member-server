package io.hirecore.hirecorememberserver.modules.resume.application.port.out;

import java.util.Collection;
import java.util.Map;

public interface LoadResumeTitlesPort {

    /**
     * 주어진 id 들에 대응하는 이력서 제목을 한 번의 조회로 적재한다.
     * 존재하지 않는 id 는 결과 맵에 포함되지 않는다.
     */
    Map<Long, String> findAllTitlesByIds(Collection<Long> resumeIds);
}
