package io.hirecore.hirecorememberserver.modules.account.adapter.in.event;

import io.hirecore.hirecorememberserver.modules.account.application.SocialAccountCommandService;
import io.hirecore.hirecorememberserver.modules.account.domain.SocialAccount;
import io.hirecore.hirecorememberserver.sharedkernel.event.MemberRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * {@link MemberRegisteredEvent}를 구독하여 소셜 계정 생성·저장을 처리하는 이벤트 핸들러입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MemberRegisteredSocialAccountHandler {

    private final SocialAccountCommandService socialAccountCommandService;

    /**
     * 소셜 연동 이벤트를 수신하여 {@link io.hirecore.hirecorememberserver.modules.account.domain.SocialAccount}를 생성하고 저장합니다.
     *
     * <p>{@link org.springframework.context.event.EventListener @EventListener}를 사용하므로
     * 이벤트 발행 측과 동일한 트랜잭션 내에서 동기적으로 실행됩니다.
     * 따라서 이 핸들러에서 예외가 발생하면 이벤트 발행 측의 트랜잭션도 함께 롤백됩니다.</p>
     */
    @EventListener
    public void handleSocialAccountCreation(MemberRegisteredEvent event) {
        log.info("=== 소셜 계정 연동 이벤트 수신 - MemberAccount ID: {}, Provider: {} ===", event.memberAccountId(), event.provider());

        SocialAccount socialAccount = SocialAccount.create(
                event.memberAccountId(),
                event.provider(),
                event.providerId(),
                event.email(),
                event.socialConnectedAt(),
                event.socialEmailAgreed(),
                event.socialNicknameAgreed()
        );

        SocialAccount saved = socialAccountCommandService.saveSocialAccount(socialAccount);
        log.info("=== 소셜 계정 연동 이벤트 처리 완료 - SocialAccount ID: {}, MemberAccount ID: {}, SocialAccount Provider: {} ===",
                saved.getId(), saved.getMemberAccountId(), saved.getProvider());
    }
}
