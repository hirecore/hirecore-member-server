package io.hirecore.hirecorememberserver.modules.account.application.usecase;

import io.hirecore.hirecorememberserver.modules.account.application.MemberAccountCommandService;
import io.hirecore.hirecorememberserver.modules.account.application.MemberAccountQueryService;
import io.hirecore.hirecorememberserver.modules.account.application.SocialAccountQueryService;
import io.hirecore.hirecorememberserver.modules.account.application.port.in.LoginSocialUserUseCase;
import io.hirecore.hirecorememberserver.modules.account.application.port.in.dto.request.SocialLoginCommand;
import io.hirecore.hirecorememberserver.modules.account.application.port.in.dto.response.PairTokenResponse;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.LoadSocialUserProfilePort;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.IssueTokenPort;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.dto.request.TokenClaimsRequest;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.dto.response.SocialUserProfileResult;
import io.hirecore.hirecorememberserver.modules.account.domain.MemberAccount;
import io.hirecore.hirecorememberserver.modules.account.application.mapper.SocialUserProfileInfoMapper;
import io.hirecore.hirecorememberserver.modules.account.domain.vo.MemberRole;
import io.hirecore.hirecorememberserver.modules.account.domain.vo.SocialUserProfileInfo;
import io.hirecore.hirecorememberserver.sharedkernel.domain.event.MemberRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 소셜 로그인 유스케이스 구현체.
 *
 * <p>외부 OAuth2 API 호출(네트워크 I/O)을 트랜잭션 밖에서 먼저 처리한 뒤,
 * {@link org.springframework.transaction.support.TransactionTemplate}으로 DB 작업 범위를 명시적으로 제어합니다.
 * 기존 회원이면 토큰을 즉시 발급하고, 신규 회원이면 가입 처리 후 토큰을 발급합니다.</p>
 */
@Service
@RequiredArgsConstructor
public class LoginSocialUserUseCaseImpl implements LoginSocialUserUseCase {

    private final SocialAccountQueryService socialAccountQueryService;
    private final MemberAccountQueryService memberAccountQueryService;
    private final MemberAccountCommandService memberAccountCommandService;
    private final LoadSocialUserProfilePort loadSocialUserProfilePort;
    private final IssueTokenPort tokenUtilsPort;
    private final TransactionTemplate transactionTemplate;
    private final SocialUserProfileInfoMapper socialUserProfileInfoMapper;

    @Override
    public PairTokenResponse execute(SocialLoginCommand socialLoginCommand) {
        SocialUserProfileResult profile = loadSocialUserProfilePort.load(socialLoginCommand.authorizationCode());
        return transactionTemplate.execute(status -> processLoginOrRegistration(profile));
    }

    private PairTokenResponse processLoginOrRegistration(SocialUserProfileResult profile) {
        return socialAccountQueryService
                .findExistingSocialAccount(profile.provider(), profile.providerId())
                .map(social -> memberAccountQueryService.findById(social.getMemberAccountId()))
                .map(this::issueToken)
                .orElseGet(() -> issueToken(registerNewMember(profile)));
    }

    private PairTokenResponse issueToken(MemberAccount memberAccount) {
        return tokenUtilsPort.issueTokenPair(new TokenClaimsRequest(
                memberAccount.getId(),
                memberAccount.getEmail(),
                memberAccount.getRole(),
                memberAccount.getTokenVersion()
        ));
    }

    /**
     * 신규 회원을 생성하여 저장합니다. 저장 완료 후 {@link MemberRegisteredEvent}가 발행됩니다.
     */
    private MemberAccount registerNewMember(SocialUserProfileResult profile) {
        SocialUserProfileInfo socialInfo = socialUserProfileInfoMapper.toSocialUserProfileInfo(profile);
        MemberAccount member = MemberAccount.createWithSocialLink(socialInfo.email(), MemberRole.USER, socialInfo);
        return memberAccountCommandService.saveMemberAccount(member);
    }
}
