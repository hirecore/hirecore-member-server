package io.hirecore.hirecorememberserver.modules.file.adapter.in.scheduler;

import io.hirecore.hirecorememberserver.modules.file.application.port.in.CleanupOrphanedImageUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

@DisplayName("OrphanedImageCleanupScheduler 단위 테스트")
@ExtendWith(MockitoExtension.class)
class OrphanedImageCleanupSchedulerTest {

    @InjectMocks
    private OrphanedImageCleanupScheduler scheduler;

    @Mock
    private CleanupOrphanedImageUseCase cleanupOrphanedImageUseCase;

    @Test
    @DisplayName("run() 은 UseCase.execute() 를 그대로 호출한다")
    void should_delegate_to_use_case() {
        given(cleanupOrphanedImageUseCase.execute()).willReturn(3);

        scheduler.run();

        then(cleanupOrphanedImageUseCase).should().execute();
    }

    @Test
    @DisplayName("UseCase 가 예외를 던져도 스케줄러는 흡수하여 다음 주기에 영향이 없다")
    void should_swallow_use_case_exception() {
        willThrow(new RuntimeException("simulated")).given(cleanupOrphanedImageUseCase).execute();

        assertThatCode(() -> scheduler.run()).doesNotThrowAnyException();
    }
}
