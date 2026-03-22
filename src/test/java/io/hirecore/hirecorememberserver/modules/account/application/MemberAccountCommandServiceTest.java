package io.hirecore.hirecorememberserver.modules.account.application;

import com.navercorp.fixturemonkey.FixtureMonkey;
import io.hirecore.hirecorememberserver.modules.account.domain.MemberAccount;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.MemberAccountCommandPort;
import io.hirecore.hirecorememberserver.support.FixtureMonkeyFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("CommandMemberAccountService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class MemberAccountCommandServiceTest {

    private final FixtureMonkey fm = FixtureMonkeyFactory.monkey();

    @InjectMocks
    private MemberAccountCommandService service;

    @Mock
    private MemberAccountCommandPort memberAccountCommandPort;

    @Test
    @DisplayName("MemberAccount를 포트를 통해 저장하고 결과를 반환한다")
    void should_save_member_account_via_port() {
        // given
        MemberAccount memberToSave = fm.giveMeOne(MemberAccount.class);
        MemberAccount savedMember = fm.giveMeOne(MemberAccount.class);

        given(memberAccountCommandPort.save(memberToSave)).willReturn(savedMember);

        // when
        MemberAccount result = service.saveMemberAccount(memberToSave);

        // then
        assertThat(result).isSameAs(savedMember);
        then(memberAccountCommandPort).should().save(memberToSave);
    }

    @Test
    @DisplayName("포트에서 예외 발생 시 그대로 전파된다")
    void should_propagate_exception_from_port() {
        // given
        MemberAccount memberToSave = fm.giveMeOne(MemberAccount.class);

        given(memberAccountCommandPort.save(memberToSave))
                .willThrow(new RuntimeException("DB 저장 실패"));

        // when & then
        assertThatThrownBy(() -> service.saveMemberAccount(memberToSave))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB 저장 실패");
    }
}
