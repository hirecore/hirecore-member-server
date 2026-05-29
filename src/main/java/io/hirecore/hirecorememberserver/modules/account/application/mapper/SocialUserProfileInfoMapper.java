package io.hirecore.hirecorememberserver.modules.account.application.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.modules.account.application.port.out.dto.result.SocialUserProfileResult;
import io.hirecore.hirecorememberserver.modules.account.domain.vo.SocialUserProfileInfo;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapStructConfig.class)
public abstract class SocialUserProfileInfoMapper {
    public abstract SocialUserProfileInfo toSocialUserProfileInfo(SocialUserProfileResult result);
}
