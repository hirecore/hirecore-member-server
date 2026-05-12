package io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.repository.UserStorageUsageLogJpaQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("UserStorageUsageLogJpaQueryAdapter 단위 테스트")
@ExtendWith(MockitoExtension.class)
class UserStorageUsageLogJpaQueryAdapterTest {

    @InjectMocks
    private UserStorageUsageLogJpaQueryAdapter sut;

    @Mock
    private UserStorageUsageLogJpaQueryRepository userStorageUsageLogJpaQueryRepository;

    private static final String IDEMPOTENCY_KEY = "image-uploaded:100";

    @Test
    @DisplayName("존재하는 멱등키에 대해 true 를 반환한다")
    void should_return_true_when_idempotency_key_exists() {
        // given
        given(userStorageUsageLogJpaQueryRepository.existsByIdempotencyKey(IDEMPOTENCY_KEY))
                .willReturn(true);

        // when
        boolean result = sut.existsByIdempotencyKey(IDEMPOTENCY_KEY);

        // then
        assertThat(result).isTrue();
        then(userStorageUsageLogJpaQueryRepository).should().existsByIdempotencyKey(IDEMPOTENCY_KEY);
    }

    @Test
    @DisplayName("존재하지 않는 멱등키에 대해 false 를 반환한다")
    void should_return_false_when_idempotency_key_not_exists() {
        // given
        given(userStorageUsageLogJpaQueryRepository.existsByIdempotencyKey(IDEMPOTENCY_KEY))
                .willReturn(false);

        // when
        boolean result = sut.existsByIdempotencyKey(IDEMPOTENCY_KEY);

        // then
        assertThat(result).isFalse();
        then(userStorageUsageLogJpaQueryRepository).should().existsByIdempotencyKey(IDEMPOTENCY_KEY);
    }
}
