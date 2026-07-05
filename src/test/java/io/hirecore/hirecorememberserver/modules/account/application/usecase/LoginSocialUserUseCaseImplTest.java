package io.hirecore.hirecorememberserver.modules.account.application.usecase;

import com.navercorp.fixturemonkey.FixtureMonkey;
import io.hirecore.hirecorememberserver.modules.account.application.port.in.LoginSocialUserUseCase;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.FetchSocialUserProfilePort;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.IssueTokenPort;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.LoadMemberAccountPort;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.LoadSocialAccountPort;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.SaveMemberAccountPort;
import io.hirecore.hirecorememberserver.modules.account.application.mapper.SocialUserProfileInfoMapper;
import io.hirecore.hirecorememberserver.modules.account.domain.MemberAccount;
import io.hirecore.hirecorememberserver.modules.account.domain.SocialAccount;
import io.hirecore.hirecorememberserver.modules.account.domain.vo.SocialUserProfileInfo;
import io.hirecore.hirecorememberserver.support.FixtureMonkeyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.lenient;

@DisplayName("LoginSocialUserUseCaseImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class LoginSocialUserUseCaseImplTest {

    private final FixtureMonkey fm = FixtureMonkeyFactory.monkey();

    private LoginSocialUserUseCaseImpl loginSocialUserUseCaseImpl;

    @Mock private FetchSocialUserProfilePort fetchSocialUserProfilePort;
    @Mock private IssueTokenPort tokenUtilsPort;
    @Mock private LoadSocialAccountPort loadSocialAccountPort;
    @Mock private LoadMemberAccountPort loadMemberAccountPort;
    @Mock private SaveMemberAccountPort saveMemberAccountPort;
    @Mock private TransactionTemplate transactionTemplate;
    @Mock private SocialUserProfileInfoMapper socialUserProfileInfoMapper;

    @BeforeEach
    void setUp() {
        loginSocialUserUseCaseImpl = new LoginSocialUserUseCaseImpl(
                loadSocialAccountPort,
                loadMemberAccountPort,
                saveMemberAccountPort,
                fetchSocialUserProfilePort,
                tokenUtilsPort,
                transactionTemplate,
                socialUserProfileInfoMapper
        );

        lenient().when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(null);
        });
    }

    @Nested
    @DisplayName("기존 회원이 소셜 로그인하는 경우")
    class ExistingMemberLoginTest {

        @Test
        @DisplayName("기존 소셜 계정과 연결된 MemberAccount를 조회하여 토큰을 발급한다")
        void should_issue_token_for_existing_member() {
            // given
            LoginSocialUserUseCase.Command command = fm.giveMeOne(LoginSocialUserUseCase.Command.class);
            FetchSocialUserProfilePort.Result profile = fm.giveMeOne(FetchSocialUserProfilePort.Result.class);
            SocialAccount socialAccount = fm.giveMeOne(SocialAccount.class);
            MemberAccount member = fm.giveMeOne(MemberAccount.class);
            IssueTokenPort.Result expectedTokens = fm.giveMeOne(IssueTokenPort.Result.class);

            given(fetchSocialUserProfilePort.fetchByAuthorizationCode(command.authorizationCode())).willReturn(profile);
            given(loadSocialAccountPort.findByProviderAndProviderId(profile.provider(), profile.providerId()))
                    .willReturn(Optional.of(socialAccount));
            given(loadMemberAccountPort.findById(socialAccount.getMemberAccountId())).willReturn(member);
            given(tokenUtilsPort.issueTokenPair(any())).willReturn(expectedTokens);

            // when
            LoginSocialUserUseCase.Response result = loginSocialUserUseCaseImpl.execute(command);

            // then
            assertThat(result.accessToken()).isEqualTo(expectedTokens.accessToken());
            assertThat(result.refreshToken()).isEqualTo(expectedTokens.refreshToken());
            then(saveMemberAccountPort).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("신규 회원이 소셜로 최초 로그인하는 경우")
    class NewMemberLoginTest {

        @Test
        @DisplayName("MemberAccount를 생성·저장한 뒤 토큰을 발급한다")
        void should_create_member_and_save_then_issue_token() {
            // given
            LoginSocialUserUseCase.Command command = fm.giveMeOne(LoginSocialUserUseCase.Command.class);
            FetchSocialUserProfilePort.Result profile = fm.giveMeOne(FetchSocialUserProfilePort.Result.class);
            SocialUserProfileInfo socialInfo = fm.giveMeBuilder(SocialUserProfileInfo.class)
                    .set("emailAgreed", true)
                    .set("profileNicknameAgreed", true)
                    .sample();
            MemberAccount savedMember = fm.giveMeOne(MemberAccount.class);
            IssueTokenPort.Result expectedTokens = fm.giveMeOne(IssueTokenPort.Result.class);

            given(fetchSocialUserProfilePort.fetchByAuthorizationCode(command.authorizationCode())).willReturn(profile);
            given(loadSocialAccountPort.findByProviderAndProviderId(profile.provider(), profile.providerId()))
                    .willReturn(Optional.empty());
            given(socialUserProfileInfoMapper.toSocialUserProfileInfo(profile)).willReturn(socialInfo);
            given(saveMemberAccountPort.save(any(MemberAccount.class))).willReturn(savedMember);
            given(tokenUtilsPort.issueTokenPair(any())).willReturn(expectedTokens);

            // when
            LoginSocialUserUseCase.Response result = loginSocialUserUseCaseImpl.execute(command);

            // then
            assertThat(result.accessToken()).isEqualTo(expectedTokens.accessToken());
            assertThat(result.refreshToken()).isEqualTo(expectedTokens.refreshToken());
            then(saveMemberAccountPort).should().save(any(MemberAccount.class));
        }
    }

    @Nested
    @DisplayName("예외 전파")
    class ExceptionPropagation {

        @Test
        @DisplayName("소셜 프로필 로딩 실패 시 예외가 그대로 전파된다")
        void should_propagate_exception_when_profile_loading_fails() {
            // given
            LoginSocialUserUseCase.Command command = fm.giveMeOne(LoginSocialUserUseCase.Command.class);

            given(fetchSocialUserProfilePort.fetchByAuthorizationCode(command.authorizationCode()))
                    .willThrow(new RuntimeException("카카오 API 호출 실패"));

            // when & then
            assertThatThrownBy(() -> loginSocialUserUseCaseImpl.execute(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("카카오 API 호출 실패");

            then(loadSocialAccountPort).shouldHaveNoInteractions();
            then(tokenUtilsPort).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("기존 회원 조회 실패 시 예외가 그대로 전파된다")
        void should_propagate_exception_when_member_loading_fails() {
            // given
            LoginSocialUserUseCase.Command command = fm.giveMeOne(LoginSocialUserUseCase.Command.class);
            FetchSocialUserProfilePort.Result profile = fm.giveMeOne(FetchSocialUserProfilePort.Result.class);
            SocialAccount socialAccount = fm.giveMeOne(SocialAccount.class);

            given(fetchSocialUserProfilePort.fetchByAuthorizationCode(command.authorizationCode())).willReturn(profile);
            given(loadSocialAccountPort.findByProviderAndProviderId(profile.provider(), profile.providerId()))
                    .willReturn(Optional.of(socialAccount));
            given(loadMemberAccountPort.findById(socialAccount.getMemberAccountId()))
                    .willThrow(new RuntimeException("데이터 정합성 오류"));

            // when & then
            assertThatThrownBy(() -> loginSocialUserUseCaseImpl.execute(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("데이터 정합성 오류");

            then(tokenUtilsPort).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("토큰 발급 실패 시 예외가 그대로 전파된다")
        void should_propagate_exception_when_token_issuance_fails() {
            // given
            LoginSocialUserUseCase.Command command = fm.giveMeOne(LoginSocialUserUseCase.Command.class);
            FetchSocialUserProfilePort.Result profile = fm.giveMeOne(FetchSocialUserProfilePort.Result.class);
            SocialAccount socialAccount = fm.giveMeOne(SocialAccount.class);
            MemberAccount member = fm.giveMeOne(MemberAccount.class);

            given(fetchSocialUserProfilePort.fetchByAuthorizationCode(command.authorizationCode())).willReturn(profile);
            given(loadSocialAccountPort.findByProviderAndProviderId(profile.provider(), profile.providerId()))
                    .willReturn(Optional.of(socialAccount));
            given(loadMemberAccountPort.findById(socialAccount.getMemberAccountId())).willReturn(member);
            given(tokenUtilsPort.issueTokenPair(any()))
                    .willThrow(new RuntimeException("토큰 생성 실패"));

            // when & then
            assertThatThrownBy(() -> loginSocialUserUseCaseImpl.execute(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("토큰 생성 실패");
        }

        @Test
        @DisplayName("신규 회원 저장 실패 시 토큰 발급이 수행되지 않는다")
        void should_not_issue_token_when_member_save_fails() {
            // given
            LoginSocialUserUseCase.Command command = fm.giveMeOne(LoginSocialUserUseCase.Command.class);
            FetchSocialUserProfilePort.Result profile = fm.giveMeOne(FetchSocialUserProfilePort.Result.class);
            SocialUserProfileInfo socialInfo = fm.giveMeBuilder(SocialUserProfileInfo.class)
                    .set("emailAgreed", true)
                    .set("profileNicknameAgreed", true)
                    .sample();

            given(fetchSocialUserProfilePort.fetchByAuthorizationCode(command.authorizationCode())).willReturn(profile);
            given(loadSocialAccountPort.findByProviderAndProviderId(profile.provider(), profile.providerId()))
                    .willReturn(Optional.empty());
            given(socialUserProfileInfoMapper.toSocialUserProfileInfo(profile)).willReturn(socialInfo);
            given(saveMemberAccountPort.save(any(MemberAccount.class)))
                    .willThrow(new RuntimeException("DB 저장 실패"));

            // when & then
            assertThatThrownBy(() -> loginSocialUserUseCaseImpl.execute(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("DB 저장 실패");

            then(tokenUtilsPort).shouldHaveNoInteractions();
        }
    }
}
