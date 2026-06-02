package io.hirecore.hirecorememberserver.modules.file.application.usecase;

import io.hirecore.hirecorememberserver.modules.file.application.exception.FileApplicationException;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.LoadImageFileMetaPort;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.UpdateImageFileMetaPort;
import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;
import io.hirecore.hirecorememberserver.modules.file.domain.vo.FileExtension;
import io.hirecore.hirecorememberserver.modules.file.domain.vo.MimeType;
import io.hirecore.hirecorememberserver.modules.file.domain.vo.UploadStatus;
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

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@DisplayName("UpdateUploadStatusOfImageFileMetaUseCaseImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class UpdateUploadStatusOfImageFileMetaUseCaseImplTest {

    @InjectMocks
    private UpdateUploadStatusOfImageFileMetaUseCaseImpl sut;

    @Mock
    private LoadImageFileMetaPort loadImageFileMetaPort;

    @Mock
    private UpdateImageFileMetaPort updateImageFileMetaPort;

    private static final Long OWNER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;

    private static ImageFileMeta pendingImage(Long memberId, String suffix) {
        return ImageFileMeta.create(
                memberId,
                DomainType.PORTFOLIO,
                Purpose.CONTENT_IMAGE,
                "AWS_S3",
                "test-bucket",
                "users/" + memberId + "/portfolio/content-image/" + suffix + ".webp",
                suffix + ".webp",
                MimeType.IMAGE_WEBP,
                FileExtension.WEBP,
                1024L,
                100,
                100
        );
    }

    private static ImageFileMeta uploadedImage(Long memberId, String suffix) {
        ImageFileMeta meta = pendingImage(memberId, suffix);
        meta.updateUploadStatus(UploadStatus.UPLOADED);
        return meta;
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessTest {

        @Test
        @DisplayName("PENDING 이미지들만 받으면 모두 UPLOADED 로 전이하고 markAllAsUploaded 가 호출된다")
        void should_transition_all_pending() {
            // given
            ImageFileMeta meta1 = pendingImage(OWNER_ID, "a");
            ImageFileMeta meta2 = pendingImage(OWNER_ID, "b");
            List<Long> ids = List.of(meta1.getId(), meta2.getId());
            given(loadImageFileMetaPort.findAllByIds(ids)).willReturn(List.of(meta1, meta2));

            // when
            sut.execute(OWNER_ID, ids);

            // then
            assertThat(meta1.getUploadStatus()).isEqualTo(UploadStatus.UPLOADED);
            assertThat(meta2.getUploadStatus()).isEqualTo(UploadStatus.UPLOADED);
            ArgumentCaptor<List<ImageFileMeta>> captor = ArgumentCaptor.forClass(List.class);
            then(updateImageFileMetaPort).should().markAllAsUploaded(captor.capture());
            assertThat(captor.getValue()).containsExactly(meta1, meta2);
        }

        @Test
        @DisplayName("모두 이미 UPLOADED 상태면 전이 없이 멱등 통과한다 (markAllAsUploaded 호출 없음)")
        void should_be_idempotent_when_all_already_uploaded() {
            // given
            ImageFileMeta meta1 = uploadedImage(OWNER_ID, "a");
            ImageFileMeta meta2 = uploadedImage(OWNER_ID, "b");
            List<Long> ids = List.of(meta1.getId(), meta2.getId());
            given(loadImageFileMetaPort.findAllByIds(ids)).willReturn(List.of(meta1, meta2));

            // when
            sut.execute(OWNER_ID, ids);

            // then — 상태 변경 없음, 어댑터 호출도 없음
            assertThat(meta1.getUploadStatus()).isEqualTo(UploadStatus.UPLOADED);
            assertThat(meta2.getUploadStatus()).isEqualTo(UploadStatus.UPLOADED);
            then(updateImageFileMetaPort).should(never()).markAllAsUploaded(anyList());
        }

        @Test
        @DisplayName("PENDING 과 UPLOADED 가 섞여 있으면 PENDING 만 전이된다")
        void should_transition_only_pending_when_mixed() {
            // given
            ImageFileMeta pending = pendingImage(OWNER_ID, "new");
            ImageFileMeta already = uploadedImage(OWNER_ID, "existing");
            List<Long> ids = List.of(pending.getId(), already.getId());
            given(loadImageFileMetaPort.findAllByIds(ids)).willReturn(List.of(pending, already));

            // when
            sut.execute(OWNER_ID, ids);

            // then
            assertThat(pending.getUploadStatus()).isEqualTo(UploadStatus.UPLOADED);
            assertThat(already.getUploadStatus()).isEqualTo(UploadStatus.UPLOADED);
            ArgumentCaptor<List<ImageFileMeta>> captor = ArgumentCaptor.forClass(List.class);
            then(updateImageFileMetaPort).should().markAllAsUploaded(captor.capture());
            assertThat(captor.getValue()).containsExactly(pending);
        }

        @Test
        @DisplayName("imageIds 가 null 이거나 빈 컬렉션이면 아무것도 하지 않는다")
        void should_no_op_on_empty_input() {
            // when
            sut.execute(OWNER_ID, null);
            sut.execute(OWNER_ID, Collections.emptyList());

            // then
            then(loadImageFileMetaPort).should(never()).findAllByIds(anyList());
            then(updateImageFileMetaPort).should(never()).markAllAsUploaded(anyList());
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailureTest {

        @Test
        @DisplayName("일부 이미지가 존재하지 않으면 IMAGE_NOT_FOUND 예외를 던진다")
        void should_throw_when_some_images_missing() {
            // given
            ImageFileMeta meta = pendingImage(OWNER_ID, "a");
            List<Long> ids = List.of(meta.getId(), 9999L);  // 9999L 은 존재하지 않는 ID
            given(loadImageFileMetaPort.findAllByIds(ids)).willReturn(List.of(meta));

            // when & then
            assertThatThrownBy(() -> sut.execute(OWNER_ID, ids))
                    .isInstanceOf(FileApplicationException.class);
            then(updateImageFileMetaPort).should(never()).markAllAsUploaded(anyList());
        }

        @Test
        @DisplayName("다른 사용자 소유의 이미지가 포함되면 IMAGE_OWNERSHIP_VIOLATION 예외를 던진다")
        void should_throw_when_image_belongs_to_other_user() {
            // given
            ImageFileMeta othersMeta = pendingImage(OTHER_USER_ID, "a");
            List<Long> ids = List.of(othersMeta.getId());
            given(loadImageFileMetaPort.findAllByIds(ids)).willReturn(List.of(othersMeta));

            // when & then
            assertThatThrownBy(() -> sut.execute(OWNER_ID, ids))
                    .isInstanceOf(FileApplicationException.class);
            then(updateImageFileMetaPort).should(never()).markAllAsUploaded(anyList());
        }
    }
}
