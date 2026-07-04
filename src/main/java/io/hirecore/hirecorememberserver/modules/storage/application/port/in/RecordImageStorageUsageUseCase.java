package io.hirecore.hirecorememberserver.modules.storage.application.port.in;

import io.hirecore.hirecorememberserver.modules.storage.domain.vo.ResourceKind;

public interface RecordImageStorageUsageUseCase {
    // 이미지 업로드 시 사용량 증분 + 이력 적재
    void execute(Long memberAccountId, Long imageFileMetaId, ResourceKind resourceKind, Long fileSizeBytes);
}
