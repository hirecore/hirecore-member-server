package io.hirecore.hirecorememberserver.modules.account.application;

import io.hirecore.hirecorememberserver.modules.account.application.port.out.SaveSocialAccountPort;
import io.hirecore.hirecorememberserver.modules.account.domain.SocialAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 소셜 계정의 생성 및 상태 변경을 담당하는 애플리케이션 서비스입니다.
 * <p>
 * CQRS 원칙을 준수하여 Query Port에 의존하지 않으며,
 * 중복 검사는 DB 유니크 제약에 위임합니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class SocialAccountCommandService {

    private final SaveSocialAccountPort socialAccountCommandPort;

    @Transactional
    public SocialAccount saveSocialAccount(SocialAccount socialAccount) {
        return socialAccountCommandPort.save(socialAccount);
    }
}
