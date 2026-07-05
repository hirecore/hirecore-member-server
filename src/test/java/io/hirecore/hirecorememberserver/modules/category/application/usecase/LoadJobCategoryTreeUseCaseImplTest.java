package io.hirecore.hirecorememberserver.modules.category.application.usecase;

import io.hirecore.hirecorememberserver.modules.category.application.mapper.JobCategoryMapper;
import io.hirecore.hirecorememberserver.modules.category.application.port.in.LoadJobCategoryTreeUseCase;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

/**
 * {@link LoadJobCategoryTreeUseCaseImpl} 단위 테스트.
 *
 * <p>Load Port 로부터 받은 도메인 리스트를 매퍼로 변환해 응답 DTO 리스트로
 * 정확히 반환하는지 검증합니다. 정렬·필터링 로직은 어댑터 책임이므로 여기서는 다루지 않습니다.</p>
 */
@DisplayName("LoadJobCategoryTreeUseCaseImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class LoadJobCategoryTreeUseCaseImplTest {

    @InjectMocks
    private LoadJobCategoryTreeUseCaseImpl useCase;

    @Mock
    private LoadJobCategoryPort loadJobCategoryPort;

    @Mock
    private JobCategoryMapper jobCategoryMapper;

    private static JobCategory category(Long id, Long parentId, Integer depth, Integer sortOrder) {
        Instant now = Instant.now();
        return JobCategory.builder()
                .id(id)
                .parentId(parentId)
                .categoryCode("CODE_" + id)
                .categoryName("NAME_" + id)
                .depth(depth)
                .isActive(true)
                .isAssignable(true)
                .allowsCustomInput(false)
                .sortOrder(sortOrder)
                .auditingInfo(new AuditingInfo(now, now))
                .build();
    }

    private static LoadJobCategoryTreeUseCase.Response response(Long id, Long parentId, Integer depth, Integer sortOrder) {
        return new LoadJobCategoryTreeUseCase.Response(id, depth, sortOrder, parentId, "NAME_" + id, "CODE_" + id, false);
    }

    @Nested
    @DisplayName("정상 흐름")
    class HappyPathTest {

        @Test
        @DisplayName("Load Port 결과의 각 도메인을 응답 DTO 로 매핑하여 동일한 순서로 반환한다")
        void should_map_each_domain_into_response_preserving_order() {
            // given
            JobCategory root = category(1L, null, 1, 1);
            JobCategory child = category(11L, 1L, 2, 1);
            LoadJobCategoryTreeUseCase.Response rootResponse = response(1L, null, 1, 1);
            LoadJobCategoryTreeUseCase.Response childResponse = response(11L, 1L, 2, 1);

            given(loadJobCategoryPort.findAllWithinDepth(2))
                    .willReturn(List.of(root, child));
            given(jobCategoryMapper.toResponse(root)).willReturn(rootResponse);
            given(jobCategoryMapper.toResponse(child)).willReturn(childResponse);

            // when
            List<LoadJobCategoryTreeUseCase.Response> result = useCase.execute(2);

            // then
            assertThat(result).containsExactly(rootResponse, childResponse);
            then(loadJobCategoryPort).should().findAllWithinDepth(2);
            then(jobCategoryMapper).should().toResponse(root);
            then(jobCategoryMapper).should().toResponse(child);
        }

        @Test
        @DisplayName("Load Port 가 빈 리스트를 반환하면 매핑 호출 없이 빈 리스트를 반환한다")
        void should_return_empty_list_when_query_service_returns_empty() {
            // given
            given(loadJobCategoryPort.findAllWithinDepth(1)).willReturn(List.of());

            // when
            List<LoadJobCategoryTreeUseCase.Response> result = useCase.execute(1);

            // then
            assertThat(result).isEmpty();
            then(jobCategoryMapper).shouldHaveNoInteractions();
        }
    }
}
