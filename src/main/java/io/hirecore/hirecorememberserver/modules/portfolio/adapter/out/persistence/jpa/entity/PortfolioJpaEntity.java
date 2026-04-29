package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity;

import io.hirecore.hirecorememberserver.sharedkernel.infrastructure.persistence.jpa.AbstractPersistableAggregateRoot;
import io.hirecore.hirecorememberserver.sharedkernel.infrastructure.persistence.jpa.AuditingJpaInfo;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.vo.ExternalLink;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.vo.PortfolioStatus;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.vo.PortfolioType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "portfolios")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class PortfolioJpaEntity extends AbstractPersistableAggregateRoot<Long> {
    @Id
    private Long id;

    @Comment("회원 계정 ID")
    @Column(name = "member_account_id", nullable = false)
    private Long memberAccountId;

    @Comment("포트폴리오 직무 카테고리 ID")
    @Column(name = "portfolio_job_category_id", nullable = false)
    private Long portfolioJobCategoryId;

    @Comment("썸네일 이미지 ID")
    @Column(name = "thumbnail_image_id")
    private Long thumbnailImageId;

    @Comment("자기소개서 ID")
    @Column(name = "cover_letter_id")
    private Long coverLetterId;

    @Comment("이력서 ID")
    @Column(name = "resume_id")
    private Long resumeId;

    @OneToOne(
            mappedBy = "portfolio",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private PortfolioContentJpaEntity portfolioContent;

    @Comment("포트폴리오 제목")
    @Column(name = "title", nullable = false, columnDefinition = "VARCHAR(200)")
    private String title;

    @Comment("미리보기 요약")
    @Column(name = "preview_summary", nullable = false, columnDefinition = "VARCHAR(500)")
    private String previewSummary;

    @Comment("포트폴리오 상태")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(30)")
    private PortfolioStatus status;

    @Comment("포트폴리오 유형")
    @Enumerated(EnumType.STRING)
    @Column(name = "portfolio_type", nullable = false, columnDefinition = "VARCHAR(30)")
    private PortfolioType portfolioType;

    @Comment("사용자 입력 외부 링크")
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "external_link_json", columnDefinition = "json")
    private List<ExternalLink> externalLinks = new ArrayList<>();

    @Comment("나만보기 메모")
    @Column(name = "private_memo", columnDefinition = "TEXT")
    private String privateMemo;

    @Comment("공개 범위")
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, columnDefinition = "VARCHAR(30)")
    private Visibility visibility;

    @Embedded
    private AuditingJpaInfo auditingInfo;

    /**
     * 양방향 @OneToOne 관계의 양쪽 참조를 동기화하는 JPA 메커니즘 헬퍼.
     * cascade/orphanRemoval이 정상 동작하도록 매퍼에서 사용됩니다.
     */
    public void syncPortfolioContent(PortfolioContentJpaEntity newContent) {
        this.portfolioContent = newContent;
        if (newContent != null) {
            newContent.setPortfolio(this);
        }
    }
}
