package io.hirecore.hirecorememberserver.modules.file.domain;

import com.github.f4b6a3.tsid.TsidCreator;
import io.hirecore.hirecorememberserver.sharedkernel.domain.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.sharedkernel.domain.exception.SharedKernelExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.file.domain.exception.ImageFileMetaDomainException;
import io.hirecore.hirecorememberserver.modules.file.domain.exception.ImageFileMetaDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.file.domain.vo.*;
import io.hirecore.hirecorememberserver.sharedkernel.domain.AbstractDomainEventPublisher;
import io.hirecore.hirecorememberserver.sharedkernel.domain.DomainAggregateRoot;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * 이미지 파일의 메타데이터를 관리하는 도메인 집합 루트(Aggregate Root)입니다.
 *
 * <p>이 객체는 스토리지에 업로드된 이미지 파일의 식별 정보, 저장 위치, 물리적 속성(크기, 해상도),
 * 업로드 상태 등 파일과 관련된 모든 메타데이터를 캡슐화하여 관리합니다.</p>
 *
 * <p>주요 책임은 다음과 같습니다.</p>
 * <ul>
 *     <li>이미지 파일의 스토리지 위치 정보 관리 (provider, bucket, objectKey)</li>
 *     <li>파일의 물리적 속성 관리 (파일 크기, 가로/세로 해상도)</li>
 *     <li>파일의 용도 및 소속 도메인 정보 관리</li>
 *     <li>업로드 및 삭제 상태 추적</li>
 *     <li>파일 메타데이터 무결성 보장</li>
 * </ul>
 *
 * <p>주요 활용 목적은 다음과 같습니다.</p>
 * <ul>
 *     <li>업로드된 이미지 파일의 전체 생명주기 관리</li>
 *     <li>스토리지 사용량 계산 및 추적</li>
 *     <li>파일 접근을 위한 위치 정보 제공</li>
 *     <li>고아(orphaned) 파일 식별 및 정리</li>
 *     <li>도메인별 이미지 파일 분류 및 관리</li>
 * </ul>
 */
@Getter
public class ImageFileMeta extends AbstractDomainEventPublisher implements DomainAggregateRoot {
    private final Long id;
    private final Long memberAccountId;
    /** 파일이 소속된 도메인 유형*/
    private final DomainType domainType;
    /** 헤딩 파일이 사용되는 목적
     *   세분화 하지 않은 이유: DomainType이 field로 위치해 간략한 정보로도 사용되는 목적이 명확함
     * */
    private final Purpose purpose;
    /** 스토리지 제공 서비스명 */
    private final String storageProvider;
    private final String bucketName;
    private final String objectKey;
    private final String originalFileName;
    /** MIME TYPE */
    private final MimeType mimeType;
    /** 파일의 확장자 */
    private final FileExtension fileExtension;
    private final Long fileSizeBytes;
    private final Integer width;
    private final Integer height;
    private final UploadStatus uploadStatus;
    private final Instant completedUploadAt;
    private final Instant completedDeleteAt;
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
