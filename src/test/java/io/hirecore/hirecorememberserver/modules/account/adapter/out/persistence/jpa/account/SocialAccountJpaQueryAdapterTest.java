package io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.account;

import com.navercorp.fixturemonkey.FixtureMonkey;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.SocialAccountJpaQueryAdapter;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.entity.SocialAccountJpaEntity;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.mapper.SocialAccountJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.repository.SocialAccountJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.account.domain.SocialAccount;
import io.hirecore.hirecorememberserver.sharedkernel.vo.OAuth2Provider;
import io.hirecore.hirecorememberserver.support.FixtureMonkeyFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@DisplayName("SocialAccountJpaQueryAdapter 단위 테스트")
@ExtendWith(MockitoExtension.class)
class SocialAccountJpaQueryAdapterTest {

    private final FixtureMonkey fm = FixtureMonkeyFactory.monkey();

    @InjectMocks
    private SocialAccountJpaQueryAdapter adapter;

    @Mock
    private SocialAccountJpaQueryRepository repository;
    @Mock
    private SocialAccountJpaEntityMapper mapper;

    @Test
    @DisplayName("존재하는 provider와 providerId로 조회하면 SocialAccount를 반환한다")
    void should_return_social_account_when_provider_and_provider_id_exists() {
        // given
        SocialAccountJpaEntity entity = fm.giveMeOne(SocialAccountJpaEntity.class);
        SocialAccount domain = fm.giveMeOne(SocialAccount.class);
        OAuth2Provider provider = entity.getProvider();
        String providerId = entity.getProviderId();

        given(repository.findByProviderAndProviderId(provider, providerId))
                .willReturn(Optional.of(entity));
        given(mapper.toDomain(entity)).willReturn(domain);

        // when
        Optional<SocialAccount> result = adapter.findByProviderAndProviderId(provider, providerId);

        // then
        assertThat(result).hasValueSatisfying(a -> assertThat(a).isSameAs(domain));
    }

    @Test
    @DisplayName("존재하지 않는 provider와 providerId로 조회하면 빈 Optional을 반환한다")
    void should_return_empty_when_provider_and_provider_id_not_found() {
        // given
        given(repository.findByProviderAndProviderId(OAuth2Provider.KAKAO, "non-existent"))
                .willReturn(Optional.empty());

        // when
        Optional<SocialAccount> result = adapter.findByProviderAndProviderId(OAuth2Provider.KAKAO, "non-existent");

        // then
        assertThat(result).isEmpty();
    }
}
