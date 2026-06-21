package io.hirecore.hirecorememberserver.modules.category.application.usecase;

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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.ArgumentMatchers.anyCollection;

/**
 * {@link LoadJobCategoryHierarchiesUseCaseImpl} 단위 테스트.
 *
 * <p>핵심 보증: 입력이 비어 있으면 포트를 호출하지 않는다. 그 외엔 포트의 결과 Map 을 그대로 반환한다.</p>
 */
@DisplayName("LoadJobCategoryHierarchiesUseCaseImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class LoadJobCategoryHierarchiesUseCaseImplTest {

    @InjectMocks
    private LoadJobCategoryHierarchiesUseCaseImpl sut;

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
    @DisplayName("leaf id 목록을 포트에 1회 위임하고 Map 결과를 그대로 반환한다")
    void should_delegate_to_port_in_single_call() {
        // given
        List<Long> leafIds = List.of(111L, 222L);
        Map<Long, List<JobCategory>> expected = Map.of(
                111L, List.of(category(1L, null, 1), category(11L, 1L, 2), category(111L, 11L, 3)),
                222L, List.of(category(2L, null, 1), category(222L, 2L, 2))
        );
        given(loadJobCategoryPort.findHierarchiesByLeafIds(leafIds)).willReturn(expected);

        // when
        Map<Long, List<JobCategory>> result = sut.execute(leafIds);

        // then
        assertThat(result).isEqualTo(expected);
        then(loadJobCategoryPort).should(only()).findHierarchiesByLeafIds(leafIds);
    }

    @Test
    @DisplayName("입력이 비어 있으면 포트를 호출하지 않고 빈 Map 을 반환한다 (SQL 발행 0회)")
    void should_short_circuit_when_input_is_empty() {
        // when
        Map<Long, List<JobCategory>> result = sut.execute(List.of());

        // then
        assertThat(result).isEmpty();
        then(loadJobCategoryPort).should(never()).findHierarchiesByLeafIds(anyCollection());
    }
}
