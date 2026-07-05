package io.hirecore.hirecorememberserver.modules.file.application.usecase;

import io.hirecore.hirecorememberserver.modules.file.application.port.in.IssueImageUploadUrlUseCase;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.GeneratePresignedPutUrlPort;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.SaveImageFileMetaPort;
import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;
import io.hirecore.hirecorememberserver.modules.file.domain.vo.*;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.VerifyUserStorageCapacitySharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.DomainType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Purpose;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;

@DisplayName("IssueImageUploadUrlUseCaseImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class IssueImageUploadUrlUseCaseImplTest {

    @InjectMocks
    private IssueImageUploadUrlUseCaseImpl sut;

    @Mock
    private VerifyUserStorageCapacitySharedPort verifyUserStorageCapacitySharedPort;

    @Mock
    private GeneratePresignedPutUrlPort generatePresignedPutUrlPort;

    @Mock
    private SaveImageFileMetaPort saveImageFileMetaPort;

    private static final Long MEMBER_ACCOUNT_ID = 1L;

    private IssueImageUploadUrlUseCase.Command createCommand(Long fileSizeBytes) {
        return new IssueImageUploadUrlUseCase.Command(
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
            IssueImageUploadUrlUseCase.Command command = createCommand(1048576L);

            ImageFileMeta savedMeta = ImageFileMeta.create(
                    MEMBER_ACCOUNT_ID, DomainType.PORTFOLIO, Purpose.CONTENT_IMAGE,
                    "AWS_S3", "test-bucket", "users/1/portfolio/content-image/uuid.webp",
                    "test-image.webp", MimeType.IMAGE_WEBP, FileExtension.WEBP,
                    1048576L, 1920, 1080
            );
            given(saveImageFileMetaPort.save(any(ImageFileMeta.class))).willReturn(savedMeta);
            given(generatePresignedPutUrlPort.generate(anyString(), anyString(), anyLong()))
                    .willReturn(new GeneratePresignedPutUrlPort.Result(
                            "https://s3.presigned.url",
                            "https://cdn.example.com/users/1/portfolio/content-image/uuid.webp",
                            "test-bucket"));

            // when
            List<IssueImageUploadUrlUseCase.Response> responses = sut.execute(MEMBER_ACCOUNT_ID, List.of(command));

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
            IssueImageUploadUrlUseCase.Command command = createCommand(2097152L);

            ImageFileMeta savedMeta = ImageFileMeta.create(
                    MEMBER_ACCOUNT_ID, DomainType.PORTFOLIO, Purpose.CONTENT_IMAGE,
                    "AWS_S3", "test-bucket", "users/1/portfolio/content-image/uuid.webp",
                    "test-image.webp", MimeType.IMAGE_WEBP, FileExtension.WEBP,
                    2097152L, 1920, 1080
            );
            given(saveImageFileMetaPort.save(any(ImageFileMeta.class))).willReturn(savedMeta);
            given(generatePresignedPutUrlPort.generate(anyString(), anyString(), anyLong()))
                    .willReturn(new GeneratePresignedPutUrlPort.Result(
                            "https://s3.presigned.url",
                            "https://cdn.example.com/users/1/portfolio/content-image/uuid.webp",
                            "test-bucket"));

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
            IssueImageUploadUrlUseCase.Command command = createCommand(1048576L);

            ImageFileMeta savedMeta = ImageFileMeta.create(
                    MEMBER_ACCOUNT_ID, DomainType.PORTFOLIO, Purpose.CONTENT_IMAGE,
                    "AWS_S3", "test-bucket", "users/1/portfolio/content-image/uuid.webp",
                    "test-image.webp", MimeType.IMAGE_WEBP, FileExtension.WEBP,
                    1048576L, 1920, 1080
            );
            given(saveImageFileMetaPort.save(any(ImageFileMeta.class))).willReturn(savedMeta);
            given(generatePresignedPutUrlPort.generate(anyString(), eq("image/webp"), eq(1048576L)))
                    .willReturn(new GeneratePresignedPutUrlPort.Result(
                            "https://s3.presigned.url",
                            "https://cdn.example.com/users/1/portfolio/content-image/uuid.webp",
                            "test-bucket"));

            // when
            sut.execute(MEMBER_ACCOUNT_ID, List.of(command));

            // then
            then(generatePresignedPutUrlPort).should().generate(anyString(), eq("image/webp"), eq(1048576L));
        }

        @Test
        @DisplayName("여러 파일 요청 시 합계 크기로 용량 검증을 위임하고 각각 presigned URL을 발급한다")
        void should_verify_total_size_and_return_multiple_responses() {
            // given
            IssueImageUploadUrlUseCase.Command command1 = createCommand(1048576L);
            IssueImageUploadUrlUseCase.Command command2 = createCommand(2097152L);

            ImageFileMeta savedMeta = ImageFileMeta.create(
                    MEMBER_ACCOUNT_ID, DomainType.PORTFOLIO, Purpose.CONTENT_IMAGE,
                    "AWS_S3", "test-bucket", "users/1/portfolio/content-image/uuid.webp",
                    "test-image.webp", MimeType.IMAGE_WEBP, FileExtension.WEBP,
                    1048576L, 1920, 1080
            );
            given(saveImageFileMetaPort.save(any(ImageFileMeta.class))).willReturn(savedMeta);
            given(generatePresignedPutUrlPort.generate(anyString(), anyString(), anyLong()))
                    .willReturn(new GeneratePresignedPutUrlPort.Result(
                            "https://s3.presigned.url",
                            "https://cdn.example.com/users/1/portfolio/content-image/uuid.webp",
                            "test-bucket"));

            // when
            List<IssueImageUploadUrlUseCase.Response> responses = sut.execute(MEMBER_ACCOUNT_ID, List.of(command1, command2));

            // then
            assertThat(responses).hasSize(2);
            then(verifyUserStorageCapacitySharedPort).should().verifyCapacityFor(MEMBER_ACCOUNT_ID, 3145728L);
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailureTest {

        @Test
        @DisplayName("용량 검증이 실패하면 예외가 그대로 전파되고 저장/URL 발급은 호출되지 않는다")
        void should_short_circuit_when_capacity_verification_fails() {
            // given
            IssueImageUploadUrlUseCase.Command command = createCommand(10485760L);
            RuntimeException capacityError = new RuntimeException("storage quota exceeded");
            willThrow(capacityError)
                    .given(verifyUserStorageCapacitySharedPort)
                    .verifyCapacityFor(eq(MEMBER_ACCOUNT_ID), anyLong());

            // when & then
            assertThatThrownBy(() -> sut.execute(MEMBER_ACCOUNT_ID, List.of(command)))
                    .isSameAs(capacityError);

            then(saveImageFileMetaPort).should(never()).save(any());
            then(generatePresignedPutUrlPort).should(never()).generate(anyString(), anyString(), anyLong());
        }
    }
}
