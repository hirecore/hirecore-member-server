package io.hirecore.hirecorememberserver.modules.file.adapter.out.s3;

import io.hirecore.hirecorememberserver.modules.file.adapter.out.s3.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.List;

/**
 * 로컬 환경에서 애플리케이션 재시작 시 MinIO bucket 안에 잔존하는 객체들을 일괄 삭제합니다.
 *
 * <p>로컬 개발 사이클에서 이전 실행이 만든 업로드 파일이 다음 실행까지 남아 있어 발생하는
 * 데이터 불일치(예: DB 는 `create-drop` 으로 비워졌으나 storage 는 남아 있음)를 방지하기 위함입니다.</p>
 *
 * <p>dev/prod 환경에서는 본 빈이 등록되지 않으므로 데이터가 안전합니다.
 * bucket 이 아직 만들어지지 않았거나 MinIO 가 미가동인 경우 경고 로그만 남기고 진행을 계속합니다.</p>
 */
@Slf4j
@Component
@Profile({"local", "local-docker"})
@RequiredArgsConstructor
public class LocalMinioBucketCleaner {

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    @EventListener(ApplicationReadyEvent.class)
    public void clearBucketOnStartup() {
        String bucket = s3Properties.bucket();
        log.info("[LOCAL] MinIO bucket '{}' cleanup 시작", bucket);

        try {
            int deletedCount = deleteAllObjects(bucket);
            log.info("[LOCAL] MinIO bucket '{}' cleanup 완료 — {} 개 object 삭제", bucket, deletedCount);
        } catch (NoSuchBucketException e) {
            log.warn("[LOCAL] MinIO bucket '{}' 가 존재하지 않습니다. cleanup 건너뜀.", bucket);
        } catch (S3Exception e) {
            log.warn("[LOCAL] MinIO bucket cleanup 실패 (MinIO 미가동/네트워크 등): {}", e.getMessage());
        }
    }

    private int deleteAllObjects(String bucket) {
        int totalDeleted = 0;
        String continuationToken = null;

        do {
            ListObjectsV2Request.Builder listRequestBuilder = ListObjectsV2Request.builder()
                    .bucket(bucket);
            if (continuationToken != null) {
                listRequestBuilder.continuationToken(continuationToken);
            }

            ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequestBuilder.build());
            List<ObjectIdentifier> objectsToDelete = listResponse.contents().stream()
                    .map(o -> ObjectIdentifier.builder().key(o.key()).build())
                    .toList();

            if (!objectsToDelete.isEmpty()) {
                s3Client.deleteObjects(DeleteObjectsRequest.builder()
                        .bucket(bucket)
                        .delete(Delete.builder().objects(objectsToDelete).build())
                        .build());
                totalDeleted += objectsToDelete.size();
            }

            continuationToken = Boolean.TRUE.equals(listResponse.isTruncated())
                    ? listResponse.nextContinuationToken()
                    : null;
        } while (continuationToken != null);

        return totalDeleted;
    }
}
