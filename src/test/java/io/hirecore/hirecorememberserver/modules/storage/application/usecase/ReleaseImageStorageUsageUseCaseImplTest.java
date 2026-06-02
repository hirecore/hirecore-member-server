package io.hirecore.hirecorememberserver.modules.storage.application.usecase;

import io.hirecore.hirecorememberserver.modules.storage.application.port.out.LoadUserStorageUsageLogPort;
import io.hirecore.hirecorememberserver.modules.storage.application.port.out.LoadUserStorageUsagePort;
import io.hirecore.hirecorememberserver.modules.storage.application.port.out.SaveUserStorageUsageLogPort;
import io.hirecore.hirecorememberserver.modules.storage.application.port.out.SaveUserStorageUsagePort;
import io.hirecore.hirecorememberserver.modules.storage.domain.UserStorageUsage;
import io.hirecore.hirecorememberserver.modules.storage.domain.UserStorageUsageLog;
import io.hirecore.hirecorememberserver.modules.storage.domain.exception.UserStorageUsageDomainException;
import io.hirecore.hirecorememberserver.modules.storage.domain.vo.ResourceKind;
import io.hirecore.hirecorememberserver.modules.storage.domain.vo.UsageChangeReason;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.never;

@DisplayName("ReleaseImageStorageUsageUseCaseImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class ReleaseImageStorageUsageUseCaseImplTest {

    @InjectMocks
    private ReleaseImageStorageUsageUseCaseImpl sut;

    @Mock
    private LoadUserStorageUsagePort loadUserStorageUsagePort;

    @Mock
    private LoadUserStorageUsageLogPort loadUserStorageUsageLogPort;

    @Mock
    private SaveUserStorageUsagePort saveUserStorageUsagePort;

    @Mock
    private SaveUserStorageUsageLogPort saveUserStorageUsageLogPort;

    private static final Long MEMBER_ACCOUNT_ID = 1L;
    private static final Long IMAGE_FILE_META_ID = 100L;
    private static final Long FILE_SIZE_BYTES = 1_048_576L;
    private static final String IDEMPOTENCY_KEY = "image-orphaned:" + IMAGE_FILE_META_ID;

    @Nested
    @DisplayName("성공 케이스")
    class SuccessTest {

        @Test
        @DisplayName("기존 사용량에서 fileSizeBytes 만큼을 차감한 인스턴스가 저장된다")
        void should_decrease_existing_usage() {
            // given
            given(loadUserStorageUsageLogPort.existsByIdempotencyKey(IDEMPOTENCY_KEY)).willReturn(false);
            UserStorageUsage existing = UserStorageUsage.createForMember(MEMBER_ACCOUNT_ID).increase(2_000_000L);
            given(loadUserStorageUsagePort.findByMemberAccountId(MEMBER_ACCOUNT_ID))
                    .willReturn(Optional.of(existing));
            willAnswer(invocation -> invocation.<UserStorageUsage>getArgument(0))
                    .given(saveUserStorageUsagePort).save(any(UserStorageUsage.class));

            // when
            sut.execute(MEMBER_ACCOUNT_ID, IMAGE_FILE_META_ID, ResourceKind.PORTFOLIO_CONTENT, FILE_SIZE_BYTES);

            // then
            ArgumentCaptor<UserStorageUsage> usageCaptor = ArgumentCaptor.forClass(UserStorageUsage.class);
            then(saveUserStorageUsagePort).should().save(usageCaptor.capture());
            assertThat(usageCaptor.getValue().getUsedQuotaBytes()).isEqualTo(2_000_000L - FILE_SIZE_BYTES);
        }

        @Test
        @DisplayName("저장되는 로그에 변경 전/후 사용량과 음수 changeBytes, RESOURCE_DELETION reason, 멱등 키가 정확히 기록된다")
        void should_persist_log_with_negative_change_bytes_and_deletion_reason() {
            // given
            given(loadUserStorageUsageLogPort.existsByIdempotencyKey(IDEMPOTENCY_KEY)).willReturn(false);
            UserStorageUsage existing = UserStorageUsage.createForMember(MEMBER_ACCOUNT_ID).increase(3_000_000L);
            given(loadUserStorageUsagePort.findByMemberAccountId(MEMBER_ACCOUNT_ID))
                    .willReturn(Optional.of(existing));
            willAnswer(invocation -> invocation.<UserStorageUsage>getArgument(0))
                    .given(saveUserStorageUsagePort).save(any(UserStorageUsage.class));

            // when
            sut.execute(MEMBER_ACCOUNT_ID, IMAGE_FILE_META_ID, ResourceKind.PORTFOLIO_THUMBNAIL, FILE_SIZE_BYTES);

            // then
            ArgumentCaptor<UserStorageUsageLog> logCaptor = ArgumentCaptor.forClass(UserStorageUsageLog.class);
            then(saveUserStorageUsageLogPort).should().save(logCaptor.capture());

            UserStorageUsageLog log = logCaptor.getValue();
            assertThat(log.getMemberAccountId()).isEqualTo(MEMBER_ACCOUNT_ID);
            assertThat(log.getResourceKind()).isEqualTo(ResourceKind.PORTFOLIO_THUMBNAIL);
            assertThat(log.getResourceKindId()).isEqualTo(IMAGE_FILE_META_ID);
            assertThat(log.getChangeBytes()).isEqualTo(-FILE_SIZE_BYTES);
            assertThat(log.getBeforeUsedQuotaBytes()).isEqualTo(3_000_000L);
            assertThat(log.getAfterUsedQuotaBytes()).isEqualTo(3_000_000L - FILE_SIZE_BYTES);
            assertThat(log.getUsageChangeReason()).isEqualTo(UsageChangeReason.RESOURCE_DELETION);
            assertThat(log.getIdempotencyKey()).isEqualTo(IDEMPOTENCY_KEY);
        }
    }

    @Nested
    @DisplayName("멱등성 보장 케이스")
    class IdempotencyTest {

        @Test
        @DisplayName("이미 같은 멱등키로 처리되어 있으면 사용량 변경과 로그 저장이 일어나지 않는다")
        void should_skip_when_idempotency_key_exists() {
            // given
            given(loadUserStorageUsageLogPort.existsByIdempotencyKey(IDEMPOTENCY_KEY)).willReturn(true);

            // when
            sut.execute(MEMBER_ACCOUNT_ID, IMAGE_FILE_META_ID, ResourceKind.PORTFOLIO_CONTENT, FILE_SIZE_BYTES);

            // then
            then(loadUserStorageUsagePort).should(never()).findByMemberAccountId(any());
            then(saveUserStorageUsagePort).should(never()).save(any(UserStorageUsage.class));
            then(saveUserStorageUsageLogPort).should(never()).save(any(UserStorageUsageLog.class));
        }
    }

    @Nested
    @DisplayName("정합성 사고 흡수 케이스")
    class ConsistencyEdgeCaseTest {

        @Test
        @DisplayName("사용량 row 가 존재하지 않으면 변경 없이 흡수되어 사용자 흐름을 막지 않는다")
        void should_swallow_when_usage_row_missing() {
            // given
            given(loadUserStorageUsageLogPort.existsByIdempotencyKey(IDEMPOTENCY_KEY)).willReturn(false);
            given(loadUserStorageUsagePort.findByMemberAccountId(MEMBER_ACCOUNT_ID))
                    .willReturn(Optional.empty());

            // when
            sut.execute(MEMBER_ACCOUNT_ID, IMAGE_FILE_META_ID, ResourceKind.PORTFOLIO_CONTENT, FILE_SIZE_BYTES);

            // then
            then(saveUserStorageUsagePort).should(never()).save(any(UserStorageUsage.class));
            then(saveUserStorageUsageLogPort).should(never()).save(any(UserStorageUsageLog.class));
        }

        @Test
        @DisplayName("저장된 사용량이 차감 요청량보다 작으면 도메인 invariant 위반으로 예외가 전파된다")
        void should_propagate_when_insufficient_usage() {
            // given
            given(loadUserStorageUsageLogPort.existsByIdempotencyKey(IDEMPOTENCY_KEY)).willReturn(false);
            UserStorageUsage existing = UserStorageUsage.createForMember(MEMBER_ACCOUNT_ID).increase(500L);
            given(loadUserStorageUsagePort.findByMemberAccountId(MEMBER_ACCOUNT_ID))
                    .willReturn(Optional.of(existing));

            // when & then
            assertThatThrownBy(() ->
                    sut.execute(MEMBER_ACCOUNT_ID, IMAGE_FILE_META_ID, ResourceKind.PORTFOLIO_CONTENT, FILE_SIZE_BYTES)
            ).isInstanceOf(UserStorageUsageDomainException.class);

            then(saveUserStorageUsagePort).should(never()).save(any(UserStorageUsage.class));
            then(saveUserStorageUsageLogPort).should(never()).save(any(UserStorageUsageLog.class));
        }
    }
}
