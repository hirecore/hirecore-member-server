package io.hirecore.hirecorememberserver.modules.account.application.mapper;

import io.hirecore.hirecorememberserver.modules.account.application.port.out.dto.response.SocialUserProfileResult;
import io.hirecore.hirecorememberserver.modules.account.domain.vo.SocialUserProfileInfo;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.OAuth2Provider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SocialUserProfileInfoMapper 단위 테스트")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SocialUserProfileInfoMapperImpl.class)
class SocialUserProfileDetailInfoMapperTest {

    @Autowired
    private SocialUserProfileInfoMapper mapper;

    @Test
    @DisplayName("SocialUserProfileResult의 공통 필드가 SocialUserProfileInfo로 정확하게 매핑된다")
    void should_map_common_fields_correctly() {
        // given
        Instant connectedAt = Instant.now();
        SocialUserProfileResult result = new SocialUserProfileResult(
                OAuth2Provider.KAKAO,
                "kakao-12345",
                "user@kakao.com",
                "홍길동",       // nickname — SocialUserProfileInfo에 없는 필드
                connectedAt,
                true,
                true
        );

        // when
        SocialUserProfileInfo info = mapper.mapToSocialUserProfileInfo(result);

        // then
        assertThat(info.provider()).isEqualTo(OAuth2Provider.KAKAO);
        assertThat(info.providerId()).isEqualTo("kakao-12345");
        assertThat(info.email()).isEqualTo("user@kakao.com");
        assertThat(info.connectedAt()).isEqualTo(connectedAt);
        assertThat(info.emailAgreed()).isTrue();
        assertThat(info.profileNicknameAgreed()).isTrue();
    }

    @Test
    @DisplayName("source의 nickname 필드는 target에 존재하지 않으므로 무시된다")
    void should_drop_nickname_field_from_source() {
        // given — SocialUserProfileResult는 nickname을 가지지만, SocialUserProfileInfo는 갖지 않음
        SocialUserProfileResult result = new SocialUserProfileResult(
                OAuth2Provider.KAKAO, "pid", "e@test.com", "anyNickname",
                Instant.now(), true, true
        );

        // when
        SocialUserProfileInfo info = mapper.mapToSocialUserProfileInfo(result);

        // then — SocialUserProfileInfo는 nickname 필드 자체가 없으므로 레코드 컴포넌트 6개만 존재
        assertThat(SocialUserProfileInfo.class.getRecordComponents()).hasSize(6);
        assertThat(info).isNotNull();
    }

    @Test
    @DisplayName("emailAgreed=false, profileNicknameAgreed=false인 경우에도 정확하게 매핑된다")
    void should_map_false_consent_values_correctly() {
        // given
        SocialUserProfileResult result = new SocialUserProfileResult(
                OAuth2Provider.KAKAO, "pid", "e@test.com", "nick",
                Instant.now(), false, false
        );

        // when
        SocialUserProfileInfo info = mapper.mapToSocialUserProfileInfo(result);

        // then
        assertThat(info.emailAgreed()).isFalse();
        assertThat(info.profileNicknameAgreed()).isFalse();
    }
}
