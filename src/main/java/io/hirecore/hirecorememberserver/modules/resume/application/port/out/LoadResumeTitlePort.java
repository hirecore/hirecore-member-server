package io.hirecore.hirecorememberserver.modules.resume.application.port.out;

import java.util.Collection;
import java.util.Map;

public interface LoadResumeTitlePort {

    // id → 제목 맵 일괄 조회 (없는 id 는 제외)
    Map<Long, String> findTitleMapByIds(Collection<Long> resumeIds);
}
