package io.hirecore.hirecorememberserver.modules.resume.application.port.in;

import java.util.Collection;
import java.util.Map;

public interface LoadResumeTitlesUseCase {
    Map<Long, String> execute(Collection<Long> resumeIds);
}
