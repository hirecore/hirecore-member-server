package io.hirecore.hirecorememberserver.modules.storage.application.port.in;

import io.hirecore.hirecorememberserver.modules.storage.domain.vo.ResourceKind;

public interface ReleaseImageStorageUsageUseCase {
    // 이미지 회수 시 사용량 차감 + 이력 적재 (fileSizeBytes 는 양수)
    void execute(Long memberAccountId, Long imageFileMetaId, ResourceKind resourceKind, Long fileSizeBytes);
}
