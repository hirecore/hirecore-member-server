package io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.account.repository;

import com.navercorp.fixturemonkey.FixtureMonkey;
import io.hirecore.hirecorememberserver.sharedkernel.infrastructure.persistence.jpa.JpaAuditingConfig;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.entity.MemberAccountJpaEntity;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.entity.SocialAccountJpaEntity;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.repository.MemberAccountJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.repository.SocialAccountJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.repository.SocialAccountJpaQueryRepository;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.OAuth2Provider;
import io.hirecore.hirecorememberserver.support.FixtureMonkeyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SocialAccount JPA 리포지토리 통합 테스트")
@DataJpaTest
@Import(JpaAuditingConfig.class)
class UserKakaoAccountJpaRepositoryTest {

    private final FixtureMonkey fm = FixtureMonkeyFactory.monkey();

    @Autowired
    private SocialAccountJpaCommandRepository commandRepository;

    @Autowired
    private SocialAccountJpaQueryRepository queryRepository;

    @Autowired
    private MemberAccountJpaCommandRepository memberAccountRepository;

    private Long savedMemberAccountId;

    @BeforeEach
    void setUp() {
        MemberAccountJpaEntity memberEntity = fm.giveMeBuilder(MemberAccountJpaEntity.class)
                .set("email", "kakao-user@test.com")
                .sample();
        savedMemberAccountId = memberAccountRepository.save(memberEntity).getId();
    }

    @Test
    @DisplayName("SocialAccount를 저장하고 provider와 providerId로 조회할 수 있다")
    void should_save_and_find_by_provider_and_provider_id() {
        // given
        String providerId = "test-provider-id";
        SocialAccountJpaEntity entity = fm.giveMeBuilder(SocialAccountJpaEntity.class)
                .set("memberAccountId", savedMemberAccountId)
                .set("provider", OAuth2Provider.KAKAO)
                .set("providerId", providerId)
                .sample();

        // when
        commandRepository.save(entity);
        Optional<SocialAccountJpaEntity> found = queryRepository.findByProviderAndProviderId(
                OAuth2Provider.KAKAO, providerId);

        // then
        assertThat(found).hasValueSatisfying(e -> {
            assertThat(e.getProvider()).isEqualTo(OAuth2Provider.KAKAO);
            assertThat(e.getProviderId()).isEqualTo(providerId);
            assertThat(e.getMemberAccountId()).isEqualTo(savedMemberAccountId);
            assertThat(e.getAuditingInfo().createdAt()).isNotNull();
        });
    }

    @Test
    @DisplayName("존재하지 않는 provider와 providerId로 조회하면 빈 Optional을 반환한다")
    void should_return_empty_when_provider_and_provider_id_not_found() {
        // when
        Optional<SocialAccountJpaEntity> found = queryRepository.findByProviderAndProviderId(OAuth2Provider.KAKAO, "999999");

        // then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("memberAccountId를 통해 회원 계정과의 참조 관계를 확인할 수 있다")
    void should_store_member_account_id_reference() {
        // given
        Instant connectedAt = Instant.parse("2025-06-15T10:30:00Z");
        SocialAccountJpaEntity entity = fm.giveMeBuilder(SocialAccountJpaEntity.class)
                .set("memberAccountId", savedMemberAccountId)
                .set("provider", OAuth2Provider.KAKAO)
                .set("connectedAt", connectedAt)
                .set("emailAgreed", false)
                .set("profileNicknameAgreed", true)
                .sample();

        // when
        SocialAccountJpaEntity saved = commandRepository.save(entity);

        // then
        assertThat(saved).satisfies(s -> {
            assertThat(s.getProvider()).isEqualTo(OAuth2Provider.KAKAO);
            assertThat(s.getMemberAccountId()).isEqualTo(savedMemberAccountId);
            assertThat(s.getEmailAgreed()).isFalse();
            assertThat(s.getConnectedAt()).isEqualTo(connectedAt);
        });
    }
}
