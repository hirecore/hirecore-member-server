package io.hirecore.hirecorememberserver.modules.storage.adapter.in.event;

import io.hirecore.hirecorememberserver.modules.storage.application.port.in.RecordImageStorageUsageUseCase;
import io.hirecore.hirecorememberserver.modules.storage.domain.vo.ResourceKind;
import io.hirecore.hirecorememberserver.sharedkernel.domain.event.ImageUploadedEvent;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.DomainType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Purpose;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.BDDMockito.then;

@DisplayName("ImageUploadedEventListener 단위 테스트")
@ExtendWith(MockitoExtension.class)
class ImageUploadedEventListenerTest {

    @InjectMocks
    private ImageUploadedEventListener listener;

    @Mock
    private RecordImageStorageUsageUseCase recordImageStorageUsageUseCase;

    @Test
    @DisplayName("ImageUploadedEvent를 수신하면 PORTFOLIO_CONTENT 리소스 종류로 사용량 기록 use case를 호출한다")
    void should_invoke_record_use_case_with_portfolio_content_kind() {
        // given
        ImageUploadedEvent event = new ImageUploadedEvent(100L, 1L, DomainType.PORTFOLIO, Purpose.CONTENT_IMAGE, 1_048_576L, Instant.now());

        // when
        listener.on(event);

        // then
        then(recordImageStorageUsageUseCase).should()
                .execute(1L, 100L, ResourceKind.PORTFOLIO_CONTENT, 1_048_576L);
    }
}
