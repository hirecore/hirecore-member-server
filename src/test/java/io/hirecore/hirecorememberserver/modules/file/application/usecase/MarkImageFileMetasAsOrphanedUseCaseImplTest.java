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

@DisplayName("MarkImageFileMetasAsOrphanedUseCaseImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class MarkImageFileMetasAsOrphanedUseCaseImplTest {

    @InjectMocks
    private MarkImageFileMetasAsOrphanedUseCaseImpl sut;

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
        meta.markUploaded();
        meta.pollAllEvents(); // 생성 시 발행된 ImageUploadedEvent 정리
        return meta;
    }

    private static ImageFileMeta orphanedImage(Long memberId, String suffix) {
        ImageFileMeta meta = uploadedImage(memberId, suffix);
        meta.markOrphaned();
        meta.pollAllEvents();
        return meta;
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessTest {

        @Test
        @DisplayName("UPLOADED 이미지들을 받으면 모두 ORPHANED 로 전이하고 markAllAsOrphaned 가 호출된다")
        void should_transition_all_uploaded_to_orphaned() {
            // given
            ImageFileMeta meta1 = uploadedImage(OWNER_ID, "a");
            ImageFileMeta meta2 = uploadedImage(OWNER_ID, "b");
            List<Long> ids = List.of(meta1.getId(), meta2.getId());
            given(loadImageFileMetaPort.findAllByIds(ids)).willReturn(List.of(meta1, meta2));

            // when
            sut.execute(OWNER_ID, ids);

            // then
            assertThat(meta1.getUploadStatus()).isEqualTo(UploadStatus.ORPHANED);
            assertThat(meta2.getUploadStatus()).isEqualTo(UploadStatus.ORPHANED);
            ArgumentCaptor<List<ImageFileMeta>> captor = ArgumentCaptor.forClass(List.class);
            then(updateImageFileMetaPort).should().markAllAsOrphaned(captor.capture());
            assertThat(captor.getValue()).containsExactly(meta1, meta2);
        }

        @Test
        @DisplayName("모두 이미 ORPHANED 면 멱등 통과한다 (markAllAsOrphaned 호출 없음)")
        void should_be_idempotent_when_all_already_orphaned() {
            // given
            ImageFileMeta meta1 = orphanedImage(OWNER_ID, "a");
            ImageFileMeta meta2 = orphanedImage(OWNER_ID, "b");
            List<Long> ids = List.of(meta1.getId(), meta2.getId());
            given(loadImageFileMetaPort.findAllByIds(ids)).willReturn(List.of(meta1, meta2));

            // when
            sut.execute(OWNER_ID, ids);

            // then
            then(updateImageFileMetaPort).should(never()).markAllAsOrphaned(anyList());
        }

        @Test
        @DisplayName("UPLOADED 와 ORPHANED 가 섞여 있으면 UPLOADED 만 ORPHANED 로 전이된다")
        void should_transition_only_uploaded_when_mixed() {
            // given
            ImageFileMeta uploaded = uploadedImage(OWNER_ID, "new");
            ImageFileMeta already = orphanedImage(OWNER_ID, "old");
            List<Long> ids = List.of(uploaded.getId(), already.getId());
            given(loadImageFileMetaPort.findAllByIds(ids)).willReturn(List.of(uploaded, already));

            // when
            sut.execute(OWNER_ID, ids);

            // then
            assertThat(uploaded.getUploadStatus()).isEqualTo(UploadStatus.ORPHANED);
            ArgumentCaptor<List<ImageFileMeta>> captor = ArgumentCaptor.forClass(List.class);
            then(updateImageFileMetaPort).should().markAllAsOrphaned(captor.capture());
            assertThat(captor.getValue()).containsExactly(uploaded);
        }

        @Test
        @DisplayName("imageIds 가 null 이거나 빈 컬렉션이면 아무것도 하지 않는다")
        void should_no_op_on_empty_input() {
            // when
            sut.execute(OWNER_ID, null);
            sut.execute(OWNER_ID, Collections.emptyList());

            // then
            then(loadImageFileMetaPort).should(never()).findAllByIds(anyList());
            then(updateImageFileMetaPort).should(never()).markAllAsOrphaned(anyList());
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailureTest {

        @Test
        @DisplayName("일부 이미지가 존재하지 않으면 IMAGE_NOT_FOUND 예외를 던진다")
        void should_throw_when_some_images_missing() {
            // given
            ImageFileMeta meta = uploadedImage(OWNER_ID, "a");
            List<Long> ids = List.of(meta.getId(), 9999L);  // 9999L 은 존재하지 않는 ID
            given(loadImageFileMetaPort.findAllByIds(ids)).willReturn(List.of(meta));

            // when & then
            assertThatThrownBy(() -> sut.execute(OWNER_ID, ids))
                    .isInstanceOf(FileApplicationException.class);
            then(updateImageFileMetaPort).should(never()).markAllAsOrphaned(anyList());
        }

        @Test
        @DisplayName("다른 사용자 소유의 이미지가 포함되면 IMAGE_OWNERSHIP_VIOLATION 예외를 던진다")
        void should_throw_when_image_belongs_to_other_user() {
            // given
            ImageFileMeta othersMeta = uploadedImage(OTHER_USER_ID, "a");
            List<Long> ids = List.of(othersMeta.getId());
            given(loadImageFileMetaPort.findAllByIds(ids)).willReturn(List.of(othersMeta));

            // when & then
            assertThatThrownBy(() -> sut.execute(OWNER_ID, ids))
                    .isInstanceOf(FileApplicationException.class);
            then(updateImageFileMetaPort).should(never()).markAllAsOrphaned(anyList());
        }

        @Test
        @DisplayName("PENDING 상태 이미지가 포함되면 INVALID_UPLOAD_STATUS_TRANSITION 도메인 예외를 던진다")
        void should_throw_when_pending_included() {
            // given
            ImageFileMeta pending = pendingImage(OWNER_ID, "pending");
            List<Long> ids = List.of(pending.getId());
            given(loadImageFileMetaPort.findAllByIds(ids)).willReturn(List.of(pending));

            // when & then
            assertThatThrownBy(() -> sut.execute(OWNER_ID, ids))
                    .isInstanceOf(RuntimeException.class);
            then(updateImageFileMetaPort).should(never()).markAllAsOrphaned(anyList());
        }
    }
}
