package io.hirecore.hirecorememberserver.modules.file.application.usecase;

import io.hirecore.hirecorememberserver.modules.file.application.port.out.DeleteObjectsFromStoragePort;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.LoadImageFileMetaPort;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.UpdateImageFileMetaPort;
import io.hirecore.hirecorememberserver.modules.file.application.properties.OrphanedImageCleanupProperties;
import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;
import io.hirecore.hirecorememberserver.modules.file.domain.vo.FileExtension;
import io.hirecore.hirecorememberserver.modules.file.domain.vo.MimeType;
import io.hirecore.hirecorememberserver.modules.file.domain.vo.UploadStatus;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.DomainType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Purpose;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@DisplayName("CleanupOrphanedImageUseCaseImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class CleanupOrphanedImageUseCaseImplTest {

    @InjectMocks
    private CleanupOrphanedImageUseCaseImpl sut;

    @Mock
    private LoadImageFileMetaPort loadImageFileMetaPort;

    @Mock
    private UpdateImageFileMetaPort updateImageFileMetaPort;

    @Mock
    private DeleteObjectsFromStoragePort deleteObjectsFromStoragePort;

    @Spy
    private OrphanedImageCleanupProperties properties = new OrphanedImageCleanupProperties(Duration.ofHours(24), 1000);

    @BeforeEach
    void setUp() {
        // record properties 는 @Spy 가 아닌 직접 인스턴스를 주입하므로 별도 셋업 불필요
    }

    private static ImageFileMeta orphanedMeta(Long memberId, String suffix, String bucket) {
        ImageFileMeta meta = ImageFileMeta.create(
                memberId,
                DomainType.PORTFOLIO,
                Purpose.CONTENT_IMAGE,
                "AWS_S3",
                bucket,
                "users/" + memberId + "/portfolio/content-image/" + suffix + ".webp",
                suffix + ".webp",
                MimeType.IMAGE_WEBP,
                FileExtension.WEBP,
                1024L,
                100,
                100
        );
        meta.updateUploadStatus(UploadStatus.UPLOADED);
        meta.markOrphaned();
        meta.pollAllEvents();
        return meta;
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessTest {

        @Test
        @DisplayName("후보가 없으면 0 을 반환하고 S3/영속 호출이 일어나지 않는다")
        void should_no_op_when_no_candidates() {
            given(loadImageFileMetaPort.findOrphanedCandidates(any(), anyInt())).willReturn(List.of());

            int processed = sut.execute();

            assertThat(processed).isZero();
            then(deleteObjectsFromStoragePort).should(never()).deleteObjects(anyString(), anyCollection());
            then(updateImageFileMetaPort).should(never()).markAllAsDeleted(any());
        }

        @Test
        @DisplayName("후보들을 버킷별로 그룹화하여 deleteObjects 를 호출하고, 성공한 메타만 markAllAsDeleted 에 전달한다")
        @SuppressWarnings("unchecked")
        void should_group_by_bucket_and_persist_only_succeeded() {
            // given — 두 버킷에 각 2건씩
            ImageFileMeta a1 = orphanedMeta(1L, "a1", "bucket-A");
            ImageFileMeta a2 = orphanedMeta(1L, "a2", "bucket-A");
            ImageFileMeta b1 = orphanedMeta(2L, "b1", "bucket-B");
            ImageFileMeta b2 = orphanedMeta(2L, "b2", "bucket-B");
            given(loadImageFileMetaPort.findOrphanedCandidates(any(), anyInt()))
                    .willReturn(List.of(a1, a2, b1, b2));

            given(deleteObjectsFromStoragePort.deleteObjects(eq("bucket-A"), anyCollection()))
                    .willReturn(Set.of(a1.getObjectKey(), a2.getObjectKey()));
            given(deleteObjectsFromStoragePort.deleteObjects(eq("bucket-B"), anyCollection()))
                    .willReturn(Set.of(b1.getObjectKey())); // b2 는 부분 실패

            // when
            int processed = sut.execute();

            // then
            assertThat(processed).isEqualTo(3); // a1, a2, b1 만 성공
            ArgumentCaptor<List<ImageFileMeta>> persistCaptor = ArgumentCaptor.forClass(List.class);
            then(updateImageFileMetaPort).should().markAllAsDeleted(persistCaptor.capture());
            assertThat(persistCaptor.getValue()).containsExactlyInAnyOrder(a1, a2, b1);
            assertThat(persistCaptor.getValue()).doesNotContain(b2);

            // 도메인 상태 — 성공한 것만 DELETED
            assertThat(a1.getUploadStatus()).isEqualTo(UploadStatus.DELETED);
            assertThat(a2.getUploadStatus()).isEqualTo(UploadStatus.DELETED);
            assertThat(b1.getUploadStatus()).isEqualTo(UploadStatus.DELETED);
            assertThat(b2.getUploadStatus()).isEqualTo(UploadStatus.ORPHANED);
        }

        @Test
        @DisplayName("S3 가 모든 키에 대해 실패하면 markAllAsDeleted 가 호출되지 않는다")
        void should_not_persist_when_all_s3_fail() {
            ImageFileMeta meta = orphanedMeta(1L, "x", "bucket-A");
            given(loadImageFileMetaPort.findOrphanedCandidates(any(), anyInt())).willReturn(List.of(meta));
            given(deleteObjectsFromStoragePort.deleteObjects(eq("bucket-A"), anyCollection()))
                    .willReturn(Set.of());

            int processed = sut.execute();

            assertThat(processed).isZero();
            then(updateImageFileMetaPort).should(never()).markAllAsDeleted(any());
            assertThat(meta.getUploadStatus()).isEqualTo(UploadStatus.ORPHANED);
        }
    }
}
