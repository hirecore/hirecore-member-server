package io.hirecore.hirecorememberserver.modules.storage.application.port.in;

import io.hirecore.hirecorememberserver.modules.storage.domain.vo.ResourceKind;

public interface RecordImageStorageUsageUseCase {
    /**
     * 이미지 업로드로 인한 사용자 스토리지 사용량 증분과 변경 이력 적재를 수행합니다.
     *
     * @param memberAccountId 사용량이 귀속되는 회원 식별자
     * @param imageFileMetaId 사용량 변경을 발생시킨 이미지 식별자 (idempotency 키 산출과 변경 대상 식별에 사용)
     * @param resourceKind 사용량 변경 대상 리소스 종류
     * @param fileSizeBytes 증분 byte 수
     */
    void execute(Long memberAccountId, Long imageFileMetaId, ResourceKind resourceKind, Long fileSizeBytes);
}
