package io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.account;

import com.navercorp.fixturemonkey.FixtureMonkey;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.MemberAccountJpaQueryAdapter;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.entity.MemberAccountJpaEntity;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.mapper.MemberAccountJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.account.adapter.out.persistence.jpa.repository.MemberAccountJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.account.domain.MemberAccount;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.exception.DataConsistencyException;
import io.hirecore.hirecorememberserver.support.FixtureMonkeyFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@DisplayName("MemberAccountJpaQueryAdapter 단위 테스트")
@ExtendWith(MockitoExtension.class)
class MemberAccountJpaQueryAdapterTest {

    private final FixtureMonkey fm = FixtureMonkeyFactory.monkey();

    @InjectMocks
    private MemberAccountJpaQueryAdapter adapter;

    @Mock
    private MemberAccountJpaQueryRepository repository;
    @Mock
    private MemberAccountJpaEntityMapper mapper;

    @Nested
    @DisplayName("findById")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회하면 MemberAccount를 반환한다")
        void should_return_member_account_when_id_exists() {
            // given
            MemberAccountJpaEntity entity = fm.giveMeOne(MemberAccountJpaEntity.class);
            MemberAccount domain = fm.giveMeOne(MemberAccount.class);
            Long id = entity.getId();

            given(repository.findById(id)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            MemberAccount result = adapter.findById(id);

            // then
            assertThat(result).isSameAs(domain);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 DataConsistencyException이 발생한다")
        void should_throw_exception_when_id_not_found() {
            // given
            Long id = 999L;
            given(repository.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> adapter.findById(id))
                    .isInstanceOf(DataConsistencyException.class)
                    .hasMessageContaining("999")
                    .hasMessageContaining("MemberAccount");
        }
    }

    @Nested
    @DisplayName("findByEmail")
    class FindByEmailTest {

        @Test
        @DisplayName("존재하는 이메일로 조회하면 MemberAccount를 반환한다")
        void should_return_member_account_when_email_exists() {
            // given
            MemberAccountJpaEntity entity = fm.giveMeOne(MemberAccountJpaEntity.class);
            MemberAccount domain = fm.giveMeOne(MemberAccount.class);
            String email = entity.getEmail();

            given(repository.findByEmail(email)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<MemberAccount> result = adapter.findByEmail(email);

            // then
            assertThat(result).hasValueSatisfying(m -> assertThat(m).isSameAs(domain));
        }

        @Test
        @DisplayName("존재하지 않는 이메일로 조회하면 빈 Optional을 반환한다")
        void should_return_empty_when_email_not_found() {
            // given
            given(repository.findByEmail("unknown@test.com")).willReturn(Optional.empty());

            // when
            Optional<MemberAccount> result = adapter.findByEmail("unknown@test.com");

            // then
            assertThat(result).isEmpty();
        }
    }
}
