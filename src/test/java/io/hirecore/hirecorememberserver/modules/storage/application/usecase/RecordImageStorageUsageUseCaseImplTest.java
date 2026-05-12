package io.hirecore.hirecorememberserver.modules.storage.application.usecase;

import io.hirecore.hirecorememberserver.modules.storage.application.port.out.LoadUserStorageUsageLogPort;
import io.hirecore.hirecorememberserver.modules.storage.application.port.out.LoadUserStorageUsagePort;
import io.hirecore.hirecorememberserver.modules.storage.application.port.out.SaveUserStorageUsageLogPort;
import io.hirecore.hirecorememberserver.modules.storage.application.port.out.SaveUserStorageUsagePort;
import io.hirecore.hirecorememberserver.modules.storage.domain.UserStorageUsage;
import io.hirecore.hirecorememberserver.modules.storage.domain.UserStorageUsageLog;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.never;

@DisplayName("RecordImageStorageUsageUseCaseImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class RecordImageStorageUsageUseCaseImplTest {

    @InjectMocks
    private RecordImageStorageUsageUseCaseImpl sut;

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
    private static final String IDEMPOTENCY_KEY = "image-uploaded:" + IMAGE_FILE_META_ID;

    @Nested
    @DisplayName("성공 케이스")
    class SuccessTest {

        @Test
        @DisplayName("기존 사용량이 없으면 0에서 시작해 증분된 사용량을 저장한다")
        void should_create_new_usage_when_not_exists() {
            // given
            given(loadUserStorageUsageLogPort.existsByIdempotencyKey(IDEMPOTENCY_KEY))
                    .willReturn(false);
            given(loadUserStorageUsagePort.findByMemberAccountId(MEMBER_ACCOUNT_ID))
                    .willReturn(Optional.empty());
            willAnswer(invocation -> invocation.<UserStorageUsage>getArgument(0))
                    .given(saveUserStorageUsagePort).save(any(UserStorageUsage.class));

            // when
            sut.execute(MEMBER_ACCOUNT_ID, IMAGE_FILE_META_ID, ResourceKind.PORTFOLIO_CONTENT, FILE_SIZE_BYTES);

            // then
            ArgumentCaptor<UserStorageUsage> usageCaptor = ArgumentCaptor.forClass(UserStorageUsage.class);
            then(saveUserStorageUsagePort).should().save(usageCaptor.capture());
            assertThat(usageCaptor.getValue().getMemberAccountId()).isEqualTo(MEMBER_ACCOUNT_ID);
            assertThat(usageCaptor.getValue().getUsedQuotaBytes()).isEqualTo(FILE_SIZE_BYTES);
        }

        @Test
        @DisplayName("기존 사용량이 있으면 그 위에 증분하여 저장한다")
        void should_increase_existing_usage() {
            // given
            given(loadUserStorageUsageLogPort.existsByIdempotencyKey(IDEMPOTENCY_KEY))
                    .willReturn(false);
            UserStorageUsage existing = UserStorageUsage.createForMember(MEMBER_ACCOUNT_ID).increase(2_000_000L);
            given(loadUserStorageUsagePort.findByMemberAccountId(MEMBER_ACCOUNT_ID))
                    .willReturn(Optional.of(existing));
            willAnswer(invocation -> invocation.<UserStorageUsage>getArgument(0))
                    .given(saveUserStorageUsagePort).save(any(UserStorageUsage.class));

            // when
            sut.execute(MEMBER_ACCOUNT_ID, IMAGE_FILE_META_ID, ResourceKind.PORTFOLIO_CONTENT, FILE_SIZE_BYTES);

            // then
            ArgumentCaptor<UserStorageUsage> captor = ArgumentCaptor.forClass(UserStorageUsage.class);
            then(saveUserStorageUsagePort).should().save(captor.capture());
            assertThat(captor.getValue().getUsedQuotaBytes()).isEqualTo(2_000_000L + FILE_SIZE_BYTES);
        }

        @Test
        @DisplayName("저장된 변경 이력에 변경 전/후 사용량과 멱등 키가 정확히 기록된다")
        void should_persist_log_with_before_after_and_idempotency_key() {
            // given
            given(loadUserStorageUsageLogPort.existsByIdempotencyKey(IDEMPOTENCY_KEY))
                    .willReturn(false);
            UserStorageUsage existing = UserStorageUsage.createForMember(MEMBER_ACCOUNT_ID).increase(500L);
            given(loadUserStorageUsagePort.findByMemberAccountId(MEMBER_ACCOUNT_ID))
                    .willReturn(Optional.of(existing));
            willAnswer(invocation -> invocation.<UserStorageUsage>getArgument(0))
                    .given(saveUserStorageUsagePort).save(any(UserStorageUsage.class));

            // when
            sut.execute(MEMBER_ACCOUNT_ID, IMAGE_FILE_META_ID, ResourceKind.PORTFOLIO_CONTENT, FILE_SIZE_BYTES);

            // then
            ArgumentCaptor<UserStorageUsageLog> logCaptor = ArgumentCaptor.forClass(UserStorageUsageLog.class);
            then(saveUserStorageUsageLogPort).should().save(logCaptor.capture());

            UserStorageUsageLog log = logCaptor.getValue();
            assertThat(log.getMemberAccountId()).isEqualTo(MEMBER_ACCOUNT_ID);
            assertThat(log.getResourceKind()).isEqualTo(ResourceKind.PORTFOLIO_CONTENT);
            assertThat(log.getResourceKindId()).isEqualTo(IMAGE_FILE_META_ID);
            assertThat(log.getChangeBytes()).isEqualTo(FILE_SIZE_BYTES);
            assertThat(log.getBeforeUsedQuotaBytes()).isEqualTo(500L);
            assertThat(log.getAfterUsedQuotaBytes()).isEqualTo(500L + FILE_SIZE_BYTES);
            assertThat(log.getUsageChangeReason()).isEqualTo(UsageChangeReason.RESOURCE_CREATION);
            assertThat(log.getIdempotencyKey()).isEqualTo(IDEMPOTENCY_KEY);
        }
    }

    @Nested
    @DisplayName("멱등성 보장 케이스")
    class IdempotencyTest {

        @Test
        @DisplayName("이미 같은 멱등키로 처리된 작업이면 사용량 변경과 로그 저장이 일어나지 않는다")
        void should_skip_when_idempotency_key_exists() {
            // given
            given(loadUserStorageUsageLogPort.existsByIdempotencyKey(IDEMPOTENCY_KEY))
                    .willReturn(true);

            // when
            sut.execute(MEMBER_ACCOUNT_ID, IMAGE_FILE_META_ID, ResourceKind.PORTFOLIO_CONTENT, FILE_SIZE_BYTES);

            // then
            then(loadUserStorageUsagePort).should(never()).findByMemberAccountId(any());
            then(saveUserStorageUsagePort).should(never()).save(any(UserStorageUsage.class));
            then(saveUserStorageUsageLogPort).should(never()).save(any(UserStorageUsageLog.class));
        }

        @Test
        @DisplayName("멱등키 조회는 imageFileMetaId 로 파생된 키로 호출된다")
        void should_query_idempotency_with_derived_key() {
            // given
            given(loadUserStorageUsageLogPort.existsByIdempotencyKey(IDEMPOTENCY_KEY))
                    .willReturn(true);

            // when
            sut.execute(MEMBER_ACCOUNT_ID, IMAGE_FILE_META_ID, ResourceKind.PORTFOLIO_CONTENT, FILE_SIZE_BYTES);

            // then
            then(loadUserStorageUsageLogPort).should().existsByIdempotencyKey(IDEMPOTENCY_KEY);
        }
    }
}
