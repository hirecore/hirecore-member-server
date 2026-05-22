package io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.mapper;

import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AuditingJpaInfo;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.entity.MemberAccountJpaEntity;
import io.hirecore.hirecorememberserver.modules.account.domain.MemberAccount;
import io.hirecore.hirecorememberserver.modules.account.domain.vo.MemberRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MemberAccountJpaEntityMapper 단위 테스트")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MemberAccountJpaEntityMapperImpl.class)
class MemberAccountJpaEntityMapperTest {

    @Autowired
    private MemberAccountJpaEntityMapper mapper;

    @Nested
    @DisplayName("toJpaEntity() — Domain → JPA Entity")
    class ToJpaEntityTest {

        @Test
        @DisplayName("MemberAccount의 모든 필드가 MemberAccountJpaEntity로 정확하게 매핑된다")
        void should_map_all_fields_from_domain_to_jpa_entity() {
            // given
            MemberAccount domain = MemberAccount.create("user@test.com", MemberRole.USER);

            // when
            MemberAccountJpaEntity entity = mapper.toJpaEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.getId());
            assertThat(entity.getEmail()).isEqualTo(domain.getEmail());
            assertThat(entity.getRole()).isEqualTo(domain.getRole());
            assertThat(entity.getPassword()).isEqualTo(domain.getPassword());
            assertThat(entity.getAuditingInfo().createdAt()).isEqualTo(domain.getAuditingInfo().createdAt());
            assertThat(entity.getAuditingInfo().updatedAt()).isEqualTo(domain.getAuditingInfo().updatedAt());
        }

        @Test
        @DisplayName("password가 null인 소셜 회원도 정상적으로 매핑된다")
        void should_map_domain_with_null_password() {
            // given — MemberAccount.create()는 password를 null로 생성
            MemberAccount domain = MemberAccount.create("social@test.com", MemberRole.USER);

            // when
            MemberAccountJpaEntity entity = mapper.toJpaEntity(domain);

            // then
            assertThat(entity.getPassword()).isNull();
            assertThat(entity.getEmail()).isEqualTo("social@test.com");
        }
    }

    @Nested
    @DisplayName("toDomain() — JPA Entity → Domain")
    class ToDomainTest {

        @Test
        @DisplayName("MemberAccountJpaEntity의 모든 필드가 MemberAccount 도메인으로 정확하게 매핑된다")
        void should_map_all_fields_from_jpa_entity_to_domain() {
            // given
            Instant now = Instant.now();
            MemberAccountJpaEntity entity = MemberAccountJpaEntity.builder()
                    .id(1000L)
                    .email("user@test.com")
                    .role(MemberRole.ADMIN)
                    .auditingInfo(new AuditingJpaInfo(now, now))
                    .build();

            // when
            MemberAccount domain = mapper.toDomain(entity);

            // then
            assertThat(domain.getId()).isEqualTo(1000L);
            assertThat(domain.getEmail()).isEqualTo("user@test.com");
            assertThat(domain.getRole()).isEqualTo(MemberRole.ADMIN);
            assertThat(domain.getAuditingInfo().createdAt()).isEqualTo(now);
            assertThat(domain.getAuditingInfo().updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("toJpaEntity → toDomain 왕복 변환 시 핵심 필드가 보존된다")
        void should_preserve_core_fields_through_roundtrip_conversion() {
            // given
            MemberAccount original = MemberAccount.create("roundtrip@test.com", MemberRole.USER);

            // when
            MemberAccountJpaEntity entity = mapper.toJpaEntity(original);
            MemberAccount restored = mapper.toDomain(entity);

            // then — id, email, role, auditingInfo 보존 검증
            assertThat(restored.getId()).isEqualTo(original.getId());
            assertThat(restored.getEmail()).isEqualTo(original.getEmail());
            assertThat(restored.getRole()).isEqualTo(original.getRole());
            assertThat(restored.getAuditingInfo().createdAt())
                    .isEqualTo(original.getAuditingInfo().createdAt());
        }
    }
}
