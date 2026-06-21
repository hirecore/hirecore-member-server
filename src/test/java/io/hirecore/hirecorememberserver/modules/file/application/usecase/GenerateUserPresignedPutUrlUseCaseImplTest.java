package io.hirecore.hirecorememberserver.modules.file.application.usecase;

import io.hirecore.hirecorememberserver.modules.file.application.exception.FileApplicationException;
import io.hirecore.hirecorememberserver.modules.file.application.port.in.dto.request.ImagePresignedPutUrlCommand;
import io.hirecore.hirecorememberserver.modules.file.application.port.in.dto.response.ImagePresignedPutUrlResponse;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.GeneratePresignedPutUrlPort;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.SaveImageFileMetaPort;
import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;
import io.hirecore.hirecorememberserver.modules.file.domain.vo.*;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadUserStorageLimitPort;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.DomainType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Purpose;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadUserUsedQuotaPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@DisplayName("GenerateUserPresignedPutUrlUseCaseImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class GenerateUserPresignedPutUrlUseCaseImplTest {

    @InjectMocks
    private GenerateUserPresignedPutUrlUseCaseImpl sut;

    @Mock
    private LoadUserStorageLimitPort loadUserStorageLimitPort;

    @Mock
    private LoadUserUsedQuotaPort loadUserStorageUsagePort;

    @Mock
    private GeneratePresignedPutUrlPort generatePresignedPutUrlPort;

    @Mock
    private SaveImageFileMetaPort saveImageFileMetaPort;

    private static final Long MEMBER_ACCOUNT_ID = 1L;

    private ImagePresignedPutUrlCommand createCommand(Long fileSizeBytes) {
        return new ImagePresignedPutUrlCommand(
                1L,
                "test-image.webp",
                MimeType.IMAGE_WEBP,
                FileExtension.WEBP,
                fileSizeBytes,
                1920,
                1080,
                DomainType.PORTFOLIO,
                Purpose.CONTENT_IMAGE
        );
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessTest {

        @Test
        @DisplayName("가용 용량 내 요청 시 presigned URL 목록을 반환한다")
        void should_return_presigned_url_responses() {
            // given
            ImagePresignedPutUrlCommand command = createCommand(1048576L);
            given(loadUserStorageLimitPort.findStorageLimitBytes(MEMBER_ACCOUNT_ID)).willReturn(10485760L);
            given(loadUserStorageUsagePort.findUsedQuotaBytes(MEMBER_ACCOUNT_ID)).willReturn(0L);

            ImageFileMeta savedMeta = ImageFileMeta.create(
                    MEMBER_ACCOUNT_ID, DomainType.PORTFOLIO, Purpose.CONTENT_IMAGE,
                    "AWS_S3", "test-bucket", "users/1/portfolio/content-image/uuid.webp",
                    "test-image.webp", MimeType.IMAGE_WEBP, FileExtension.WEBP,
                    1048576L, 1920, 1080
            );
            given(saveImageFileMetaPort.save(any(ImageFileMeta.class))).willReturn(savedMeta);
            given(generatePresignedPutUrlPort.generate(anyString(), anyString(), anyLong()))
                    .willReturn("https://s3.presigned.url");
            given(generatePresignedPutUrlPort.getBucketName()).willReturn("test-bucket");
            given(generatePresignedPutUrlPort.getPublicBaseUrl()).willReturn("https://cdn.example.com");

            // when
            List<ImagePresignedPutUrlResponse> responses = sut.execute(MEMBER_ACCOUNT_ID, List.of(command));

            // then
            assertThat(responses).hasSize(1);
            assertThat(responses.get(0)).satisfies(response -> {
                assertThat(response.clientFileId()).isEqualTo(1L);
                assertThat(response.imageFileMetaId()).isNotNull();
                assertThat(response.presignedUrl()).isEqualTo("https://s3.presigned.url");
                assertThat(response.publicUrl()).startsWith("https://cdn.example.com/users/");
            });

            then(saveImageFileMetaPort).should().save(any(ImageFileMeta.class));
        }

        @Test
        @DisplayName("저장되는 ImageFileMeta에 요청 정보가 올바르게 전달된다")
        void should_pass_correct_data_to_save_port() {
            // given
            ImagePresignedPutUrlCommand command = createCommand(2097152L);
            given(loadUserStorageLimitPort.findStorageLimitBytes(MEMBER_ACCOUNT_ID)).willReturn(10485760L);
            given(loadUserStorageUsagePort.findUsedQuotaBytes(MEMBER_ACCOUNT_ID)).willReturn(0L);

            ImageFileMeta savedMeta = ImageFileMeta.create(
                    MEMBER_ACCOUNT_ID, DomainType.PORTFOLIO, Purpose.CONTENT_IMAGE,
                    "AWS_S3", "test-bucket", "users/1/portfolio/content-image/uuid.webp",
                    "test-image.webp", MimeType.IMAGE_WEBP, FileExtension.WEBP,
                    2097152L, 1920, 1080
            );
            given(saveImageFileMetaPort.save(any(ImageFileMeta.class))).willReturn(savedMeta);
            given(generatePresignedPutUrlPort.generate(anyString(), anyString(), anyLong()))
                    .willReturn("https://s3.presigned.url");
            given(generatePresignedPutUrlPort.getBucketName()).willReturn("test-bucket");
            given(generatePresignedPutUrlPort.getPublicBaseUrl()).willReturn("https://cdn.example.com");

            // when
            sut.execute(MEMBER_ACCOUNT_ID, List.of(command));

            // then
            ArgumentCaptor<ImageFileMeta> captor = ArgumentCaptor.forClass(ImageFileMeta.class);
            then(saveImageFileMetaPort).should().save(captor.capture());

            ImageFileMeta captured = captor.getValue();
            assertThat(captured.getMemberAccountId()).isEqualTo(MEMBER_ACCOUNT_ID);
            assertThat(captured.getDomainType()).isEqualTo(DomainType.PORTFOLIO);
            assertThat(captured.getPurpose()).isEqualTo(Purpose.CONTENT_IMAGE);
            assertThat(captured.getBucketName()).isEqualTo("test-bucket");
            assertThat(captured.getStorageProvider()).isEqualTo("AWS_S3");
            assertThat(captured.getMimeType()).isEqualTo(MimeType.IMAGE_WEBP);
            assertThat(captured.getFileExtension()).isEqualTo(FileExtension.WEBP);
            assertThat(captured.getFileSizeBytes()).isEqualTo(2097152L);
            assertThat(captured.getWidth()).isEqualTo(1920);
            assertThat(captured.getHeight()).isEqualTo(1080);
            assertThat(captured.getUploadStatus()).isEqualTo(UploadStatus.PENDING);
            assertThat(captured.getObjectKey()).contains("users/1/portfolio/content-image/");
        }

        @Test
        @DisplayName("presigned URL 생성 시 올바른 contentType과 contentLength가 전달된다")
        void should_pass_correct_params_to_presigned_url_port() {
            // given
            ImagePresignedPutUrlCommand command = createCommand(1048576L);
            given(loadUserStorageLimitPort.findStorageLimitBytes(MEMBER_ACCOUNT_ID)).willReturn(10485760L);
            given(loadUserStorageUsagePort.findUsedQuotaBytes(MEMBER_ACCOUNT_ID)).willReturn(0L);

            ImageFileMeta savedMeta = ImageFileMeta.create(
                    MEMBER_ACCOUNT_ID, DomainType.PORTFOLIO, Purpose.CONTENT_IMAGE,
                    "AWS_S3", "test-bucket", "users/1/portfolio/content-image/uuid.webp",
                    "test-image.webp", MimeType.IMAGE_WEBP, FileExtension.WEBP,
                    1048576L, 1920, 1080
            );
            given(saveImageFileMetaPort.save(any(ImageFileMeta.class))).willReturn(savedMeta);
            given(generatePresignedPutUrlPort.generate(anyString(), eq("image/webp"), eq(1048576L)))
                    .willReturn("https://s3.presigned.url");
            given(generatePresignedPutUrlPort.getBucketName()).willReturn("test-bucket");
            given(generatePresignedPutUrlPort.getPublicBaseUrl()).willReturn("https://cdn.example.com");

            // when
            sut.execute(MEMBER_ACCOUNT_ID, List.of(command));

            // then
            then(generatePresignedPutUrlPort).should().generate(anyString(), eq("image/webp"), eq(1048576L));
        }

        @Test
        @DisplayName("여러 파일 요청 시 각각에 대해 presigned URL을 발급한다")
        void should_return_multiple_responses_for_multiple_commands() {
            // given
            ImagePresignedPutUrlCommand command1 = createCommand(1048576L);
            ImagePresignedPutUrlCommand command2 = createCommand(2097152L);
            given(loadUserStorageLimitPort.findStorageLimitBytes(MEMBER_ACCOUNT_ID)).willReturn(10485760L);
            given(loadUserStorageUsagePort.findUsedQuotaBytes(MEMBER_ACCOUNT_ID)).willReturn(0L);

            ImageFileMeta savedMeta = ImageFileMeta.create(
                    MEMBER_ACCOUNT_ID, DomainType.PORTFOLIO, Purpose.CONTENT_IMAGE,
                    "AWS_S3", "test-bucket", "users/1/portfolio/content-image/uuid.webp",
                    "test-image.webp", MimeType.IMAGE_WEBP, FileExtension.WEBP,
                    1048576L, 1920, 1080
            );
            given(saveImageFileMetaPort.save(any(ImageFileMeta.class))).willReturn(savedMeta);
            given(generatePresignedPutUrlPort.generate(anyString(), anyString(), anyLong()))
                    .willReturn("https://s3.presigned.url");
            given(generatePresignedPutUrlPort.getBucketName()).willReturn("test-bucket");
            given(generatePresignedPutUrlPort.getPublicBaseUrl()).willReturn("https://cdn.example.com");

            // when
            List<ImagePresignedPutUrlResponse> responses = sut.execute(MEMBER_ACCOUNT_ID, List.of(command1, command2));

            // then
            assertThat(responses).hasSize(2);
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailureTest {

        @Test
        @DisplayName("요청 용량이 가용 용량을 초과하면 STORAGE_QUOTA_EXCEEDED 예외가 발생한다")
        void should_throw_when_storage_quota_exceeded() {
            // given
            ImagePresignedPutUrlCommand command = createCommand(10485760L);
            given(loadUserStorageLimitPort.findStorageLimitBytes(MEMBER_ACCOUNT_ID)).willReturn(5242880L);
            given(loadUserStorageUsagePort.findUsedQuotaBytes(MEMBER_ACCOUNT_ID)).willReturn(0L);

            // when & then
            assertThatThrownBy(() -> sut.execute(MEMBER_ACCOUNT_ID, List.of(command)))
                    .isInstanceOf(FileApplicationException.class);

            then(saveImageFileMetaPort).should(never()).save(any());
            then(generatePresignedPutUrlPort).should(never()).generate(anyString(), anyString(), anyLong());
        }

        @Test
        @DisplayName("기존 사용량 + 요청 용량이 할당량을 초과하면 예외가 발생한다")
        void should_throw_when_used_plus_request_exceeds_quota() {
            // given
            ImagePresignedPutUrlCommand command = createCommand(3145728L);
            given(loadUserStorageLimitPort.findStorageLimitBytes(MEMBER_ACCOUNT_ID)).willReturn(5242880L);
            given(loadUserStorageUsagePort.findUsedQuotaBytes(MEMBER_ACCOUNT_ID)).willReturn(3145728L);

            // when & then
            assertThatThrownBy(() -> sut.execute(MEMBER_ACCOUNT_ID, List.of(command)))
                    .isInstanceOf(FileApplicationException.class);
        }
    }
}
