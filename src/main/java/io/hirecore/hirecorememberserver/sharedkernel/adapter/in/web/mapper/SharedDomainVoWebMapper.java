package io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.mapper;

import io.hirecore.hirecorememberserver.common.config.GlobalMapStructConfig;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.CollaborationTypeApiValue;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.ExternalLinkApiValue;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.VisibilityApiValue;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ExternalLink;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import org.mapstruct.Mapper;

// todo: GlobalMapStructConfig의 의존성 해소하기
@Mapper(config = GlobalMapStructConfig.class)
public abstract class SharedDomainVoWebMapper {
    public abstract CollaborationType toCollaborationType(CollaborationTypeApiValue source);
    public abstract Visibility toVisibility(VisibilityApiValue source);
    public abstract ExternalLink toExternalLink(ExternalLinkApiValue source);

    public abstract CollaborationTypeApiValue toCollaborationTypeApiValue(CollaborationType source);
    public abstract VisibilityApiValue toVisibilityApiValue(Visibility source);
    public abstract ExternalLinkApiValue toExternalLinkApiValue(ExternalLink source);
}
