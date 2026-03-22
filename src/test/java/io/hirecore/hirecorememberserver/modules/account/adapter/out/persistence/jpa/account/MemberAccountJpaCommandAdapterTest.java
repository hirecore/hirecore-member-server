package io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.account;

import com.navercorp.fixturemonkey.FixtureMonkey;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.MemberAccountJpaCommandAdapter;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.entity.MemberAccountJpaEntity;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.mapper.MemberAccountJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.repository.MemberAccountJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.account.domain.MemberAccount;
import io.hirecore.hirecorememberserver.support.FixtureMonkeyFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("MemberAccountJpaCommandAdapter 단위 테스트")
@ExtendWith(MockitoExtension.class)
class MemberAccountJpaCommandAdapterTest {

    private final FixtureMonkey fm = FixtureMonkeyFactory.monkey();

    @InjectMocks
    private MemberAccountJpaCommandAdapter adapter;

    @Mock
    private MemberAccountJpaEntityMapper mapper;
    @Mock
    private MemberAccountJpaCommandRepository repository;

    @Test
    @DisplayName("MemberAccount 도메인 객체를 JPA 엔티티로 변환하여 저장하고 도메인 객체로 반환한다")
    void should_save_member_account_and_return_domain() {
        // given
        MemberAccount domain = fm.giveMeOne(MemberAccount.class);
        MemberAccountJpaEntity jpaEntity = fm.giveMeOne(MemberAccountJpaEntity.class);
        MemberAccount savedDomain = fm.giveMeOne(MemberAccount.class);

        given(mapper.toJpaEntity(domain)).willReturn(jpaEntity);
        given(repository.save(jpaEntity)).willReturn(jpaEntity);
        given(mapper.toDomain(jpaEntity)).willReturn(savedDomain);

        // when
        MemberAccount result = adapter.save(domain);

        // then
        assertThat(result).isSameAs(savedDomain);
        then(mapper).should().toJpaEntity(domain);
        then(repository).should().save(jpaEntity);
        then(mapper).should().toDomain(jpaEntity);
    }
}
