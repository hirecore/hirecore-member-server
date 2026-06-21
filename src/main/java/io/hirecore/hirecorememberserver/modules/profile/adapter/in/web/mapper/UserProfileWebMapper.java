package io.hirecore.hirecorememberserver.modules.profile.adapter.in.web.mapper;

import io.hirecore.hirecorememberserver.modules.profile.adapter.in.web.dto.UserProfileSummaryApi;
import io.hirecore.hirecorememberserver.modules.profile.application.port.in.dto.response.UserProfileSummaryResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class UserProfileWebMapper {
    public abstract UserProfileSummaryApi.Response toApiResponse(UserProfileSummaryResponse response);
}
