package io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.sharedkernel.infrastructure.persistence.jpa.AuditingJpaInfo;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.entity.SocialAccountJpaEntity;
import io.hirecore.hirecorememberserver.modules.account.domain.SocialAccount;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.OAuth2Provider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SocialAccountJpaEntityMapper 단위 테스트")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SocialAccountJpaEntityMapperImpl.class)
class SocialAccountJpaEntityMapperTest {

    @Autowired
    private SocialAccountJpaEntityMapper mapper;

    private static final Long MEMBER_ID = 1L;
    private static final OAuth2Provider PROVIDER = OAuth2Provider.KAKAO;
    private static final String PROVIDER_ID = "kakao-12345";
    private static final String EMAIL = "user@kakao.com";

    @Nested
    @DisplayName("toJpaEntity() — Domain → JPA Entity")
    class ToJpaEntityTest {

        @Test
        @DisplayName("SocialAccount의 모든 필드가 SocialAccountJpaEntity로 정확하게 매핑된다")
        void should_map_all_fields_from_domain_to_jpa_entity() {
            // given
            Instant connectedAt = Instant.now();
            SocialAccount domain = SocialAccount.create(
                    MEMBER_ID, PROVIDER, PROVIDER_ID, EMAIL, connectedAt, true, true);

            // when
            SocialAccountJpaEntity entity = mapper.toJpaEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.getId());
            assertThat(entity.getMemberAccountId()).isEqualTo(MEMBER_ID);
            assertThat(entity.getProvider()).isEqualTo(PROVIDER);
            assertThat(entity.getProviderId()).isEqualTo(PROVIDER_ID);
            assertThat(entity.getEmail()).isEqualTo(EMAIL);
            assertThat(entity.getConnectedAt()).isEqualTo(connectedAt);
            assertThat(entity.getEmailAgreed()).isTrue();
            assertThat(entity.getProfileNicknameAgreed()).isTrue();
            assertThat(entity.getAuditingInfo().createdAt()).isEqualTo(domain.getAuditingInfo().createdAt());
        }

        @Test
        @DisplayName("동의 항목이 false인 경우에도 정확하게 매핑된다")
        void should_map_false_consent_values_correctly() {
            // given
            SocialAccount domain = SocialAccount.create(
                    MEMBER_ID, PROVIDER, PROVIDER_ID, EMAIL, Instant.now(), false, false);

            // when
            SocialAccountJpaEntity entity = mapper.toJpaEntity(domain);

            // then
            assertThat(entity.getEmailAgreed()).isFalse();
            assertThat(entity.getProfileNicknameAgreed()).isFalse();
        }
    }

    @Nested
    @DisplayName("toDomain() — JPA Entity → Domain")
    class ToDomainTest {

        @Test
        @DisplayName("SocialAccountJpaEntity의 모든 필드가 SocialAccount 도메인으로 정확하게 매핑된다")
        void should_map_all_fields_from_jpa_entity_to_domain() {
            // given
            Instant now = Instant.now();
            SocialAccountJpaEntity entity = SocialAccountJpaEntity.builder()
                    .id(9999L)
                    .memberAccountId(MEMBER_ID)
                    .provider(PROVIDER)
                    .providerId(PROVIDER_ID)
                    .email(EMAIL)
                    .connectedAt(now)
                    .emailAgreed(true)
                    .profileNicknameAgreed(false)
                    .auditingInfo(new AuditingJpaInfo(now, now))
                    .build();

            // when
            SocialAccount domain = mapper.toDomain(entity);

            // then
            assertThat(domain.getId()).isEqualTo(9999L);
            assertThat(domain.getMemberAccountId()).isEqualTo(MEMBER_ID);
            assertThat(domain.getProvider()).isEqualTo(PROVIDER);
            assertThat(domain.getProviderId()).isEqualTo(PROVIDER_ID);
            assertThat(domain.getEmail()).isEqualTo(EMAIL);
            assertThat(domain.getConnectedAt()).isEqualTo(now);
            assertThat(domain.getEmailAgreed()).isTrue();
            assertThat(domain.getProfileNicknameAgreed()).isFalse();
            assertThat(domain.getAuditingInfo().createdAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("toJpaEntity → toDomain 왕복 변환 시 핵심 필드가 보존된다")
        void should_preserve_all_fields_through_roundtrip_conversion() {
            // given
            Instant connectedAt = Instant.now();
            SocialAccount original = SocialAccount.create(
                    MEMBER_ID, PROVIDER, PROVIDER_ID, EMAIL, connectedAt, true, true);

            // when
            SocialAccountJpaEntity entity = mapper.toJpaEntity(original);
            SocialAccount restored = mapper.toDomain(entity);

            // then
            assertThat(restored.getId()).isEqualTo(original.getId());
            assertThat(restored.getMemberAccountId()).isEqualTo(original.getMemberAccountId());
            assertThat(restored.getProvider()).isEqualTo(original.getProvider());
            assertThat(restored.getProviderId()).isEqualTo(original.getProviderId());
            assertThat(restored.getEmail()).isEqualTo(original.getEmail());
            assertThat(restored.getConnectedAt()).isEqualTo(original.getConnectedAt());
            assertThat(restored.getEmailAgreed()).isEqualTo(original.getEmailAgreed());
            assertThat(restored.getProfileNicknameAgreed()).isEqualTo(original.getProfileNicknameAgreed());
        }
    }
}
