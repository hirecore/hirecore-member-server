package io.hirecore.hirecorememberserver.modules.file.application.port.out;

import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;

import java.util.List;

public interface UpdateImageFileMetaPort {
    /**
     * 전달받은 도메인들의 누적 도메인 이벤트를 영속 엔티티로 전이한 뒤,
     * 영속 엔티티에 UPLOADED 상태 전이를 적용합니다. {@code Repository.save()} 호출을 통해
     * Spring Data의 {@code @DomainEvents}가 발행됩니다.
     *
     * <p>도메인의 임의 상태를 setter처럼 복사하지 않으며, 항상 고정된 UPLOADED 전이만 수행합니다.
     * 도메인 객체는 이벤트 전이 매개체로 사용되고, 검증과 mutation은 호출자의 책임입니다.</p>
     */
    void markAllAsUploaded(List<ImageFileMeta> imageFileMetas);
}
