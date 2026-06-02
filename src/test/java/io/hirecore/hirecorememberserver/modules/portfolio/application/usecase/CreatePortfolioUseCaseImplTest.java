package io.hirecore.hirecorememberserver.modules.portfolio.application.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.JobCategoryCommand;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.CreatePortfolioCommand;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.PortfolioContentCommand;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.PortfolioTagCommand;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.out.SavePortfolioPort;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.Portfolio;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.LoadJobCategoryPort;
import io.hirecore.hirecorememberserver.sharedkernel.application.port.out.UpdateUploadStatusOfImageFileMetaPort;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.PortfolioExternalLinkCommand;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ExternalLink;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;

@DisplayName("CreatePortfolioUseCaseImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class CreatePortfolioUseCaseImplTest {

    @InjectMocks
    private CreatePortfolioUseCaseImpl sut;

    @Mock
    private UpdateUploadStatusOfImageFileMetaPort updateUploadStatusOfImageFileMetaPort;

    @Mock
    private LoadJobCategoryPort loadJobCategoryIdByCodePort;

    @Mock
    private SavePortfolioPort savePortfolioPort;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    private static final Long MEMBER_ACCOUNT_ID = 1L;
    private static final Long JOB_CATEGORY_ID = 9L;
    private static final Long THUMBNAIL_IMAGE_ID = 100L;
    private static final List<Long> CONTENT_IMAGE_IDS = List.of(101L, 102L);

    private CreatePortfolioCommand createCommand() {
        return new CreatePortfolioCommand(
                new JobCategoryCommand("DEV_BACKEND", "백엔드 직무"),
                CollaborationType.TEAM,
                Visibility.PUBLIC,
                "회원 서비스 도메인 모델링 회고",
                "회고 작성 시 참고용 메모입니다.",
                "회원 서비스를 도메인 모델링한 회고를 정리한 글입니다.",
                THUMBNAIL_IMAGE_ID,
                CONTENT_IMAGE_IDS,
                List.of(
                        new PortfolioTagCommand("Spring", 0),
                        new PortfolioTagCommand("DDD", 1)
                ),
                List.of(),
                new PortfolioContentCommand(
                        Map.of("type", "doc"),
                        "<p>본문</p>"
                ),
                7001L,
                8001L
        );
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessTest {

        @Test
        @DisplayName("이미지 상태 전이, 카테고리 코드 해석, 포트폴리오 저장이 모두 호출되고 저장된 포트폴리오의 ID를 반환한다")
        void should_orchestrate_steps_and_return_saved_id() {
            // given
            CreatePortfolioCommand command = createCommand();
            given(loadJobCategoryIdByCodePort.findIdByCode("DEV_BACKEND")).willReturn(JOB_CATEGORY_ID);
            willAnswer(invocation -> invocation.<Portfolio>getArgument(0))
                    .given(savePortfolioPort).save(any(Portfolio.class));

            // when
            Long result = sut.execute(MEMBER_ACCOUNT_ID, command);

            // then
            assertThat(result).isNotNull();
            then(updateUploadStatusOfImageFileMetaPort).should().markUploaded(eq(MEMBER_ACCOUNT_ID), any());
            then(loadJobCategoryIdByCodePort).should().findIdByCode("DEV_BACKEND");
            then(savePortfolioPort).should().save(any(Portfolio.class));
        }

        @Test
        @DisplayName("썸네일 이미지 ID와 본문 이미지 ID가 markUploaded 호출 시 모두 전달된다")
        @SuppressWarnings("unchecked")
        void should_pass_thumbnail_and_content_image_ids_to_mark_uploaded() {
            // given
            CreatePortfolioCommand command = createCommand();
            given(loadJobCategoryIdByCodePort.findIdByCode(anyString())).willReturn(JOB_CATEGORY_ID);
            willAnswer(invocation -> invocation.<Portfolio>getArgument(0))
                    .given(savePortfolioPort).save(any(Portfolio.class));

            // when
            sut.execute(MEMBER_ACCOUNT_ID, command);

            // then
            ArgumentCaptor<Collection<Long>> captor = ArgumentCaptor.forClass(Collection.class);
            then(updateUploadStatusOfImageFileMetaPort).should()
                    .markUploaded(eq(MEMBER_ACCOUNT_ID), captor.capture());

            assertThat(captor.getValue())
                    .containsExactly(THUMBNAIL_IMAGE_ID, 101L, 102L);
        }

        @Test
        @DisplayName("썸네일 이미지 ID가 null이면 본문 이미지 ID만 markUploaded에 전달된다")
        @SuppressWarnings("unchecked")
        void should_pass_only_content_image_ids_when_thumbnail_is_null() {
            // given
            CreatePortfolioCommand command = new CreatePortfolioCommand(
                    new JobCategoryCommand("DEV_BACKEND", null),
                    CollaborationType.PERSONAL, Visibility.PRIVATE,
                    "title", null, "한 줄 소개", null, CONTENT_IMAGE_IDS, null, null,
                    new PortfolioContentCommand(Map.of("type", "doc"), "<p>x</p>"),
                    null, null
            );
            given(loadJobCategoryIdByCodePort.findIdByCode(anyString())).willReturn(JOB_CATEGORY_ID);
            willAnswer(invocation -> invocation.<Portfolio>getArgument(0))
                    .given(savePortfolioPort).save(any(Portfolio.class));

            // when
            sut.execute(MEMBER_ACCOUNT_ID, command);

            // then
            ArgumentCaptor<Collection<Long>> captor = ArgumentCaptor.forClass(Collection.class);
            then(updateUploadStatusOfImageFileMetaPort).should()
                    .markUploaded(eq(MEMBER_ACCOUNT_ID), captor.capture());

            assertThat(captor.getValue()).containsExactly(101L, 102L);
        }

        @Test
        @DisplayName("이미지 ID가 모두 비어있으면 빈 컬렉션으로 markUploaded를 호출한다")
        @SuppressWarnings("unchecked")
        void should_call_mark_uploaded_with_empty_when_no_images() {
            // given
            CreatePortfolioCommand command = new CreatePortfolioCommand(
                    new JobCategoryCommand("DEV_BACKEND", null),
                    CollaborationType.PERSONAL, Visibility.PRIVATE,
                    "title", null, "한 줄 소개", null, null, null, null,
                    new PortfolioContentCommand(Map.of("type", "doc"), "<p>x</p>"),
                    null, null
            );
            given(loadJobCategoryIdByCodePort.findIdByCode(anyString())).willReturn(JOB_CATEGORY_ID);
            willAnswer(invocation -> invocation.<Portfolio>getArgument(0))
                    .given(savePortfolioPort).save(any(Portfolio.class));

            // when
            sut.execute(MEMBER_ACCOUNT_ID, command);

            // then
            ArgumentCaptor<Collection<Long>> captor = ArgumentCaptor.forClass(Collection.class);
            then(updateUploadStatusOfImageFileMetaPort).should()
                    .markUploaded(eq(MEMBER_ACCOUNT_ID), captor.capture());

            assertThat(captor.getValue()).isEmpty();
        }

        @Test
        @DisplayName("저장되는 Portfolio에 회원/카테고리/제목/협업유형/공개범위가 정확히 전달된다")
        void should_build_portfolio_with_correct_fields() {
            // given
            CreatePortfolioCommand command = createCommand();
            given(loadJobCategoryIdByCodePort.findIdByCode("DEV_BACKEND")).willReturn(JOB_CATEGORY_ID);
            willAnswer(invocation -> invocation.<Portfolio>getArgument(0))
                    .given(savePortfolioPort).save(any(Portfolio.class));

            // when
            sut.execute(MEMBER_ACCOUNT_ID, command);

            // then
            ArgumentCaptor<Portfolio> captor = ArgumentCaptor.forClass(Portfolio.class);
            then(savePortfolioPort).should().save(captor.capture());

            Portfolio captured = captor.getValue();
            assertThat(captured.getMemberAccountId()).isEqualTo(MEMBER_ACCOUNT_ID);
            assertThat(captured.getPortfolioJobCategory().getJobCategoryId()).isEqualTo(JOB_CATEGORY_ID);
            assertThat(captured.getPortfolioJobCategory().getUserInput()).isEqualTo("백엔드 직무");
            assertThat(captured.getTitle()).isEqualTo("회원 서비스 도메인 모델링 회고");
            assertThat(captured.getCollaborationType()).isEqualTo(CollaborationType.TEAM);
            assertThat(captured.getVisibility()).isEqualTo(Visibility.PUBLIC);
            assertThat(captured.getThumbnailImageId()).isEqualTo(THUMBNAIL_IMAGE_ID);
        }
    }

    @Nested
    @DisplayName("가공 책임 (도메인이 아닌 use case 측에서 수행)")
    class TransformationTest {

        @Test
        @DisplayName("클라이언트가 보낸 previewSummary 가 도메인에 그대로 전달된다")
        void should_pass_preview_summary_through_as_is() {
            // given
            String userPreview = "협업 에디터를 직접 구현해본 경험을 정리한 글";
            CreatePortfolioCommand command = new CreatePortfolioCommand(
                    new JobCategoryCommand("DEV_BACKEND", null),
                    CollaborationType.TEAM, Visibility.PUBLIC,
                    "title", null, userPreview, null, null, null, null,
                    new PortfolioContentCommand(Map.of("type", "doc"), "<p>본문</p>"),
                    null, null
            );
            given(loadJobCategoryIdByCodePort.findIdByCode(anyString())).willReturn(JOB_CATEGORY_ID);
            willAnswer(invocation -> invocation.<Portfolio>getArgument(0))
                    .given(savePortfolioPort).save(any(Portfolio.class));

            // when
            sut.execute(MEMBER_ACCOUNT_ID, command);

            // then
            ArgumentCaptor<Portfolio> captor = ArgumentCaptor.forClass(Portfolio.class);
            then(savePortfolioPort).should().save(captor.capture());
            assertThat(captor.getValue().getPreviewSummary()).isEqualTo(userPreview);
        }

        @Test
        @DisplayName("tags Command 리스트가 PortfolioTag 리스트로 매핑되며 클라이언트가 보낸 sortOrder 가 도메인에 그대로 전달된다")
        void should_map_tag_commands_to_portfolio_tags_with_sort_order() {
            // given
            CreatePortfolioCommand command = createCommand();
            given(loadJobCategoryIdByCodePort.findIdByCode(anyString())).willReturn(JOB_CATEGORY_ID);
            willAnswer(invocation -> invocation.<Portfolio>getArgument(0))
                    .given(savePortfolioPort).save(any(Portfolio.class));

            // when
            sut.execute(MEMBER_ACCOUNT_ID, command);

            // then
            ArgumentCaptor<Portfolio> captor = ArgumentCaptor.forClass(Portfolio.class);
            then(savePortfolioPort).should().save(captor.capture());
            assertThat(captor.getValue().getPortfolioTags())
                    .extracting("userInputTag", "sortOrder")
                    .containsExactly(
                            org.assertj.core.groups.Tuple.tuple("Spring", 0),
                            org.assertj.core.groups.Tuple.tuple("DDD", 1)
                    );
        }

        @Test
        @DisplayName("tags가 null이면 도메인에는 빈 PortfolioTag 리스트가 전달된다")
        void should_pass_empty_tags_when_input_is_null() {
            // given
            CreatePortfolioCommand command = new CreatePortfolioCommand(
                    new JobCategoryCommand("DEV_BACKEND", null),
                    CollaborationType.PERSONAL, Visibility.PRIVATE,
                    "title", null, "한 줄 소개", null, null, null, null,
                    new PortfolioContentCommand(Map.of("type", "doc"), "<p>x</p>"),
                    null, null
            );
            given(loadJobCategoryIdByCodePort.findIdByCode(anyString())).willReturn(JOB_CATEGORY_ID);
            willAnswer(invocation -> invocation.<Portfolio>getArgument(0))
                    .given(savePortfolioPort).save(any(Portfolio.class));

            // when
            sut.execute(MEMBER_ACCOUNT_ID, command);

            // then
            ArgumentCaptor<Portfolio> captor = ArgumentCaptor.forClass(Portfolio.class);
            then(savePortfolioPort).should().save(captor.capture());
            assertThat(captor.getValue().getPortfolioTags()).isEmpty();
        }

        @Test
        @DisplayName("externalLinks가 null이면 도메인에는 빈 ExternalLink 리스트가 전달된다")
        void should_coerce_null_external_links_to_empty() {
            // given — externalLinks=null
            CreatePortfolioCommand command = new CreatePortfolioCommand(
                    new JobCategoryCommand("DEV_BACKEND", null),
                    CollaborationType.PERSONAL, Visibility.PRIVATE,
                    "title", null, "한 줄 소개", null, null, null, null,
                    new PortfolioContentCommand(Map.of("type", "doc"), "<p>x</p>"),
                    null, null
            );
            given(loadJobCategoryIdByCodePort.findIdByCode(anyString())).willReturn(JOB_CATEGORY_ID);
            willAnswer(invocation -> invocation.<Portfolio>getArgument(0))
                    .given(savePortfolioPort).save(any(Portfolio.class));

            // when
            sut.execute(MEMBER_ACCOUNT_ID, command);

            // then
            ArgumentCaptor<Portfolio> captor = ArgumentCaptor.forClass(Portfolio.class);
            then(savePortfolioPort).should().save(captor.capture());
            assertThat(captor.getValue().getExternalLinks()).isEmpty();
        }

        @Test
        @DisplayName("externalLinks가 채워져 있으면 도메인에 그대로 전달된다")
        void should_pass_external_links_as_is() {
            // given
            PortfolioExternalLinkCommand linkCommand = new PortfolioExternalLinkCommand("Repo", "https://github.com/example");
            CreatePortfolioCommand command = new CreatePortfolioCommand(
                    new JobCategoryCommand("DEV_BACKEND", null),
                    CollaborationType.PERSONAL, Visibility.PRIVATE,
                    "title", null, "한 줄 소개", null, null, null, List.of(linkCommand),
                    new PortfolioContentCommand(Map.of("type", "doc"), "<p>x</p>"),
                    null, null
            );
            given(loadJobCategoryIdByCodePort.findIdByCode(anyString())).willReturn(JOB_CATEGORY_ID);
            willAnswer(invocation -> invocation.<Portfolio>getArgument(0))
                    .given(savePortfolioPort).save(any(Portfolio.class));

            // when
            sut.execute(MEMBER_ACCOUNT_ID, command);

            // then
            ArgumentCaptor<Portfolio> captor = ArgumentCaptor.forClass(Portfolio.class);
            then(savePortfolioPort).should().save(captor.capture());
            assertThat(captor.getValue().getExternalLinks())
                    .containsExactly(new ExternalLink(linkCommand.label(), linkCommand.url()));
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailureTest {

        @Test
        @DisplayName("markUploaded에서 예외가 발생하면 카테고리 조회와 저장은 호출되지 않는다")
        void should_short_circuit_when_mark_uploaded_fails() {
            // given
            CreatePortfolioCommand command = createCommand();
            doThrow(new RuntimeException("simulated"))
                    .when(updateUploadStatusOfImageFileMetaPort)
                    .markUploaded(eq(MEMBER_ACCOUNT_ID), any());

            // when & then
            assertThatThrownBy(() -> sut.execute(MEMBER_ACCOUNT_ID, command))
                    .isInstanceOf(RuntimeException.class);

            then(loadJobCategoryIdByCodePort).should(never()).findIdByCode(anyString());
            then(savePortfolioPort).should(never()).save(any());
        }
    }
}
