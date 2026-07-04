package io.hirecore.hirecorememberserver.modules.file.application.port.out;

import java.util.Collection;
import java.util.Set;

public interface DeleteObjectsFromStoragePort {
    // 일괄 삭제 후 성공한 objectKey 만 반환 (부분 실패분은 다음 주기 재시도)
    Set<String> deleteObjects(String bucketName, Collection<String> objectKeys);
}
