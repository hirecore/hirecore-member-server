package io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa;

import com.navercorp.fixturemonkey.FixtureMonkey;
import io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.entity.ImageFileMetaJpaEntity;
import io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.mapper.ImageFileMetaJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.repository.ImageFileMetaJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.repository.ImageFileMetaJpaQueryRepository;
import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;
import io.hirecore.hirecorememberserver.modules.file.domain.vo.UploadStatus;
import io.hirecore.hirecorememberserver.support.FixtureMonkeyFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;

@DisplayName("ImageFileMetaJpaCommandAdapter 단위 테스트")
@ExtendWith(MockitoExtension.class)
class ImageFileMetaJpaCommandAdapterTest {

    @InjectMocks
    private ImageFileMetaJpaCommandAdapter sut;

    @Mock
    private ImageFileMetaJpaEntityMapper imageFileMetaJpaEntityMapper;

    @Mock
    private ImageFileMetaJpaCommandRepository imageFileMetaJpaCommandRepository;

    @Mock
    private ImageFileMetaJpaQueryRepository imageFileMetaJpaQueryRepository;

    private static final FixtureMonkey monkey = FixtureMonkeyFactory.monkey();

    @Test
    @DisplayName("도메인 객체를 JPA 엔티티로 변환하여 저장하고 다시 도메인으로 변환하여 반환한다")
    void should_save_and_return_domain_object() {
        // given
        ImageFileMeta domain = monkey.giveMeOne(ImageFileMeta.class);
        ImageFileMetaJpaEntity entity = monkey.giveMeOne(ImageFileMetaJpaEntity.class);
        ImageFileMetaJpaEntity savedEntity = monkey.giveMeOne(ImageFileMetaJpaEntity.class);
        ImageFileMeta expectedDomain = monkey.giveMeOne(ImageFileMeta.class);

        given(imageFileMetaJpaEntityMapper.toJpaEntity(domain)).willReturn(entity);
        given(imageFileMetaJpaCommandRepository.save(entity)).willReturn(savedEntity);
        given(imageFileMetaJpaEntityMapper.toDomain(savedEntity)).willReturn(expectedDomain);

        // when
        ImageFileMeta result = sut.save(domain);

        // then
        assertThat(result).isSameAs(expectedDomain);
        then(imageFileMetaJpaEntityMapper).should().toJpaEntity(domain);
        then(imageFileMetaJpaCommandRepository).should().save(entity);
        then(imageFileMetaJpaEntityMapper).should().toDomain(savedEntity);
    }

    @Nested
    @DisplayName("markAllAsUploaded 호출 시")
    class MarkAllAsUploadedTest {

        @Test
        @DisplayName("도메인이 결정한 completedUploadAt을 그대로 엔티티에 전달한다")
        void should_pass_domain_completed_upload_at_to_entity() {
            // given
            Instant domainCompletedUploadAt = Instant.parse("2026-05-12T03:00:00Z");
            ImageFileMeta domain = monkey.giveMeBuilder(ImageFileMeta.class)
                    .set("id", 1L)
                    .set("uploadStatus", UploadStatus.UPLOADED)
                    .set("completedUploadAt", domainCompletedUploadAt)
                    .sample();
            ImageFileMetaJpaEntity entity = mock(ImageFileMetaJpaEntity.class);
            given(entity.getId()).willReturn(1L);
            given(imageFileMetaJpaQueryRepository.findAllByIdIn(List.of(1L)))
                    .willReturn(List.of(entity));

            // when
            sut.markAllAsUploaded(List.of(domain));

            // then
            then(entity).should().updateUploadStatus(UploadStatus.UPLOADED, domainCompletedUploadAt);
            then(imageFileMetaJpaCommandRepository).should().save(entity);
        }

        @Test
        @DisplayName("입력이 비어있으면 어떤 영속화 호출도 수행하지 않는다")
        void should_skip_when_input_is_empty() {
            // when
            sut.markAllAsUploaded(List.of());

            // then
            then(imageFileMetaJpaQueryRepository).should(never()).findAllByIdIn(any());
            then(imageFileMetaJpaCommandRepository).should(never()).save(any());
        }
    }
}
