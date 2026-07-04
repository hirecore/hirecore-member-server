package io.hirecore.hirecorememberserver.modules.profile.adapter.in.event;

import io.hirecore.hirecorememberserver.modules.profile.application.port.out.CheckProfilePublicCodePort;
import io.hirecore.hirecorememberserver.modules.profile.application.port.out.SaveProfilePort;
import io.hirecore.hirecorememberserver.modules.profile.domain.Profile;
import io.hirecore.hirecorememberserver.modules.profile.domain.vo.PublicCodeInfo;
import io.hirecore.hirecorememberserver.sharedkernel.domain.event.MemberAccountCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberSignupListener {

    private final SaveProfilePort saveProfilePort;
    private final CheckProfilePublicCodePort checkProfilePublicCodePort;

    @EventListener
    public void handleMemberSignup(MemberAccountCreatedEvent event) {
        String nickname = event.email().split("@")[0];
        PublicCodeInfo publicCodeInfo;
        do{
            publicCodeInfo = PublicCodeInfo.generate();

        } while(checkProfilePublicCodePort.existsByPublicCode(publicCodeInfo.publicCode()));


        Profile profile = Profile.createUserProfile(
                event.memberAccountId(),
                publicCodeInfo,
                nickname,
                "",
                null
        );

        saveProfilePort.save(profile);
        log.info("=== 프로필 연동 이벤트 처리 완료 - Profile ID: {}, MemberAccount ID: {}, ===",
                profile.getId(), profile.getMemberAccountId()
        );
    }
}
