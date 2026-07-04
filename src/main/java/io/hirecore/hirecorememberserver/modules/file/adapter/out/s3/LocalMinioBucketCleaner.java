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

// 로컬 재시작 시 MinIO 잔존 객체 정리 (DB create-drop 과 storage 불일치 방지)
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
