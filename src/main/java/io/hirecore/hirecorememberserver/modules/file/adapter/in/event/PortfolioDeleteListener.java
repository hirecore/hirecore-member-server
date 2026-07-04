package io.hirecore.hirecorememberserver.modules.file.adapter.in.event;

import io.hirecore.hirecorememberserver.modules.file.application.port.in.MarkImageFileMetasAsOrphanedUseCase;
import io.hirecore.hirecorememberserver.sharedkernel.domain.event.PortfolioImagesUnlinkedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PortfolioDeleteListener {

    private final MarkImageFileMetasAsOrphanedUseCase markImageFileMetasAsOrphanedUseCase;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handlePortfolioDelete(PortfolioImagesUnlinkedEvent event) {
        markImageFileMetasAsOrphanedUseCase.execute(event.memberAccountId(), event.imageFileMetaIds());
    }
}
