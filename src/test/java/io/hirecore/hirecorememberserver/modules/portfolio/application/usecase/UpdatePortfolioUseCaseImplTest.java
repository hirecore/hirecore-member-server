package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationException;
import io.hirecore.hirecorememberserver.modules.portfolio.application.exception.PortfolioApplicationExceptionCodeCluster;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioDomainException;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.in.dto.SharedCommandDto;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.UpdatePortfolioUseCase;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.LoadPortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.UpdatePortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadJobCategorySharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.MarkImagesAsUploadedSharedPort;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;

@DisplayName("UpdatePortfolioUseCaseImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class UpdatePortfolioUseCaseImplTest {

    @InjectMocks
    private UpdatePortfolioUseCaseImpl sut;

    @Mock
    private LoadPortfolioPort loadPortfolioPort;

    @Mock
    private UpdatePortfolioPort updatePortfolioPort;

    @Mock
    private LoadJobCategorySharedPort loadJobCategoryPort;

    @Mock
    private MarkImagesAsUploadedSharedPort markImagesAsUploadedPort;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    private static final Long OWNER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;
    private static final Long PORTFOLIO_ID = 9001L;
    private static final Long JOB_CATEGORY_ID = 9L;
    private static final Long THUMBNAIL_IMAGE_ID = 100L;
    private static final List<Long> CONTENT_IMAGE_IDS = List.of(101L, 102L);

    private static Portfolio existingPortfolio(Long ownerId) {
        return Portfolio.create(
                ownerId,
                THUMBNAIL_IMAGE_ID,
                null,
                null,
                "기존 제목",
                "기존 미리보기",
                null,
                JOB_CATEGORY_ID,
                null,
                "{\"type\":\"doc\"}",
                "<p>기존 본문</p>",
                CONTENT_IMAGE_IDS,
                List.of(),
                List.of(),
                CollaborationType.PERSONAL,
                Visibility.PRIVATE
        );
    }

    private static UpdatePortfolioUseCase.Command validUpdateCommand() {
        return new UpdatePortfolioUseCase.Command(
                new SharedCommandDto.LeafJobCategory("DEV_BACKEND", "백엔드 직무"),
                CollaborationType.TEAM,
                Visibility.PUBLIC,
                "수정된 제목",
                "수정 시 참고 메모",
                "수정된 미리보기",
                THUMBNAIL_IMAGE_ID,
                CONTENT_IMAGE_IDS,
                List.of(new SharedCommandDto.SequentialTag("Spring", 0)),
                List.of(),
                new SharedCommandDto.RichTextContent(Map.of("type", "doc"), "<p>수정 본문</p>"),
                7001L,
                8001L
        );
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessTest {

        @Test
        @DisplayName("작성자 본인이 호출하면 markUploaded → jobCategory 해석 → portfolio.modify → update 가 순서대로 호출되고 portfolioId 를 반환한다")
        void should_orchestrate_and_return_id() {
            // given
            Portfolio loaded = existingPortfolio(OWNER_ID);
            given(loadPortfolioPort.findById(PORTFOLIO_ID)).willReturn(Optional.of(loaded));
            given(loadJobCategoryPort.findIdByCode("DEV_BACKEND")).willReturn(JOB_CATEGORY_ID);

            // when
            Long result = sut.execute(PORTFOLIO_ID, OWNER_ID, validUpdateCommand());

            // then
            assertThat(result).isEqualTo(loaded.getId());
            then(markImagesAsUploadedPort).should().markUploaded(eq(OWNER_ID), any());
            then(loadJobCategoryPort).should().findIdByCode("DEV_BACKEND");
            then(updatePortfolioPort).should().update(loaded);
        }

        @Test
        @DisplayName("썸네일 이미지 ID 와 본문 이미지 ID 가 markUploaded 호출에 모두 전달된다")
        @SuppressWarnings("unchecked")
        void should_pass_thumbnail_and_content_image_ids_to_mark_uploaded() {
            // given
            Portfolio loaded = existingPortfolio(OWNER_ID);
            given(loadPortfolioPort.findById(PORTFOLIO_ID)).willReturn(Optional.of(loaded));
            given(loadJobCategoryPort.findIdByCode(anyString())).willReturn(JOB_CATEGORY_ID);

            // when
            sut.execute(PORTFOLIO_ID, OWNER_ID, validUpdateCommand());

            // then
            ArgumentCaptor<Collection<Long>> captor = ArgumentCaptor.forClass(Collection.class);
            then(markImagesAsUploadedPort).should()
                    .markUploaded(eq(OWNER_ID), captor.capture());
            assertThat(captor.getValue()).containsExactly(THUMBNAIL_IMAGE_ID, 101L, 102L);
        }

        @Test
        @DisplayName("수정 명령의 필드들이 portfolio 도메인에 반영된다")
        void should_apply_command_fields_to_portfolio() {
            // given
            Portfolio loaded = existingPortfolio(OWNER_ID);
            given(loadPortfolioPort.findById(PORTFOLIO_ID)).willReturn(Optional.of(loaded));
            given(loadJobCategoryPort.findIdByCode(anyString())).willReturn(JOB_CATEGORY_ID);

            // when
            sut.execute(PORTFOLIO_ID, OWNER_ID, validUpdateCommand());

            // then — 도메인이 새 값으로 교체되었음
            assertThat(loaded.getTitle()).isEqualTo("수정된 제목");
            assertThat(loaded.getPreviewSummary()).isEqualTo("수정된 미리보기");
            assertThat(loaded.getCollaborationType()).isEqualTo(CollaborationType.TEAM);
            assertThat(loaded.getVisibility()).isEqualTo(Visibility.PUBLIC);
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailureTest {

        @Test
        @DisplayName("존재하지 않는 portfolioId 면 PORTFOLIO_NOT_FOUND 예외를 던지고 후속 호출은 없다")
        void should_throw_when_portfolio_not_found() {
            // given
            given(loadPortfolioPort.findById(PORTFOLIO_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.execute(PORTFOLIO_ID, OWNER_ID, validUpdateCommand()))
                    .isInstanceOf(PortfolioApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_NOT_FOUND.getErrorCode());

            then(markImagesAsUploadedPort).should(never()).markUploaded(anyLong(), any());
            then(loadJobCategoryPort).should(never()).findIdByCode(anyString());
            then(updatePortfolioPort).should(never()).update(any());
        }

        @Test
        @DisplayName("작성자가 아닌 사용자가 수정을 시도하면 PORTFOLIO_FORBIDDEN 예외를 던지고 후속 호출은 없다")
        void should_throw_when_viewer_is_not_owner() {
            // given
            Portfolio loaded = existingPortfolio(OWNER_ID);
            given(loadPortfolioPort.findById(PORTFOLIO_ID)).willReturn(Optional.of(loaded));

            // when & then
            assertThatThrownBy(() -> sut.execute(PORTFOLIO_ID, OTHER_USER_ID, validUpdateCommand()))
                    .isInstanceOf(PortfolioApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_FORBIDDEN.getErrorCode());

            then(markImagesAsUploadedPort).should(never()).markUploaded(anyLong(), any());
            then(loadJobCategoryPort).should(never()).findIdByCode(anyString());
            then(updatePortfolioPort).should(never()).update(any());
        }

        @Test
        @DisplayName("viewerId 가 null 이면 PORTFOLIO_FORBIDDEN 예외를 던진다")
        void should_throw_when_viewer_id_is_null() {
            // given
            Portfolio loaded = existingPortfolio(OWNER_ID);
            given(loadPortfolioPort.findById(PORTFOLIO_ID)).willReturn(Optional.of(loaded));

            // when & then
            assertThatThrownBy(() -> sut.execute(PORTFOLIO_ID, null, validUpdateCommand()))
                    .isInstanceOf(PortfolioApplicationException.class)
                    .extracting("errorCode")
                    .isEqualTo(PortfolioApplicationExceptionCodeCluster.DetailResponse.PORTFOLIO_FORBIDDEN.getErrorCode());
        }

        @Test
        @DisplayName("markUploaded 에서 예외가 발생하면 카테고리 조회와 update 는 호출되지 않는다")
        void should_short_circuit_when_mark_uploaded_fails() {
            // given
            Portfolio loaded = existingPortfolio(OWNER_ID);
            given(loadPortfolioPort.findById(PORTFOLIO_ID)).willReturn(Optional.of(loaded));
            doThrow(new RuntimeException("simulated"))
                    .when(markImagesAsUploadedPort)
                    .markUploaded(eq(OWNER_ID), any());

            // when & then
            assertThatThrownBy(() -> sut.execute(PORTFOLIO_ID, OWNER_ID, validUpdateCommand()))
                    .isInstanceOf(RuntimeException.class);

            then(loadJobCategoryPort).should(never()).findIdByCode(anyString());
            then(updatePortfolioPort).should(never()).update(any());
        }
    }
}
