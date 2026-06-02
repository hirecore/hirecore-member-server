package io.hirecore.hirecorememberserver.modules.category.application;

import io.hirecore.hirecorememberserver.modules.category.application.exception.CategoryApplicationException;
import io.hirecore.hirecorememberserver.modules.category.application.exception.CategoryApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.category.application.mapper.JobCategoryNodeMapper;
import io.hirecore.hirecorememberserver.modules.category.application.port.in.dto.response.JobCategoryNodeResponse;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("JobCategoryQueryService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class JobCategoryQueryServiceTest {

    @InjectMocks
    private JobCategoryQueryService sut;

    @Mock
    private LoadJobCategoryPort loadJobCategoryPort;

    @Mock
    private JobCategoryNodeMapper jobCategoryNodeMapper;

    private static JobCategory category(Long id, Long parentId, Integer depth, Integer sortOrder, String code) {
        Instant now = Instant.now();
        return JobCategory.builder()
                .id(id)
                .parentId(parentId)
                .categoryCode(code != null ? code : "CODE_" + id)
                .categoryName("NAME_" + id)
                .depth(depth)
                .isActive(true)
                .isAssignable(true)
                .allowsCustomInput(false)
                .sortOrder(sortOrder)
                .auditingInfo(new AuditingInfo(now, now))
                .build();
    }

    private static JobCategoryNodeResponse response(Long id, Long parentId, Integer depth, Integer sortOrder) {
        return new JobCategoryNodeResponse(id, depth, sortOrder, parentId, "NAME_" + id, "CODE_" + id, false);
    }

    @Nested
    @DisplayName("loadAllWithinDepth(Integer)")
    class LoadAllWithinDepthTest {

        @Test
        @DisplayName("Load Port 결과의 각 도메인을 응답 DTO 로 매핑하여 동일한 순서로 반환한다")
        void should_map_each_domain_into_response_preserving_order() {
            JobCategory root = category(1L, null, 1, 1, null);
            JobCategory child = category(11L, 1L, 2, 1, null);
            JobCategoryNodeResponse rootResponse = response(1L, null, 1, 1);
            JobCategoryNodeResponse childResponse = response(11L, 1L, 2, 1);

            given(loadJobCategoryPort.loadAllWithinDepth(2)).willReturn(List.of(root, child));
            given(jobCategoryNodeMapper.toResponse(root)).willReturn(rootResponse);
            given(jobCategoryNodeMapper.toResponse(child)).willReturn(childResponse);

            List<JobCategoryNodeResponse> result = sut.loadAllWithinDepth(2);

            assertThat(result).containsExactly(rootResponse, childResponse);
            then(loadJobCategoryPort).should().loadAllWithinDepth(2);
        }

        @Test
        @DisplayName("Load Port 가 빈 리스트를 반환하면 매핑 호출 없이 빈 리스트를 반환한다")
        void should_return_empty_list_when_port_returns_empty() {
            given(loadJobCategoryPort.loadAllWithinDepth(1)).willReturn(List.of());

            List<JobCategoryNodeResponse> result = sut.loadAllWithinDepth(1);

            assertThat(result).isEmpty();
            then(jobCategoryNodeMapper).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("loadIdByCode(String)")
    class LoadIdByCodeTest {

        @Test
        @DisplayName("코드에 매칭되는 카테고리가 존재하면 해당 ID를 반환한다")
        void should_return_id_when_category_exists() {
            given(loadJobCategoryPort.loadByCategoryCode("DEV_BACKEND"))
                    .willReturn(Optional.of(category(42L, null, 2, 1, "DEV_BACKEND")));

            Long result = sut.loadIdByCode("DEV_BACKEND");

            assertThat(result).isEqualTo(42L);
            then(loadJobCategoryPort).should().loadByCategoryCode("DEV_BACKEND");
        }

        @Test
        @DisplayName("코드에 매칭되는 카테고리가 없으면 JOB_CATEGORY_CODE_NOT_FOUND 예외가 발생한다")
        void should_throw_when_category_not_found() {
            given(loadJobCategoryPort.loadByCategoryCode("UNKNOWN")).willReturn(Optional.empty());

            assertThatThrownBy(() -> sut.loadIdByCode("UNKNOWN"))
                    .isInstanceOf(CategoryApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(CategoryApplicationExceptionCodeCluster.DetailResponse.JOB_CATEGORY_CODE_NOT_FOUND.getErrorCode());
        }
    }

    @Nested
    @DisplayName("loadHierarchyFromLeaf(Long)")
    class LoadHierarchyFromLeafTest {

        @Test
        @DisplayName("leaf 부터 root 까지 부모 체인을 따라 올라가며 root → leaf 순서로 반환한다")
        void should_return_hierarchy_from_root_to_leaf() {
            JobCategory leaf = category(11L, 1L, 2, 1, null);
            JobCategory root = category(1L, null, 1, 1, null);
            given(loadJobCategoryPort.loadById(11L)).willReturn(Optional.of(leaf));
            given(loadJobCategoryPort.loadById(1L)).willReturn(Optional.of(root));

            List<JobCategory> result = sut.loadHierarchyFromLeaf(11L);

            assertThat(result).extracting(JobCategory::getId).containsExactly(1L, 11L);
        }

        @Test
        @DisplayName("체인 중간에 존재하지 않는 카테고리가 있으면 JOB_CATEGORY_NOT_FOUND 예외가 발생한다")
        void should_throw_when_intermediate_category_missing() {
            given(loadJobCategoryPort.loadById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> sut.loadHierarchyFromLeaf(99L))
                    .isInstanceOf(CategoryApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(CategoryApplicationExceptionCodeCluster.DetailResponse.JOB_CATEGORY_NOT_FOUND.getErrorCode());
        }
    }
}
