package io.hirecore.hirecorememberserver.modules.account.application.usecase;

import io.hirecore.hirecorememberserver.modules.account.application.port.in.LoginSocialUserUseCase;
import io.hirecore.hirecorememberserver.modules.account.application.port.in.dto.response.PairTokenResponse;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.FetchSocialUserProfilePort;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.IssueTokenPort;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.LoadMemberAccountPort;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.LoadSocialAccountPort;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.SaveMemberAccountPort;
import io.hirecore.hirecorememberserver.modules.account.domain.MemberAccount;
import io.hirecore.hirecorememberserver.modules.account.application.mapper.SocialUserProfileInfoMapper;
import io.hirecore.hirecorememberserver.modules.account.domain.vo.MemberRole;
import io.hirecore.hirecorememberserver.modules.account.domain.vo.SocialUserProfileInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

// 소셜 로그인 (외부 API 는 트랜잭션 밖에서, DB 작업만 TransactionTemplate 으로)
@Service
@RequiredArgsConstructor
public class LoginSocialUserUseCaseImpl implements LoginSocialUserUseCase {

    private final LoadSocialAccountPort loadSocialAccountPort;
    private final LoadMemberAccountPort loadMemberAccountPort;
    private final SaveMemberAccountPort saveMemberAccountPort;
    private final FetchSocialUserProfilePort fetchSocialUserProfilePort;
    private final IssueTokenPort tokenUtilsPort;
    private final TransactionTemplate transactionTemplate;
    private final SocialUserProfileInfoMapper socialUserProfileInfoMapper;

    @Override
    public PairTokenResponse execute(LoginSocialUserUseCase.Command command) {
        FetchSocialUserProfilePort.Result profile = fetchSocialUserProfilePort.fetchByAuthorizationCode(command.authorizationCode());
        return transactionTemplate.execute(status -> processLoginOrRegistration(profile));
    }

    private PairTokenResponse processLoginOrRegistration(FetchSocialUserProfilePort.Result profile) {
        return loadSocialAccountPort
                .findByProviderAndProviderId(profile.provider(), profile.providerId())
                .map(social -> loadMemberAccountPort.findById(social.getMemberAccountId()))
                .map(this::issueToken)
                .orElseGet(() -> issueToken(registerNewMember(profile)));
    }

    private PairTokenResponse issueToken(MemberAccount memberAccount) {
        return tokenUtilsPort.issueTokenPair(new IssueTokenPort.Request(
                memberAccount.getId(),
                memberAccount.getEmail(),
                memberAccount.getRole(),
                memberAccount.getTokenVersion()
        ));
    }


    private MemberAccount registerNewMember(FetchSocialUserProfilePort.Result profile) {
        SocialUserProfileInfo socialInfo = socialUserProfileInfoMapper.toSocialUserProfileInfo(profile);
        MemberAccount member = MemberAccount.createWithSocial(socialInfo.email(), MemberRole.USER, socialInfo);
        return saveMemberAccountPort.save(member);
    }
}
