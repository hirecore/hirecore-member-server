package io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.entity;

import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AbstractPersistableAggregateRoot;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AuditingJpaInfo;
import io.hirecore.hirecorememberserver.modules.file.domain.vo.*;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.DomainType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Purpose;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.Instant;

@Entity
@Table(name = "image_file_metas")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class ImageFileMetaJpaEntity extends AbstractPersistableAggregateRoot<Long> {
    @Id
    private Long id;

    @Comment("회원 계정 ID")
    @Column(name = "member_account_id", nullable = false)
    private Long memberAccountId;

    @Comment("파일이 소속된 도메인 유형")
    @Enumerated(EnumType.STRING)
    @Column(name = "domain_type", nullable = false, columnDefinition = "VARCHAR(50)")
    private DomainType domainType;

    @Comment("파일이 사용되는 목적")
    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", nullable = false, columnDefinition = "VARCHAR(30)")
    private Purpose purpose;

    @Comment("스토리지 제공 서비스명")
    @Column(name = "storage_provider", nullable = false, columnDefinition = "VARCHAR(30)")
    private String storageProvider;

    @Comment("버킷명")
    @Column(name = "bucket_name", nullable = false, columnDefinition = "VARCHAR(250)")
    private String bucketName;

    @Comment("오브젝트 키")
    @Column(name = "object_key", nullable = false, columnDefinition = "VARCHAR(100)")
    private String objectKey;

    @Comment("원본 파일명")
    @Column(name = "original_file_name", nullable = false, columnDefinition = "VARCHAR(250)")
    private String originalFileName;

    @Comment("MIME 타입")
    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false, columnDefinition = "VARCHAR(50)")
    private MimeType mimeType;

    @Comment("파일 확장자")
    @Enumerated(EnumType.STRING)
    @Column(name = "file_extension", nullable = false, columnDefinition = "VARCHAR(30)")
    private FileExtension fileExtension;

    @Comment("파일 크기 (바이트)")
    @Column(name = "file_size_bytes", nullable = false)
    private Long fileSizeBytes;

    @Comment("이미지 가로 해상도")
    @Column(name = "width", nullable = false)
    private Integer width;

    @Comment("이미지 세로 해상도")
    @Column(name = "height", nullable = false)
    private Integer height;

    @Comment("업로드 상태")
    @Enumerated(EnumType.STRING)
    @Column(name = "upload_status", nullable = false, columnDefinition = "VARCHAR(30)")
    private UploadStatus uploadStatus;

    @Comment("업로드 완료 시각")
    @Column(name = "completed_upload_at", columnDefinition = "DATETIME(6)")
    private Instant completedUploadAt;

    @Comment("ORPHANED 전이 시각")
    @Column(name = "orphaned_at", columnDefinition = "DATETIME(6)")
    private Instant orphanedAt;

    @Comment("삭제 완료 시각")
    @Column(name = "completed_delete_at", columnDefinition = "DATETIME(6)")
    private Instant completedDeleteAt;

    @Embedded
    private AuditingJpaInfo auditingInfo;

    public void updateUploadStatus(UploadStatus uploadStatus, Instant completedUploadAt) {
        this.uploadStatus = uploadStatus;
        this.completedUploadAt = completedUploadAt;
    }

    public void markOrphaned(Instant orphanedAt) {
        this.uploadStatus = UploadStatus.ORPHANED;
        this.orphanedAt = orphanedAt;
    }
}
