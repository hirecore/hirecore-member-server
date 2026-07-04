package io.hirecore.hirecorememberserver.modules.account.adapter.in.event;

import io.hirecore.hirecorememberserver.modules.account.application.port.out.SaveSocialAccountPort;
import io.hirecore.hirecorememberserver.modules.account.domain.SocialAccount;
import io.hirecore.hirecorememberserver.sharedkernel.domain.event.MemberSocialSignedUpEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SocialSignupListener {

    private final SaveSocialAccountPort saveSocialAccountPort;

    @EventListener
    @Transactional
    public void handleSocialSignup(MemberSocialSignedUpEvent event) {
        SocialAccount socialAccount = SocialAccount.create(
                event.memberAccountId(),
                event.provider(),
                event.providerId(),
                event.email(),
                event.socialConnectedAt(),
                event.socialEmailAgreed(),
                event.socialNicknameAgreed()
        );

        saveSocialAccountPort.save(socialAccount);
    }
}
