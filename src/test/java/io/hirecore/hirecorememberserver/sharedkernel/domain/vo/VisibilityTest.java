package io.hirecore.hirecorememberserver.sharedkernel.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Visibility 값 객체 단위 테스트")
class VisibilityTest {

    @Test
    @DisplayName("PUBLIC 이면 isPublic 은 true, PRIVATE 이면 false 다")
    void should_report_whether_public() {
        assertThat(Visibility.PUBLIC.isPublic()).isTrue();
        assertThat(Visibility.PRIVATE.isPublic()).isFalse();
    }
}
