package io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.mapper;

import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.request.JobCategoryApiRequest;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.request.CreatePortfolioApiRequest;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.request.PortfolioContentApiRequest;
import io.hirecore.hirecorememberserver.modules.portfolio.adapter.in.web.dto.request.PortfolioTagApiRequest;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.CreatePortfolioCommand;
import io.hirecore.hirecorememberserver.modules.portfolio.application.port.in.dto.request.PortfolioTagCommand;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.CollaborationTypeApiValue;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.ExternalLinkApiValue;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.dto.value.VisibilityApiValue;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.in.web.mapper.SharedDomainVoWebMapperImpl;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ExternalLink;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PortfolioWebMapper 단위 테스트")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PortfolioWebMapperImpl.class, SharedDomainVoWebMapperImpl.class})
class PortfolioWebMapperTest {

    @Autowired
    private PortfolioWebMapper mapper;

    @Nested
    @DisplayName("toCreatePortfolioCommand() — API 요청을 Command로 변환")
    class ToCommandTest {

        @Test
        @DisplayName("모든 필드가 채워진 요청은 동일 값으로 매핑된다")
        void should_map_all_fields() {
            // given
            CreatePortfolioApiRequest request = new CreatePortfolioApiRequest(
                    new JobCategoryApiRequest("DEV_BACKEND", "백엔드 직무"),
                    CollaborationTypeApiValue.TEAM,
                    VisibilityApiValue.PUBLIC,
                    "회원 서비스 도메인 모델링 회고",
                    "메모입니다.",
                    "회원 서비스를 도메인 모델링한 회고를 정리한 글입니다.",
                    100L,
                    List.of(101L, 102L),
                    List.of(
                            new PortfolioTagApiRequest("Spring", 0),
                            new PortfolioTagApiRequest("DDD", 1)
                    ),
                    List.of(new ExternalLinkApiValue("GitHub", "https://github.com/example/repo")),
                    new PortfolioContentApiRequest(
                            Map.of("type", "doc"),
                            "<p>본문</p>"
                    ),
                    7001L,
                    8001L
            );

            // when
            CreatePortfolioCommand command = mapper.toCreatePortfolioCommand(request);

            // then
            assertThat(command.jobCategory()).isNotNull();
            assertThat(command.jobCategory().code()).isEqualTo("DEV_BACKEND");
            assertThat(command.jobCategory().customJobCategoryName()).isEqualTo("백엔드 직무");
            assertThat(command.collaborationType()).isEqualTo(CollaborationType.TEAM);
            assertThat(command.visibility()).isEqualTo(Visibility.PUBLIC);
            assertThat(command.title()).isEqualTo("회원 서비스 도메인 모델링 회고");
            assertThat(command.privateMemo()).isEqualTo("메모입니다.");
            assertThat(command.previewSummary()).isEqualTo("회원 서비스를 도메인 모델링한 회고를 정리한 글입니다.");
            assertThat(command.thumbnailImageId()).isEqualTo(100L);
            assertThat(command.contentImageIds()).containsExactly(101L, 102L);
            assertThat(command.tags())
                    .extracting(PortfolioTagCommand::userInputTag, PortfolioTagCommand::sortOrder)
                    .containsExactly(
                            org.assertj.core.groups.Tuple.tuple("Spring", 0),
                            org.assertj.core.groups.Tuple.tuple("DDD", 1)
                    );
            assertThat(command.externalLinks())
                    .extracting(ExternalLink::url)
                    .containsExactly("https://github.com/example/repo");
            assertThat(command.linkedResumeId()).isEqualTo(7001L);
            assertThat(command.linkedCoverLetterId()).isEqualTo(8001L);
            assertThat(command.content().json()).containsEntry("type", "doc");
            assertThat(command.content().html()).isEqualTo("<p>본문</p>");
        }

        @Test
        @DisplayName("선택 필드(customJobCategoryName, privateMemo, thumbnailImageId 등)가 null이어도 정상 매핑된다")
        void should_map_request_with_nullable_fields_omitted() {
            // given — 필수 필드만 채움
            CreatePortfolioApiRequest request = new CreatePortfolioApiRequest(
                    new JobCategoryApiRequest("DEV_BACKEND", null),
                    CollaborationTypeApiValue.PERSONAL,
                    VisibilityApiValue.PRIVATE,
                    "title",
                    null,
                    "한 줄 소개",
                    null,
                    null,
                    null,
                    null,
                    new PortfolioContentApiRequest(Map.of("type", "doc"), "<p>본문</p>"),
                    null,
                    null
            );

            // when
            CreatePortfolioCommand command = mapper.toCreatePortfolioCommand(request);

            // then
            assertThat(command.jobCategory().code()).isEqualTo("DEV_BACKEND");
            assertThat(command.jobCategory().customJobCategoryName()).isNull();
            assertThat(command.previewSummary()).isEqualTo("한 줄 소개");
            assertThat(command.thumbnailImageId()).isNull();
            assertThat(command.contentImageIds()).isNull();
            assertThat(command.tags()).isNull();
            assertThat(command.externalLinks()).isNull();
            assertThat(command.linkedResumeId()).isNull();
            assertThat(command.linkedCoverLetterId()).isNull();
        }

        @Test
        @DisplayName("null 요청을 받으면 null을 반환한다")
        void should_return_null_when_request_is_null() {
            // when & then
            assertThat(mapper.toCreatePortfolioCommand(null)).isNull();
        }
    }
}
