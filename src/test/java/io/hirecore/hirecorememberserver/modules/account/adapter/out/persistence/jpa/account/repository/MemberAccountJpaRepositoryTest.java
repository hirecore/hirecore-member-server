package io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.account.repository;

import com.navercorp.fixturemonkey.FixtureMonkey;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.JpaAuditingConfig;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.entity.MemberAccountJpaEntity;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.repository.MemberAccountJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.repository.MemberAccountJpaQueryRepository;
import io.hirecore.hirecorememberserver.support.FixtureMonkeyFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.Optional;

import org.hibernate.exception.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("MemberAccount JPA 리포지토리 통합 테스트")
@DataJpaTest
@Import(JpaAuditingConfig.class)
class MemberAccountJpaRepositoryTest {

    private final FixtureMonkey fm = FixtureMonkeyFactory.monkey();

    @Autowired
    private MemberAccountJpaCommandRepository commandRepository;

    @Autowired
    private MemberAccountJpaQueryRepository queryRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("MemberAccount를 저장하고 ID로 조회할 수 있다")
    void should_save_and_find_by_id() {
        // given
        Instant now = Instant.now();
        MemberAccountJpaEntity entity = fm.giveMeBuilder(MemberAccountJpaEntity.class)
                .set("auditingInfo.createdAt", now)
                .set("auditingInfo.updatedAt", now)
                .sample();

        // when
        MemberAccountJpaEntity saved = commandRepository.save(entity);
        Optional<MemberAccountJpaEntity> found = queryRepository.findById(saved.getId());

        // then
        assertThat(found).hasValueSatisfying(e -> {
            assertThat(e.getEmail()).isEqualTo(entity.getEmail());
            assertThat(e.getRole()).isEqualTo(entity.getRole());
            assertThat(e.getAuditingInfo().createdAt()).isNotNull();
            assertThat(e.getAuditingInfo().updatedAt()).isNotNull();
        });
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회하면 빈 Optional을 반환한다")
    void should_return_empty_when_id_not_found() {
        // when
        Optional<MemberAccountJpaEntity> found = queryRepository.findById(999L);

        // then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("패스워드가 null인 소셜 로그인 회원도 저장할 수 있다")
    void should_save_member_without_password() {
        // given
        MemberAccountJpaEntity entity = fm.giveMeBuilder(MemberAccountJpaEntity.class)
                .set("password", null)
                .sample();

        // when
        MemberAccountJpaEntity saved = commandRepository.save(entity);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPassword()).isNull();
    }

    @Test
    @DisplayName("동일한 email로 두 번 저장하면 유니크 제약 조건 위반이 발생한다")
    void should_throw_when_duplicate_email() {
        // given
        String duplicateEmail = "duplicate@test.com";
        MemberAccountJpaEntity first = fm.giveMeBuilder(MemberAccountJpaEntity.class)
                .set("email", duplicateEmail)
                .sample();
        commandRepository.save(first);
        entityManager.flush();
        entityManager.clear();

        MemberAccountJpaEntity second = fm.giveMeBuilder(MemberAccountJpaEntity.class)
                .set("email", duplicateEmail)
                .sample();

        // when & then
        assertThatThrownBy(() -> {
            commandRepository.save(second);
            entityManager.flush();
        }).isInstanceOf(ConstraintViolationException.class);
    }
}
