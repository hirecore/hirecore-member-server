package io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.account;

import com.navercorp.fixturemonkey.FixtureMonkey;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.SocialAccountJpaCommandAdapter;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.entity.SocialAccountJpaEntity;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.mapper.SocialAccountJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.repository.SocialAccountJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.account.domain.SocialAccount;
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

@DisplayName("SocialAccountJpaCommandAdapter 단위 테스트")
@ExtendWith(MockitoExtension.class)
class SocialAccountJpaCommandAdapterTest {

    private final FixtureMonkey fm = FixtureMonkeyFactory.monkey();

    @InjectMocks
    private SocialAccountJpaCommandAdapter adapter;

    @Mock
    private SocialAccountJpaEntityMapper mapper;
    @Mock
    private SocialAccountJpaCommandRepository repository;

    @Test
    @DisplayName("SocialAccount 도메인 객체를 JPA 엔티티로 변환하여 저장하고 도메인 객체로 반환한다")
    void should_save_social_account_and_return_domain() {
        // given
        SocialAccount domain = fm.giveMeOne(SocialAccount.class);
        SocialAccountJpaEntity entity = fm.giveMeOne(SocialAccountJpaEntity.class);
        SocialAccount savedDomain = fm.giveMeOne(SocialAccount.class);

        given(mapper.toJpaEntity(domain)).willReturn(entity);
        given(repository.save(entity)).willReturn(entity);
        given(mapper.toDomain(entity)).willReturn(savedDomain);

        // when
        SocialAccount result = adapter.save(domain);

        // then
        assertThat(result).isSameAs(savedDomain);
        then(mapper).should().toJpaEntity(domain);
        then(repository).should().save(entity);
        then(mapper).should().toDomain(entity);
    }
}
