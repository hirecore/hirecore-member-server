package io.hirecore.hirecorememberserver.modules.file.domain;

import com.github.f4b6a3.tsid.TsidCreator;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster;
import io.hirecore.hirecorememberserver.sharedkernel.domain.event.ImageOrphanedEvent;
import io.hirecore.hirecorememberserver.sharedkernel.domain.event.ImageUploadedEvent;
import io.hirecore.hirecorememberserver.modules.file.domain.exception.ImageFileMetaDomainException;
import io.hirecore.hirecorememberserver.modules.file.domain.exception.ImageFileMetaDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.file.domain.vo.*;
import io.hirecore.hirecorememberserver.sharedkernel.domain.AbstractDomainEventPublisher;
import io.hirecore.hirecorememberserver.sharedkernel.domain.DomainAggregateRoot;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.DomainType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Purpose;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

// 이미지 파일 메타데이터 애그리거트 루트 (위치/물리속성/업로드 상태 관리)
@Getter
public class ImageFileMeta extends AbstractDomainEventPublisher implements DomainAggregateRoot {
    private final Long id;
    private final Long memberAccountId;
    private final DomainType domainType;
    // DomainType 으로 목적이 명확해 세분화하지 않음
    private final Purpose purpose;
    private final String storageProvider;
    private final String bucketName;
    private final String objectKey;
    private final String originalFileName;
    private final MimeType mimeType;
    private final FileExtension fileExtension;
    private final Long fileSizeBytes;
    private final Integer width;
    private final Integer height;
    private UploadStatus uploadStatus;
    private Instant completedUploadAt;
    private Instant orphanedAt;
    private Instant completedDeleteAt;
    private final AuditingInfo auditingInfo;

    @Builder(access = AccessLevel.PUBLIC)
    private ImageFileMeta(
            Long id,
            Long memberAccountId,
            DomainType domainType,
            Purpose purpose,
            String storageProvider,
            String bucketName,
            String objectKey,
            String originalFileName,
            MimeType mimeType,
            FileExtension fileExtension,
            Long fileSizeBytes,
            Integer width,
            Integer height,
            UploadStatus uploadStatus,
            Instant completedUploadAt,
            Instant orphanedAt,
            Instant completedDeleteAt,
            AuditingInfo auditingInfo
    ) {
        ensureInvariants(
                id, memberAccountId, domainType, purpose, storageProvider,
                bucketName, objectKey, originalFileName, mimeType, fileExtension,
                fileSizeBytes, width, height, uploadStatus, auditingInfo
        );

        this.id = id;
        this.memberAccountId = memberAccountId;
        this.domainType = domainType;
        this.purpose = purpose;
        this.storageProvider = storageProvider;
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.originalFileName = originalFileName;
        this.mimeType = mimeType;
        this.fileExtension = fileExtension;
        this.fileSizeBytes = fileSizeBytes;
        this.width = width;
        this.height = height;
        this.uploadStatus = uploadStatus;
        this.completedUploadAt = completedUploadAt;
        this.orphanedAt = orphanedAt;
        this.completedDeleteAt = completedDeleteAt;
        this.auditingInfo = auditingInfo;
    }

    public static ImageFileMeta create(
            Long memberAccountId,
            DomainType domainType,
            Purpose purpose,
            String storageProvider,
            String bucketName,
            String objectKey,
            String originalFileName,
            MimeType mimeType,
            FileExtension fileExtension,
            Long fileSizeBytes,
            Integer width,
            Integer height
    ) {
        return ImageFileMeta.builder()
                .id(TsidCreator.getTsid().toLong())
                .memberAccountId(memberAccountId)
                .domainType(domainType)
                .purpose(purpose)
                .storageProvider(storageProvider)
                .bucketName(bucketName)
                .objectKey(objectKey)
                .originalFileName(originalFileName)
                .mimeType(mimeType)
                .fileExtension(fileExtension)
                .fileSizeBytes(fileSizeBytes)
                .width(width)
                .height(height)
                .uploadStatus(UploadStatus.PENDING)
                .auditingInfo(AuditingInfo.create())
                .build();
    }

    // 본 이미지를 UPLOADED 로 전이
    public void markUploaded() {
        if (this.uploadStatus == UploadStatus.UPLOADED) {
            return;
        }
        if (this.uploadStatus != UploadStatus.PENDING) {
            throw new ImageFileMetaDomainException(
                    ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.INVALID_UPLOAD_STATUS_TRANSITION
            );
        }

        this.uploadStatus = UploadStatus.UPLOADED;
        this.completedUploadAt = Instant.now();

        registerEvent(new ImageUploadedEvent(
                this.id,
                this.memberAccountId,
                this.domainType,
                this.purpose,
                this.fileSizeBytes,
                this.completedUploadAt
        ));
    }

    // 본 이미지를 ORPHANED 로 전이
    public void markOrphaned() {
        if (this.uploadStatus == UploadStatus.ORPHANED) {
            return;
        }
        if (this.uploadStatus != UploadStatus.UPLOADED) {
            throw new ImageFileMetaDomainException(
                    ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.INVALID_UPLOAD_STATUS_TRANSITION
            );
        }

        this.uploadStatus = UploadStatus.ORPHANED;
        this.orphanedAt = Instant.now();

        registerEvent(new ImageOrphanedEvent(
                this.id,
                this.memberAccountId,
                this.domainType,
                this.purpose,
                this.fileSizeBytes,
                this.orphanedAt
        ));
    }

    // 본 이미지를 DELETED 로 전이
    public void markDeleted() {
        if (this.uploadStatus == UploadStatus.DELETED) {
            return;
        }
        if (this.uploadStatus != UploadStatus.ORPHANED) {
            throw new ImageFileMetaDomainException(
                    ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.INVALID_UPLOAD_STATUS_TRANSITION
            );
        }

        this.uploadStatus = UploadStatus.DELETED;
        this.completedDeleteAt = Instant.now();
    }

    // 주어진 회원이 이 이미지 메타의 소유자인지 여부를 반환
    public boolean isOwnedBy(Long memberAccountId) {
        return this.memberAccountId.equals(memberAccountId);
    }

    // 업로드가 확정되었는지 확인
    public boolean isUploaded() {
        return this.uploadStatus == UploadStatus.UPLOADED;
    }

    // 고아 상태 여부 확인
    public boolean isOrphaned() {
        return this.uploadStatus == UploadStatus.ORPHANED;
    }

    private static void ensureInvariants(
            Long id,
            Long memberAccountId,
            DomainType domainType,
            Purpose purpose,
            String storageProvider,
            String bucketName,
            String objectKey,
            String originalFileName,
            MimeType mimeType,
            FileExtension fileExtension,
            Long fileSizeBytes,
            Integer width,
            Integer height,
            UploadStatus uploadStatus,
            AuditingInfo auditingInfo
    ) {
        AssertionUtils.notNull(
                id,
                ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.ID_MISSING,
                ImageFileMetaDomainException::new
        );
        AssertionUtils.notNull(
                memberAccountId,
                ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.MEMBER_ACCOUNT_ID_MISSING,
                ImageFileMetaDomainException::new
        );
        AssertionUtils.notNull(
                domainType,
                ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.DOMAIN_TYPE_MISSING,
                ImageFileMetaDomainException::new
        );
        AssertionUtils.notNull(
                purpose,
                ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.PURPOSE_MISSING,
                ImageFileMetaDomainException::new
        );
        AssertionUtils.notNull(
                storageProvider,
                ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.STORAGE_PROVIDER_MISSING,
                ImageFileMetaDomainException::new
        );
        AssertionUtils.notNull(
                bucketName,
                ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.BUCKET_NAME_MISSING,
                ImageFileMetaDomainException::new
        );
        AssertionUtils.notNull(
                objectKey,
                ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.OBJECT_KEY_MISSING,
                ImageFileMetaDomainException::new
        );
        AssertionUtils.notNull(
                originalFileName,
                ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.ORIGINAL_FILE_NAME_MISSING,
                ImageFileMetaDomainException::new
        );
        AssertionUtils.notNull(
                mimeType,
                ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.CONTENT_TYPE_MISSING,
                ImageFileMetaDomainException::new
        );
        AssertionUtils.notNull(
                fileExtension,
                ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.FILE_EXTENSION_MISSING,
                ImageFileMetaDomainException::new
        );
        AssertionUtils.notNull(
                fileSizeBytes,
                ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.FILE_SIZE_BYTES_MISSING,
                ImageFileMetaDomainException::new
        );
        AssertionUtils.notNull(
                width,
                ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.WIDTH_MISSING,
                ImageFileMetaDomainException::new
        );
        AssertionUtils.notNull(
                height,
                ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.HEIGHT_MISSING,
                ImageFileMetaDomainException::new
        );
        AssertionUtils.notNull(
                uploadStatus,
                ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.UPLOAD_STATUS_MISSING,
                ImageFileMetaDomainException::new
        );
        AssertionUtils.notNull(
                auditingInfo,
                SharedKernelExceptionCodeCluster.HiddenDetailResponse.AUDITING_MISSING,
                ImageFileMetaDomainException::new
        );
    }
}
