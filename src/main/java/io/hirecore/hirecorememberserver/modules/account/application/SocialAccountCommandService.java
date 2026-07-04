package io.hirecore.hirecorememberserver.modules.account.application;

import io.hirecore.hirecorememberserver.modules.account.application.port.out.SaveSocialAccountPort;
import io.hirecore.hirecorememberserver.modules.account.domain.SocialAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 소셜 계정 생성·상태 변경 (중복 검사는 DB 유니크 제약에 위임)
@Service
@RequiredArgsConstructor
public class SocialAccountCommandService {

    private final SaveSocialAccountPort socialAccountCommandPort;

    @Transactional
    public SocialAccount saveSocialAccount(SocialAccount socialAccount) {
        return socialAccountCommandPort.save(socialAccount);
    }
}
