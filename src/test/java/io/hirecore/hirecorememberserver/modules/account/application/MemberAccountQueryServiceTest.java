package io.hirecore.hirecorememberserver.modules.account.application;

import com.navercorp.fixturemonkey.FixtureMonkey;
import io.hirecore.hirecorememberserver.modules.account.domain.MemberAccount;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.LoadMemberAccountPort;
import io.hirecore.hirecorememberserver.sharedkernel.infrastructure.persistence.exception.DataConsistencyException;
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

@DisplayName("QueryMemberAccountService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class MemberAccountQueryServiceTest {

    private final FixtureMonkey fm = FixtureMonkeyFactory.monkey();

    @InjectMocks
    private MemberAccountQueryService service;

    @Mock
    private LoadMemberAccountPort memberAccountQueryPort;

    @Nested
    @DisplayName("findById")
    class FindByIdTest {

        @Test
        @DisplayName("ID로 MemberAccount를 조회하면 포트의 결과를 반환한다")
        void should_return_member_account_from_port() {
            // given
            MemberAccount member = fm.giveMeOne(MemberAccount.class);
            Long id = member.getId();

            given(memberAccountQueryPort.findById(id)).willReturn(member);

            // when
            MemberAccount result = service.findById(id);

            // then
            assertThat(result).isSameAs(member);
        }

        @Test
        @DisplayName("포트에서 DataConsistencyException 발생 시 그대로 전파된다")
        void should_propagate_data_consistency_exception() {
            // given
            Long id = 999L;
            given(memberAccountQueryPort.findById(id))
                    .willThrow(new DataConsistencyException("MemberAccount(ID: 999)가 존재하지 않습니다."));

            // when & then
            assertThatThrownBy(() -> service.findById(id))
                    .isInstanceOf(DataConsistencyException.class)
                    .hasMessageContaining("999");
        }
    }

    @Nested
    @DisplayName("findByEmail")
    class FindByEmailTest {

        @Test
        @DisplayName("이메일로 MemberAccount를 조회하면 포트의 결과를 반환한다")
        void should_return_member_account_when_email_exists() {
            // given
            MemberAccount member = fm.giveMeOne(MemberAccount.class);
            String email = member.getEmail();

            given(memberAccountQueryPort.findByEmail(email)).willReturn(Optional.of(member));

            // when
            Optional<MemberAccount> result = service.findByEmail(email);

            // then
            assertThat(result).hasValueSatisfying(m -> assertThat(m).isSameAs(member));
        }

        @Test
        @DisplayName("이메일에 해당하는 회원이 없으면 빈 Optional을 반환한다")
        void should_return_empty_when_email_not_exists() {
            // given
            String email = "nonexistent@example.com";
            given(memberAccountQueryPort.findByEmail(email)).willReturn(Optional.empty());

            // when
            Optional<MemberAccount> result = service.findByEmail(email);

            // then
            assertThat(result).isEmpty();
        }
    }
}
