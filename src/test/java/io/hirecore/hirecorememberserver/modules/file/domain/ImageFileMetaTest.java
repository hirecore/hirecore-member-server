package io.hirecore.hirecorememberserver.modules.file.domain;

import io.hirecore.hirecorememberserver.modules.file.domain.exception.ImageFileMetaDomainException;
import io.hirecore.hirecorememberserver.modules.file.domain.exception.ImageFileMetaDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.file.domain.vo.*;
import io.hirecore.hirecorememberserver.sharedkernel.domain.event.ImageOrphanedEvent;
import io.hirecore.hirecorememberserver.sharedkernel.domain.event.ImageUploadedEvent;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.DomainType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Purpose;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ImageFileMeta 도메인 단위 테스트")
class ImageFileMetaTest {

    private static ImageFileMeta createValid() {
        return ImageFileMeta.create(
                1L,
                DomainType.PORTFOLIO,
                Purpose.CONTENT_IMAGE,
                "AWS_S3",
                "test-bucket",
                "users/1/portfolio/content-image/uuid.webp",
                "my-image.webp",
                MimeType.IMAGE_WEBP,
                FileExtension.WEBP,
                1048576L,
                1920,
                1080
        );
    }

    @Nested
    @DisplayName("isOwnedBy()")
    class IsOwnedByTest {

        @Test
        @DisplayName("소유자면 true, 아니면(또는 null) false 를 반환한다")
        void should_report_ownership() {
            ImageFileMeta meta = createValid();

            assertThat(meta.isOwnedBy(1L)).isTrue();
            assertThat(meta.isOwnedBy(2L)).isFalse();
            assertThat(meta.isOwnedBy(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("업로드 상태 질의 (isUploaded/isOrphaned)")
    class UploadStatusQueryTest {

        @Test
        @DisplayName("PENDING 상태면 isUploaded/isOrphaned 모두 false 다")
        void should_report_false_when_pending() {
            ImageFileMeta meta = createValid();

            assertThat(meta.isUploaded()).isFalse();
            assertThat(meta.isOrphaned()).isFalse();
        }

        @Test
        @DisplayName("markUploaded 후 isUploaded 만 true 다")
        void should_report_uploaded_after_mark_uploaded() {
            ImageFileMeta meta = createValid();
            meta.markUploaded();

            assertThat(meta.isUploaded()).isTrue();
            assertThat(meta.isOrphaned()).isFalse();
        }

        @Test
        @DisplayName("markOrphaned 후 isOrphaned 만 true 다")
        void should_report_orphaned_after_mark_orphaned() {
            ImageFileMeta meta = createValid();
            meta.markUploaded();
            meta.markOrphaned();

            assertThat(meta.isOrphaned()).isTrue();
            assertThat(meta.isUploaded()).isFalse();
        }
    }

    @Nested
    @DisplayName("create() 팩토리 메서드")
    class CreateTest {

        @Test
        @DisplayName("유효한 파라미터로 ImageFileMeta를 생성한다")
        void should_create_image_file_meta_with_valid_parameters() {
            // when
            ImageFileMeta meta = createValid();

            // then
            assertThat(meta).satisfies(m -> {
                assertThat(m.getId()).isNotNull();
                assertThat(m.getMemberAccountId()).isEqualTo(1L);
                assertThat(m.getDomainType()).isEqualTo(DomainType.PORTFOLIO);
                assertThat(m.getPurpose()).isEqualTo(Purpose.CONTENT_IMAGE);
                assertThat(m.getStorageProvider()).isEqualTo("AWS_S3");
                assertThat(m.getBucketName()).isEqualTo("test-bucket");
                assertThat(m.getObjectKey()).isEqualTo("users/1/portfolio/content-image/uuid.webp");
                assertThat(m.getOriginalFileName()).isEqualTo("my-image.webp");
                assertThat(m.getMimeType()).isEqualTo(MimeType.IMAGE_WEBP);
                assertThat(m.getFileExtension()).isEqualTo(FileExtension.WEBP);
                assertThat(m.getFileSizeBytes()).isEqualTo(1048576L);
                assertThat(m.getWidth()).isEqualTo(1920);
                assertThat(m.getHeight()).isEqualTo(1080);
                assertThat(m.getUploadStatus()).isEqualTo(UploadStatus.PENDING);
                assertThat(m.getAuditingInfo()).isNotNull();
                assertThat(m.getAuditingInfo().createdAt()).isNotNull();
                assertThat(m.getAuditingInfo().updatedAt()).isNotNull();
            });
        }

        @Test
        @DisplayName("create()로 생성하면 uploadStatus는 항상 PENDING이다")
        void should_have_pending_status_when_created() {
            // when
            ImageFileMeta meta = createValid();

            // then
            assertThat(meta.getUploadStatus()).isEqualTo(UploadStatus.PENDING);
        }
    }

    @Nested
    @DisplayName("markUploaded 상태 전이")
    class MarkUploadedTest {

        @Test
        @DisplayName("PENDING에서 UPLOADED로 전이하면 ImageUploadedEvent가 emit된다")
        void should_emit_image_uploaded_event_when_transition_pending_to_uploaded() {
            ImageFileMeta meta = createValid();

            meta.markUploaded();

            Collection<Object> events = meta.pollAllEvents();
            assertThat(events).hasSize(1);
            assertThat(events.iterator().next()).isInstanceOfSatisfying(ImageUploadedEvent.class, e -> {
                assertThat(e.imageFileMetaId()).isEqualTo(meta.getId());
                assertThat(e.memberAccountId()).isEqualTo(meta.getMemberAccountId());
                assertThat(e.domainType()).isEqualTo(meta.getDomainType());
                assertThat(e.purpose()).isEqualTo(meta.getPurpose());
                assertThat(e.fileSizeBytes()).isEqualTo(meta.getFileSizeBytes());
                assertThat(e.completedUploadAt()).isEqualTo(meta.getCompletedUploadAt());
            });
        }

        @Test
        @DisplayName("이미 UPLOADED 인 상태에서 markUploaded 를 호출하면 멱등 처리되어 이벤트가 emit 되지 않는다")
        void should_be_idempotent_when_already_uploaded() {
            ImageFileMeta meta = createValid();
            meta.markUploaded();
            Instant firstCompletedUploadAt = meta.getCompletedUploadAt();
            meta.pollAllEvents(); // 기존 ImageUploadedEvent 비움

            meta.markUploaded();

            assertThat(meta.getUploadStatus()).isEqualTo(UploadStatus.UPLOADED);
            assertThat(meta.getCompletedUploadAt()).isEqualTo(firstCompletedUploadAt);
            assertThat(meta.pollAllEvents()).isEmpty();
        }

        @Test
        @DisplayName("ORPHANED 상태에서 markUploaded 를 호출하면 INVALID_UPLOAD_STATUS_TRANSITION 예외가 발생한다")
        void should_throw_when_mark_uploaded_from_orphaned() {
            ImageFileMeta meta = createValid();
            meta.markUploaded();
            meta.markOrphaned();

            assertThatThrownBy(meta::markUploaded)
                    .isInstanceOf(ImageFileMetaDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.INVALID_UPLOAD_STATUS_TRANSITION.getErrorCode());
        }
    }

    @Nested
    @DisplayName("markOrphaned 상태 전이")
    class MarkOrphanedTest {

        @Test
        @DisplayName("UPLOADED 상태에서 ORPHANED 로 전이하면 ImageOrphanedEvent 가 emit 된다")
        void should_emit_image_orphaned_event_when_transition_uploaded_to_orphaned() {
            ImageFileMeta meta = createValid();
            meta.markUploaded();
            meta.pollAllEvents(); // 기존 ImageUploadedEvent 비움

            meta.markOrphaned();

            assertThat(meta.getUploadStatus()).isEqualTo(UploadStatus.ORPHANED);
            assertThat(meta.getOrphanedAt()).isNotNull();
            Collection<Object> events = meta.pollAllEvents();
            assertThat(events).hasSize(1);
            assertThat(events.iterator().next()).isInstanceOfSatisfying(ImageOrphanedEvent.class, e -> {
                assertThat(e.imageFileMetaId()).isEqualTo(meta.getId());
                assertThat(e.memberAccountId()).isEqualTo(meta.getMemberAccountId());
                assertThat(e.domainType()).isEqualTo(meta.getDomainType());
                assertThat(e.purpose()).isEqualTo(meta.getPurpose());
                assertThat(e.fileSizeBytes()).isEqualTo(meta.getFileSizeBytes());
                assertThat(e.orphanedAt()).isEqualTo(meta.getOrphanedAt());
            });
        }

        @Test
        @DisplayName("이미 ORPHANED 인 상태에서 markOrphaned 를 호출하면 멱등 처리되어 이벤트가 emit 되지 않는다")
        void should_be_idempotent_when_already_orphaned() {
            ImageFileMeta meta = createValid();
            meta.markUploaded();
            meta.markOrphaned();
            Instant firstOrphanedAt = meta.getOrphanedAt();
            meta.pollAllEvents(); // 기존 이벤트 비움

            meta.markOrphaned();

            assertThat(meta.getUploadStatus()).isEqualTo(UploadStatus.ORPHANED);
            assertThat(meta.getOrphanedAt()).isEqualTo(firstOrphanedAt);
            assertThat(meta.pollAllEvents()).isEmpty();
        }

        @Test
        @DisplayName("PENDING 상태에서 markOrphaned 를 호출하면 INVALID_UPLOAD_STATUS_TRANSITION 예외가 발생한다")
        void should_throw_when_call_mark_orphaned_in_pending_status() {
            ImageFileMeta meta = createValid();

            assertThatThrownBy(meta::markOrphaned)
                    .isInstanceOf(ImageFileMetaDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.INVALID_UPLOAD_STATUS_TRANSITION.getErrorCode());
        }
    }

    @Nested
    @DisplayName("markDeleted 상태 전이")
    class MarkDeletedTest {

        @Test
        @DisplayName("ORPHANED 상태에서 DELETED 로 전이되고 completedDeleteAt 이 채워진다")
        void should_transition_orphaned_to_deleted() {
            ImageFileMeta meta = createValid();
            meta.markUploaded();
            meta.markOrphaned();
            meta.pollAllEvents();

            meta.markDeleted();

            assertThat(meta.getUploadStatus()).isEqualTo(UploadStatus.DELETED);
            assertThat(meta.getCompletedDeleteAt()).isNotNull();
            assertThat(meta.pollAllEvents()).isEmpty();
        }

        @Test
        @DisplayName("이미 DELETED 인 상태에서 markDeleted 를 호출하면 멱등 처리된다")
        void should_be_idempotent_when_already_deleted() {
            ImageFileMeta meta = createValid();
            meta.markUploaded();
            meta.markOrphaned();
            meta.markDeleted();
            Instant firstCompletedAt = meta.getCompletedDeleteAt();
            meta.pollAllEvents();

            meta.markDeleted();

            assertThat(meta.getUploadStatus()).isEqualTo(UploadStatus.DELETED);
            assertThat(meta.getCompletedDeleteAt()).isEqualTo(firstCompletedAt);
            assertThat(meta.pollAllEvents()).isEmpty();
        }

        @Test
        @DisplayName("PENDING 상태에서 markDeleted 를 호출하면 INVALID_UPLOAD_STATUS_TRANSITION 예외가 발생한다")
        void should_throw_when_call_mark_deleted_in_pending_status() {
            ImageFileMeta meta = createValid();

            assertThatThrownBy(meta::markDeleted)
                    .isInstanceOf(ImageFileMetaDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.INVALID_UPLOAD_STATUS_TRANSITION.getErrorCode());
        }

        @Test
        @DisplayName("UPLOADED 상태에서 markDeleted 를 호출하면 INVALID_UPLOAD_STATUS_TRANSITION 예외가 발생한다")
        void should_throw_when_call_mark_deleted_in_uploaded_status() {
            ImageFileMeta meta = createValid();
            meta.markUploaded();
            meta.pollAllEvents();

            assertThatThrownBy(meta::markDeleted)
                    .isInstanceOf(ImageFileMetaDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.INVALID_UPLOAD_STATUS_TRANSITION.getErrorCode());
        }
    }

    @Nested
    @DisplayName("불변식 (Invariants) 검증")
    class InvariantsTest {

        @Test
        @DisplayName("memberAccountId가 null이면 MEMBER_ACCOUNT_ID_MISSING 예외가 발생한다")
        void should_throw_when_member_account_id_is_null() {
            assertThatThrownBy(() -> ImageFileMeta.create(
                    null, DomainType.PORTFOLIO, Purpose.CONTENT_IMAGE,
                    "AWS_S3", "bucket", "key", "file.webp",
                    MimeType.IMAGE_WEBP, FileExtension.WEBP, 1024L, 100, 100
            ))
                    .isInstanceOf(ImageFileMetaDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.MEMBER_ACCOUNT_ID_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("domainType이 null이면 DOMAIN_TYPE_MISSING 예외가 발생한다")
        void should_throw_when_domain_type_is_null() {
            assertThatThrownBy(() -> ImageFileMeta.create(
                    1L, null, Purpose.CONTENT_IMAGE,
                    "AWS_S3", "bucket", "key", "file.webp",
                    MimeType.IMAGE_WEBP, FileExtension.WEBP, 1024L, 100, 100
            ))
                    .isInstanceOf(ImageFileMetaDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.DOMAIN_TYPE_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("objectKey가 null이면 OBJECT_KEY_MISSING 예외가 발생한다")
        void should_throw_when_object_key_is_null() {
            assertThatThrownBy(() -> ImageFileMeta.create(
                    1L, DomainType.PORTFOLIO, Purpose.CONTENT_IMAGE,
                    "AWS_S3", "bucket", null, "file.webp",
                    MimeType.IMAGE_WEBP, FileExtension.WEBP, 1024L, 100, 100
            ))
                    .isInstanceOf(ImageFileMetaDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.OBJECT_KEY_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("fileSizeBytes가 null이면 FILE_SIZE_BYTES_MISSING 예외가 발생한다")
        void should_throw_when_file_size_bytes_is_null() {
            assertThatThrownBy(() -> ImageFileMeta.create(
                    1L, DomainType.PORTFOLIO, Purpose.CONTENT_IMAGE,
                    "AWS_S3", "bucket", "key", "file.webp",
                    MimeType.IMAGE_WEBP, FileExtension.WEBP, null, 100, 100
            ))
                    .isInstanceOf(ImageFileMetaDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.FILE_SIZE_BYTES_MISSING.getErrorCode());
        }

        @Test
        @DisplayName("width가 null이면 WIDTH_MISSING 예외가 발생한다")
        void should_throw_when_width_is_null() {
            assertThatThrownBy(() -> ImageFileMeta.create(
                    1L, DomainType.PORTFOLIO, Purpose.CONTENT_IMAGE,
                    "AWS_S3", "bucket", "key", "file.webp",
                    MimeType.IMAGE_WEBP, FileExtension.WEBP, 1024L, null, 100
            ))
                    .isInstanceOf(ImageFileMetaDomainException.class)
                    .extracting("errorCode")
                    .isEqualTo(ImageFileMetaDomainExceptionCodeCluster.HiddenDetailResponse.WIDTH_MISSING.getErrorCode());
        }
    }
}
