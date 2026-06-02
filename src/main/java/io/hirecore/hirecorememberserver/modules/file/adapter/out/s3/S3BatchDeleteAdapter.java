package io.hirecore.hirecorememberserver.modules.file.adapter.out.s3;

import io.hirecore.hirecorememberserver.modules.file.application.port.out.DeleteObjectsFromStoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Error;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * S3 {@code DeleteObjects} 배치 호출로 객체들을 일괄 삭제합니다.
 *
 * <p>한 호출에 최대 1000개 객체까지 처리할 수 있으므로 호출자가 사전 청크 분할을 책임집니다.
 * 부분 실패 시 응답의 {@code errors()} 에 있는 키는 결과에서 제외하여 호출자가 영속 반영
 * 대상에서 빼고 다음 주기에 재시도할 수 있게 합니다.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class S3BatchDeleteAdapter implements DeleteObjectsFromStoragePort {

    private final S3Client s3Client;

    @Override
    public Set<String> deleteObjects(String bucketName, Collection<String> objectKeys) {
        if (objectKeys == null || objectKeys.isEmpty()) {
            return Set.of();
        }

        Set<String> uniqueKeys = new LinkedHashSet<>(objectKeys);

        List<ObjectIdentifier> identifiers = uniqueKeys.stream()
                .map(key -> ObjectIdentifier.builder().key(key).build())
                .toList();

        DeleteObjectsResponse response = s3Client.deleteObjects(DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(Delete.builder().objects(identifiers).build())
                .build());

        Set<String> failedKeys = response.errors().stream()
                .map(S3Error::key)
                .collect(Collectors.toSet());

        if (!failedKeys.isEmpty()) {
            response.errors().forEach(error -> log.warn(
                    "S3 DeleteObjects 부분 실패: bucket={}, key={}, code={}, message={}",
                    bucketName, error.key(), error.code(), error.message()
            ));
        }

        return uniqueKeys.stream()
                .filter(key -> !failedKeys.contains(key))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
