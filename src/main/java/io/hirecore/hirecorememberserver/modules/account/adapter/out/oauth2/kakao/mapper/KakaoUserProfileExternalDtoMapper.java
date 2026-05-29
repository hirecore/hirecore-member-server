package io.hirecore.hirecorememberserver.modules.account.adapter.out.oauth2.kakao.mapper;

import io.hirecore.hirecorememberserver.modules.account.adapter.out.oauth2.kakao.dto.result.KakaoUserProfileExternalResult;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.dto.result.SocialUserProfileResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public abstract class KakaoUserProfileExternalDtoMapper {

    /**
     * 카카오 동의항목의 "동의가 필요한가?" 기준을 우리 시스템의 "동의했는가?" 기준으로 역치환합니다.
     * (예: {@code emailNeedsAgreement = true} → {@code emailAgreed = false})
     */
    @Mappings({
            @Mapping(target = "provider", constant = "KAKAO"),
            @Mapping(target = "providerId", source = "id"),
            @Mapping(target = "connectedAt", source = "connectedAt"),
            @Mapping(target = "email", source = "kakaoAccount.email"),
            @Mapping(target = "nickname", source = "kakaoAccount.profile.nickname"),
            @Mapping(target = "emailAgreed", expression = "java(!request.kakaoAccount().emailNeedsAgreement())"),
            @Mapping(target = "profileNicknameAgreed", expression = "java(!request.kakaoAccount().profileNicknameNeedsAgreement())"),
    })
    public abstract SocialUserProfileResult toSocialUserProfileResult(KakaoUserProfileExternalResult request);
}
