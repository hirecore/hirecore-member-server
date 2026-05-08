package io.hirecore.hirecorememberserver.modules.storage.application.usecase;

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

@DisplayName("RecordImageStorageUsageUseCaseImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class RecordImageStorageUsageUseCaseImplTest {

    @InjectMocks
    private RecordImageStorageUsageUseCaseImpl sut;

    @Mock
    private LoadUserStorageUsagePort loadUserStorageUsagePort;

    @Mock
    private SaveUserStorageUsagePort saveUserStorageUsagePort;

    @Mock
    private SaveUserStorageUsageLogPort saveUserStorageUsageLogPort;

    private static final Long MEMBER_ACCOUNT_ID = 1L;
    private static final Long IMAGE_FILE_META_ID = 100L;
    private static final Long FILE_SIZE_BYTES = 1_048_576L;

    @Nested
    @DisplayName("성공 케이스")
    class SuccessTest {

        @Test
        @DisplayName("기존 사용량이 없으면 0에서 시작해 증분된 사용량을 저장한다")
        void should_create_new_usage_when_not_exists() {
            // given
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
            assertThat(log.getIdempotencyKey()).isEqualTo("image-uploaded:" + IMAGE_FILE_META_ID);
        }
    }
}
