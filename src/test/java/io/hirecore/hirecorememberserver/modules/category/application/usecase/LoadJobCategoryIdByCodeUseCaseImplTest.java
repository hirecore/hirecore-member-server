package io.hirecore.hirecorememberserver.modules.category.application.usecase;

import io.hirecore.hirecorememberserver.modules.category.application.exception.CategoryApplicationException;
import io.hirecore.hirecorememberserver.modules.category.application.port.out.LoadJobCategoryPort;
import io.hirecore.hirecorememberserver.modules.category.domain.JobCategory;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("LoadJobCategoryIdByCodeUseCaseImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class LoadJobCategoryIdByCodeUseCaseImplTest {

    @InjectMocks
    private LoadJobCategoryIdByCodeUseCaseImpl sut;

    @Mock
    private LoadJobCategoryPort loadJobCategoryPort;

    private static JobCategory category(Long id, String code) {
        Instant now = Instant.now();
        return JobCategory.builder()
                .id(id)
                .parentId(null)
                .categoryCode(code)
                .categoryName("백엔드")
                .depth(2)
                .isActive(true)
                .isAssignable(true)
                .allowsCustomInput(false)
                .sortOrder(1)
                .auditingInfo(new AuditingInfo(now, now))
                .build();
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessTest {

        @Test
        @DisplayName("코드에 매칭되는 카테고리가 존재하면 해당 ID를 반환한다")
        void should_return_id_when_category_exists() {
            // given
            given(loadJobCategoryPort.loadByCategoryCode("DEV_BACKEND"))
                    .willReturn(Optional.of(category(42L, "DEV_BACKEND")));

            // when
            Long result = sut.execute("DEV_BACKEND");

            // then
            assertThat(result).isEqualTo(42L);
            then(loadJobCategoryPort).should().loadByCategoryCode("DEV_BACKEND");
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailureTest {

        @Test
        @DisplayName("코드에 매칭되는 카테고리가 없으면 JOB_CATEGORY_CODE_NOT_FOUND 예외가 발생한다")
        void should_throw_when_category_not_found() {
            // given
            given(loadJobCategoryPort.loadByCategoryCode("UNKNOWN")).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.execute("UNKNOWN"))
                    .isInstanceOf(CategoryApplicationException.class);
        }
    }
}
