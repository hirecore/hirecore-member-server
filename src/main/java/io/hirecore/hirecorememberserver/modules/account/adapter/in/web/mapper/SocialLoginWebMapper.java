package io.hirecore.hirecorememberserver.modules.account.adapter.in.web.mapper;

import io.hirecore.hirecorememberserver.modules.account.adapter.in.web.dto.request.SocialLoginApiRequest;
import io.hirecore.hirecorememberserver.modules.account.application.port.in.dto.request.SocialLoginCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public abstract class SocialLoginWebMapper {
    @Mappings({
            @Mapping(target = "provider", source = "provider"),
            @Mapping(target = "authorizationCode", source = "socialLoginApiRequest.authorizationCode")
    })
    public abstract SocialLoginCommand mapToSocialLoginCommand(String provider, SocialLoginApiRequest socialLoginApiRequest);
}
