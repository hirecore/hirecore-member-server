package io.hirecore.hirecorememberserver.modules.portfolio.adapter.persistence.jpa.entity;

import io.hirecore.hirecorememberserver.common.adapter.out.persistence.jpa.AbstractPersistableAggregateRoot;
import io.hirecore.hirecorememberserver.common.adapter.out.persistence.jpa.vo.AuditingJpaInfo;
import io.hirecore.hirecorememberserver.common.utils.AssertionUtils;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioDomainExceptionCodeCluster;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

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

    @Comment("포트폴리오 카테고리 ID")
    @Column(name = "portfolio_category_id", nullable = false)
    private Long portfolioCategoryId;

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
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(30)")
    private String status;

    @Comment("공개 여부")
    @Column(name = "visibility", nullable = false)
    private Boolean visibility;

    @Embedded
    private AuditingJpaInfo auditingInfo;

    public void syncPortfolioContent(PortfolioContentJpaEntity newContent) {
        this.portfolioContent = newContent;
        if (newContent != null) {
            newContent.setPortfolio(this);
        }
    }

    public void changePortfolioContent(String contentJson, String contentHtml) {
        AssertionUtils.notNull(
                this.portfolioContent,
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.PORTFOLIO_CONTENT_NOT_ATTACHED,
                PortfolioDomainException::new
        );
        this.portfolioContent.changeContent(contentJson, contentHtml);
    }
}
