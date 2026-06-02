package io.hirecore.hirecorememberserver.modules.file.application.port.out;

import java.util.Collection;
import java.util.Set;

public interface DeleteObjectsFromStoragePort {
    /**
     * 주어진 bucket 의 objectKeys 를 일괄 삭제하고, **성공한 objectKey 의 set 만** 반환합니다.
     *
     * <p>부분 실패가 가능합니다. 실패한 키는 결과에서 제외되어, 호출자가 영속에 반영하지 않고
     * 다음 주기에 재시도할 수 있도록 합니다.</p>
     *
     * <p>구현은 한 번의 배치 호출(예: S3 {@code DeleteObjects}, 1회 최대 1000 객체)을 사용하여
     * 네트워크 라운드트립을 최소화합니다.</p>
     */
    Set<String> deleteObjects(String bucketName, Collection<String> objectKeys);
}
