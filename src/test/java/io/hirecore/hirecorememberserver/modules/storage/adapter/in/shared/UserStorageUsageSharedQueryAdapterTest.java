package io.hirecore.hirecorememberserver.modules.storage.adapter.in.shared;

import io.hirecore.hirecorememberserver.modules.storage.application.exception.UserStorageApplicationException;
import io.hirecore.hirecorememberserver.modules.storage.application.exception.UserStorageApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.storage.application.port.in.LoadUserStorageUsageUseCase;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadUserStorageLimitPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@DisplayName("UserStorageUsageSharedQueryAdapter 단위 테스트")
@ExtendWith(MockitoExtension.class)
class UserStorageUsageSharedQueryAdapterTest {

    @InjectMocks
    private UserStorageUsageSharedQueryAdapter sut;

    @Mock
    private LoadUserStorageUsageUseCase loadUserStorageUsageUseCase;

    @Mock
    private LoadUserStorageLimitPort loadUserStorageLimitPort;

    private static final Long MEMBER_ACCOUNT_ID = 1L;
    private static final Long STORAGE_QUOTA_BYTES = 10_000L;

    @Nested
    @DisplayName("verifyCapacityFor — 스토리지 용량 검증")
    class VerifyCapacityForTest {

        @Test
        @DisplayName("사용량 + 업로드 크기가 할당량 이내면 예외가 발생하지 않는다")
        void should_pass_when_within_capacity() {
            given(loadUserStorageLimitPort.findStorageLimitBytes(MEMBER_ACCOUNT_ID)).willReturn(STORAGE_QUOTA_BYTES);
            given(loadUserStorageUsageUseCase.execute(MEMBER_ACCOUNT_ID)).willReturn(3_000L);

            assertThatCode(() -> sut.verifyCapacityFor(MEMBER_ACCOUNT_ID, 5_000L))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("사용량 + 업로드 크기가 할당량과 정확히 같으면 예외가 발생하지 않는다 (경계)")
        void should_pass_when_exactly_at_limit() {
            given(loadUserStorageLimitPort.findStorageLimitBytes(MEMBER_ACCOUNT_ID)).willReturn(STORAGE_QUOTA_BYTES);
            given(loadUserStorageUsageUseCase.execute(MEMBER_ACCOUNT_ID)).willReturn(3_000L);

            assertThatCode(() -> sut.verifyCapacityFor(MEMBER_ACCOUNT_ID, 7_000L))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("사용량 + 업로드 크기가 할당량을 초과하면 STORAGE_QUOTA_EXCEEDED 예외가 발생한다")
        void should_throw_when_exceeds_capacity() {
            given(loadUserStorageLimitPort.findStorageLimitBytes(MEMBER_ACCOUNT_ID)).willReturn(STORAGE_QUOTA_BYTES);
            given(loadUserStorageUsageUseCase.execute(MEMBER_ACCOUNT_ID)).willReturn(3_000L);

            assertThatThrownBy(() -> sut.verifyCapacityFor(MEMBER_ACCOUNT_ID, 7_001L))
                    .isInstanceOf(UserStorageApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(UserStorageApplicationExceptionCodeCluster.DetailResponse.STORAGE_QUOTA_EXCEEDED.getErrorCode());
        }
    }
}
