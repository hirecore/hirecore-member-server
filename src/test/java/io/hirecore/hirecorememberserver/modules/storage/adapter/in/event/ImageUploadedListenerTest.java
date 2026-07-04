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
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

@DisplayName("ImageUploadedListener 단위 테스트")
@ExtendWith(MockitoExtension.class)
class ImageUploadedListenerTest {

    @InjectMocks
    private ImageUploadedListener listener;

    @Mock
    private RecordImageStorageUsageUseCase recordImageStorageUsageUseCase;

    private static ImageUploadedEvent eventFor(DomainType domainType, Purpose purpose) {
        return new ImageUploadedEvent(100L, 1L, domainType, purpose, 1_048_576L, Instant.now());
    }

    @Test
    @DisplayName("PORTFOLIO + CONTENT_IMAGE 이벤트는 PORTFOLIO_CONTENT 리소스로 기록한다")
    void should_resolve_portfolio_content() {
        ImageUploadedEvent event = eventFor(DomainType.PORTFOLIO, Purpose.CONTENT_IMAGE);

        listener.handleImageUploaded(event);

        then(recordImageStorageUsageUseCase).should()
                .execute(1L, 100L, ResourceKind.PORTFOLIO_CONTENT, 1_048_576L);
    }

    @Test
    @DisplayName("PORTFOLIO + THUMBNAIL_IMAGE 이벤트는 PORTFOLIO_THUMBNAIL 리소스로 기록한다")
    void should_resolve_portfolio_thumbnail() {
        ImageUploadedEvent event = eventFor(DomainType.PORTFOLIO, Purpose.THUMBNAIL_IMAGE);

        listener.handleImageUploaded(event);

        then(recordImageStorageUsageUseCase).should()
                .execute(1L, 100L, ResourceKind.PORTFOLIO_THUMBNAIL, 1_048_576L);
    }

    @Test
    @DisplayName("RESUME + CONTENT_IMAGE 이벤트는 RESUME_CONTENT 리소스로 기록한다")
    void should_resolve_resume_content() {
        ImageUploadedEvent event = eventFor(DomainType.RESUME, Purpose.CONTENT_IMAGE);

        listener.handleImageUploaded(event);

        then(recordImageStorageUsageUseCase).should()
                .execute(1L, 100L, ResourceKind.RESUME_CONTENT, 1_048_576L);
    }

    @Test
    @DisplayName("RESUME + THUMBNAIL_IMAGE 이벤트는 RESUME_ATTACHMENT 리소스로 기록한다")
    void should_resolve_resume_attachment() {
        ImageUploadedEvent event = eventFor(DomainType.RESUME, Purpose.THUMBNAIL_IMAGE);

        listener.handleImageUploaded(event);

        then(recordImageStorageUsageUseCase).should()
                .execute(1L, 100L, ResourceKind.RESUME_ATTACHMENT, 1_048_576L);
    }

    @Test
    @DisplayName("멱등키 race 로 DataIntegrityViolationException 이 발생해도 위로 전파되지 않고 흡수된다")
    void should_swallow_data_integrity_violation_from_race() {
        ImageUploadedEvent event = eventFor(DomainType.PORTFOLIO, Purpose.CONTENT_IMAGE);
        willThrow(new DataIntegrityViolationException("duplicate idempotency_key"))
                .given(recordImageStorageUsageUseCase)
                .execute(anyLong(), anyLong(), any(ResourceKind.class), anyLong());

        assertThatCode(() -> listener.handleImageUploaded(event)).doesNotThrowAnyException();
    }
}
