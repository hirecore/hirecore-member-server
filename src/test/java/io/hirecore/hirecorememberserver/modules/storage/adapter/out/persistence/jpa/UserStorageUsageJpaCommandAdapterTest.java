package io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa;

import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.entity.UserStorageUsageJpaEntity;
import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.mapper.UserStorageUsageJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.repository.UserStorageUsageJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.storage.adapter.out.persistence.jpa.repository.UserStorageUsageJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.storage.domain.UserStorageUsage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@DisplayName("UserStorageUsageJpaCommandAdapter 단위 테스트")
@ExtendWith(MockitoExtension.class)
class UserStorageUsageJpaCommandAdapterTest {

    @InjectMocks
    private UserStorageUsageJpaCommandAdapter sut;

    @Mock
    private UserStorageUsageJpaQueryRepository userStorageUsageJpaQueryRepository;

    @Mock
    private UserStorageUsageJpaCommandRepository userStorageUsageJpaCommandRepository;

    @Mock
    private UserStorageUsageJpaEntityMapper userStorageUsageJpaEntityMapper;

    @Test
    @DisplayName("기존 엔티티가 있으면 기존 엔티티에 새 사용량을 적용한다 (insert 호출 없음)")
    void should_apply_to_existing_entity_when_present() {
        // given
        UserStorageUsage domain = UserStorageUsage.createForMember(1L).increase(1024L);
        UserStorageUsageJpaEntity existingEntity = mock(UserStorageUsageJpaEntity.class);
        UserStorageUsage expectedDomain = UserStorageUsage.createForMember(1L);

        given(userStorageUsageJpaQueryRepository.findByMemberAccountId(1L))
                .willReturn(Optional.of(existingEntity));
        given(userStorageUsageJpaEntityMapper.toDomain(existingEntity)).willReturn(expectedDomain);

        // when
        UserStorageUsage result = sut.save(domain);

        // then
        assertThat(result).isSameAs(expectedDomain);
        then(existingEntity).should().applyUsedQuotaBytes(
                domain.getUsedQuotaBytes(),
                domain.getAuditingInfo().updatedAt()
        );
        then(userStorageUsageJpaCommandRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("기존 엔티티가 없으면 매퍼로 변환한 새 엔티티를 저장한다")
    void should_save_new_entity_when_not_present() {
        // given
        UserStorageUsage domain = UserStorageUsage.createForMember(1L);
        UserStorageUsageJpaEntity freshEntity = mock(UserStorageUsageJpaEntity.class);
        UserStorageUsageJpaEntity persistedEntity = mock(UserStorageUsageJpaEntity.class);
        UserStorageUsage expectedDomain = UserStorageUsage.createForMember(1L);

        given(userStorageUsageJpaQueryRepository.findByMemberAccountId(1L))
                .willReturn(Optional.empty());
        given(userStorageUsageJpaEntityMapper.toJpaEntity(domain)).willReturn(freshEntity);
        given(userStorageUsageJpaCommandRepository.save(freshEntity)).willReturn(persistedEntity);
        given(userStorageUsageJpaEntityMapper.toDomain(persistedEntity)).willReturn(expectedDomain);

        // when
        UserStorageUsage result = sut.save(domain);

        // then
        assertThat(result).isSameAs(expectedDomain);
        then(userStorageUsageJpaCommandRepository).should().save(freshEntity);
    }
}
