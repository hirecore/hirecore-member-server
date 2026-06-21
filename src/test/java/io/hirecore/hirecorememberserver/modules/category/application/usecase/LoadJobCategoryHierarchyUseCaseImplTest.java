package io.hirecore.hirecorememberserver.modules.category.application.usecase;

import io.hirecore.hirecorememberserver.modules.category.application.exception.CategoryApplicationException;
import io.hirecore.hirecorememberserver.modules.category.application.exception.CategoryApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.category.application.port.out.LoadJobCategoryPort;
import io.hirecore.hirecorememberserver.modules.category.domain.JobCategory;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.AuditingInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.only;

/**
 * {@link LoadJobCategoryHierarchyUseCaseImpl} 단위 테스트.
 *
 * <p>핵심 보증: 포트의 일괄 조회 메서드를 <b>단 한 번</b> 호출해 root → leaf 경로를 그대로 반환한다는 것.
 * 깊이만큼 반복 호출하던 기존 재귀 흐름이 제거됐음을 명시적으로 검증한다.</p>
 */
@DisplayName("LoadJobCategoryHierarchyUseCaseImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class LoadJobCategoryHierarchyUseCaseImplTest {

    @InjectMocks
    private LoadJobCategoryHierarchyUseCaseImpl sut;

    @Mock
    private LoadJobCategoryPort loadJobCategoryPort;

    private static JobCategory category(Long id, Long parentId, Integer depth) {
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
                .sortOrder(0)
                .auditingInfo(new AuditingInfo(now, now))
                .build();
    }

    @Test
    @DisplayName("leaf id 로 포트의 일괄 조회를 1회만 호출하여 root → leaf 경로를 그대로 반환한다")
    void should_delegate_to_port_in_single_call() {
        // given - depth 3 짜리 경로
        JobCategory root = category(1L, null, 1);
        JobCategory mid = category(11L, 1L, 2);
        JobCategory leaf = category(111L, 11L, 3);
        given(loadJobCategoryPort.findHierarchyByLeafId(111L))
                .willReturn(List.of(root, mid, leaf));

        // when
        List<JobCategory> result = sut.execute(111L);

        // then
        assertThat(result).containsExactly(root, mid, leaf);
        then(loadJobCategoryPort).should(only()).findHierarchyByLeafId(111L);
    }

    @Test
    @DisplayName("포트가 빈 리스트를 반환하면 JOB_CATEGORY_NOT_FOUND 예외를 던진다")
    void should_throw_not_found_when_port_returns_empty() {
        // given - 존재하지 않는 leaf
        given(loadJobCategoryPort.findHierarchyByLeafId(999L)).willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> sut.execute(999L))
                .isInstanceOf(CategoryApplicationException.class)
                .extracting("errorCode")
                .isEqualTo(CategoryApplicationExceptionCodeCluster.DetailResponse.JOB_CATEGORY_NOT_FOUND.getErrorCode());
    }
}
