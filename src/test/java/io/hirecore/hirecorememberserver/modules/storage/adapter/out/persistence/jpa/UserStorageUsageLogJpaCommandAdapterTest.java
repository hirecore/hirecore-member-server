package io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.entity.UserStorageUsageLogJpaEntity;
import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.mapper.UserStorageUsageLogJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.repository.UserStorageUsageLogJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.storage.domain.UserStorageUsageLog;
import io.hirecore.hirecorememberserver.modules.storage.domain.vo.ResourceKind;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@DisplayName("UserStorageUsageLogJpaCommandAdapter 단위 테스트")
@ExtendWith(MockitoExtension.class)
class UserStorageUsageLogJpaCommandAdapterTest {

    @InjectMocks
    private UserStorageUsageLogJpaCommandAdapter sut;

    @Mock
    private UserStorageUsageLogJpaCommandRepository userStorageUsageLogJpaCommandRepository;

    @Mock
    private UserStorageUsageLogJpaEntityMapper userStorageUsageLogJpaEntityMapper;

    @Test
    @DisplayName("도메인 객체를 엔티티로 변환해 저장하고 다시 도메인으로 매핑하여 반환한다")
    void should_save_log_via_mapper_roundtrip() {
        // given
        UserStorageUsageLog log = UserStorageUsageLog.createForResourceCreation(
                1L, ResourceKind.PORTFOLIO_CONTENT, 100L, 1024L, 0L, 1024L, "image-uploaded:100"
        );
        UserStorageUsageLogJpaEntity entity = mock(UserStorageUsageLogJpaEntity.class);
        UserStorageUsageLogJpaEntity savedEntity = mock(UserStorageUsageLogJpaEntity.class);
        UserStorageUsageLog expectedDomain = mock(UserStorageUsageLog.class);

        given(userStorageUsageLogJpaEntityMapper.toJpaEntity(log)).willReturn(entity);
        given(userStorageUsageLogJpaCommandRepository.save(entity)).willReturn(savedEntity);
        given(userStorageUsageLogJpaEntityMapper.toDomain(savedEntity)).willReturn(expectedDomain);

        // when
        UserStorageUsageLog result = sut.save(log);

        // then
        assertThat(result).isSameAs(expectedDomain);
        then(userStorageUsageLogJpaCommandRepository).should().save(entity);
    }
}
