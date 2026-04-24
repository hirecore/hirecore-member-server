package io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa;

import com.navercorp.fixturemonkey.FixtureMonkey;
import io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.entity.ImageFileMetaJpaEntity;
import io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.mapper.ImageFileMetaJpaEntityMapper;
import io.hirecore.hirecorememberserver.modules.file.adapter.out.persistence.jpa.repository.ImageFileMetaJpaCommandRepository;
import io.hirecore.hirecorememberserver.modules.file.domain.ImageFileMeta;
import io.hirecore.hirecorememberserver.support.FixtureMonkeyFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("SaveImageFileMetaJpaAdapter 단위 테스트")
@ExtendWith(MockitoExtension.class)
class SaveImageFileMetaJpaAdapterTest {

    @InjectMocks
    private SaveImageFileMetaJpaAdapter sut;

    @Mock
    private ImageFileMetaJpaEntityMapper imageFileMetaJpaEntityMapper;

    @Mock
    private ImageFileMetaJpaCommandRepository imageFileMetaJpaCommandRepository;

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
}
