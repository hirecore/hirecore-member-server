package io.hirecore.hirecorememberserver.modules.coverletter.application.port.in;

import java.util.Collection;
import java.util.Map;

public interface LoadCoverLetterTitlesUseCase {
    Map<Long, String> execute(Collection<Long> coverLetterIds);
}
