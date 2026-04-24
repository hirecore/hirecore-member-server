package io.hirecore.hirecorememberserver.modules.file.domain;

import io.hirecore.hirecorememberserver.modules.file.domain.exception.ImageFileMetaDomainException;
import io.hirecore.hirecorememberserver.modules.file.domain.exception.ImageFileMetaDomainExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.file.domain.vo.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
