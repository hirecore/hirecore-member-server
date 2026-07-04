package io.hirecore.hirecorememberserver.modules.file.adapter.in.event;

import io.hirecore.hirecorememberserver.modules.file.application.port.in.MarkImageFileMetasAsOrphanedUseCase;
import io.hirecore.hirecorememberserver.sharedkernel.domain.event.PortfolioImagesUnlinkedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.BDDMockito.then;

@DisplayName("PortfolioImagesUnlinkedImageOrphaningHandler 단위 테스트")
@ExtendWith(MockitoExtension.class)
class PortfolioDeleteListenerTest {

    @InjectMocks
    private PortfolioDeleteListener handler;

    @Mock
    private MarkImageFileMetasAsOrphanedUseCase markImageFileMetasAsOrphanedUseCase;

    @Test
    @DisplayName("이벤트의 memberAccountId 와 imageFileMetaIds 가 그대로 UseCase 로 위임된다")
    void should_delegate_event_payload_to_use_case() {
        // given
        PortfolioImagesUnlinkedEvent event = new PortfolioImagesUnlinkedEvent(
                5L,
                1L,
                List.of(100L, 200L, 300L)
        );

        // when
        handler.handlePortfolioDelete(event);

        // then
        then(markImageFileMetasAsOrphanedUseCase).should()
                .execute(1L, List.of(100L, 200L, 300L));
    }
}
