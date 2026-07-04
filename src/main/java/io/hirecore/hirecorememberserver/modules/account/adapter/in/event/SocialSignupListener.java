package io.hirecore.hirecorememberserver.modules.account.adapter.in.event;

import io.hirecore.hirecorememberserver.modules.account.application.SocialAccountCommandService;
import io.hirecore.hirecorememberserver.modules.account.domain.SocialAccount;
import io.hirecore.hirecorememberserver.sharedkernel.domain.event.MemberSocialSignedUpEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SocialSignupListener {

    private final SocialAccountCommandService socialAccountCommandService;

    @EventListener
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

        socialAccountCommandService.saveSocialAccount(socialAccount);
    }
}
