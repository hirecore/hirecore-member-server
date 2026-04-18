package io.hirecore.hirecorememberserver.modules.profile.adapter.in.event;

import io.hirecore.hirecorememberserver.modules.profile.application.ProfileCommandService;
import io.hirecore.hirecorememberserver.modules.profile.application.ProfileQueryService;
import io.hirecore.hirecorememberserver.modules.profile.domain.Profile;
import io.hirecore.hirecorememberserver.modules.profile.domain.vo.PublicCodeInfo;
import io.hirecore.hirecorememberserver.sharedkernel.event.MemberRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * {@link MemberRegisteredEvent}를 구독하여 신규 회원의 프로필을 생성·저장하는 이벤트 핸들러입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MemberRegisteredProfileHandler {

    private final ProfileCommandService profileCommandService;
    private final ProfileQueryService profileQueryService;

    /**
     * 회원 등록 이벤트를 수신하여 {@link Profile}을 생성하고 저장합니다.
     */
    @EventListener
    public void handleUserProfileCreation(MemberRegisteredEvent event) {
        log.info("=== 프로필 연동 이벤트 수신 - MemberAccount ID: {}, Provider: {} ===", event.memberAccountId(), event.provider());

        String nickname = event.email().split("@")[0];
        PublicCodeInfo publicCodeInfo;
        do{
            publicCodeInfo = PublicCodeInfo.generate();

        } while(profileQueryService.existsByPublicCode(publicCodeInfo.publicCode()));


        Profile profile = Profile.createUserProfile(
                event.memberAccountId(),
                publicCodeInfo,
                nickname,
                "",
                null
        );

        profileCommandService.save(profile);
        log.info("=== 프로필 연동 이벤트 처리 완료 - Profile ID: {}, MemberAccount ID: {}, ===",
                profile.getId(), profile.getMemberAccountId()
        );
    }
}
