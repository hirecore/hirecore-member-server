package io.hirecore.hirecorememberserver.modules.portfolio.adapter.out.persistence.jpa.entity;

import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AbstractPersistableAggregateRoot;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AuditingJpaInfo;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.vo.PortfolioStatus;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ExternalLink;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
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

    // primitives
    @Id
    private Long id;

    @Comment("회원 계정 ID")
    @Column(name = "member_account_id", nullable = false)
    private Long memberAccountId;

    @Comment("썸네일 이미지 ID")
    @Column(name = "thumbnail_image_id")
    private Long thumbnailImageId;

    @Comment("자기소개서 ID")
    @Column(name = "cover_letter_id")
    private Long coverLetterId;

    @Comment("이력서 ID")
    @Column(name = "resume_id")
    private Long resumeId;

    @Comment("포트폴리오 제목")
    @Column(name = "title", nullable = false, columnDefinition = "VARCHAR(200)")
    private String title;

    @Comment("미리보기 요약")
    @Column(name = "preview_summary", nullable = false, columnDefinition = "VARCHAR(100)")
    private String previewSummary;

    @Comment("나만보기 메모")
    @Column(name = "private_memo", columnDefinition = "TEXT")
    private String privateMemo;

    @Comment("캐싱된 조회수")
    @Column(name = "cached_view_count", nullable = false)
    private Long cachedViewCount;

    @Comment("캐싱된 관심수")
    @Column(name = "cached_interest_count", nullable = false)
    private Long cachedInterestCount;

    // sub-aggregate
    @OneToOne(
            mappedBy = "portfolio",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private PortfolioJobCategoryJpaEntity portfolioJobCategory;

    @OneToOne(
            mappedBy = "portfolio",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private PortfolioContentJpaEntity portfolioContent;

    @Comment("사용자 입력 외부 링크")
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "external_link_json", columnDefinition = "json")
    @Builder.Default
    private List<ExternalLink> externalLinks = new ArrayList<>();

    @OneToMany(
            mappedBy = "portfolio",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @OrderBy("sortOrder ASC")
    @BatchSize(size = 100)
    @Builder.Default
    private List<PortfolioTagJpaEntity> portfolioTags = new ArrayList<>();

    // vo
    @Comment("포트폴리오 상태")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(30)")
    private PortfolioStatus status;

    @Comment("협업 유형")
    @Enumerated(EnumType.STRING)
    @Column(name = "collaboration_type", nullable = false, columnDefinition = "VARCHAR(30)")
    private CollaborationType collaborationType;

    @Comment("공개 범위")
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, columnDefinition = "VARCHAR(30)")
    private Visibility visibility;

    @Embedded
    private AuditingJpaInfo auditingInfo;

    // 양방향 @OneToOne 양쪽 참조 동기화 (cascade/orphanRemoval 위해)
    public void syncPortfolioContent(PortfolioContentJpaEntity newContent) {
        this.portfolioContent = newContent;
        if (newContent != null) {
            newContent.setPortfolio(this);
        }
    }

    public void syncPortfolioJobCategory(PortfolioJobCategoryJpaEntity newJobCategory) {
        this.portfolioJobCategory = newJobCategory;
        if (newJobCategory != null) {
            newJobCategory.setPortfolio(this);
        }
    }

    public void addPortfolioTag(PortfolioTagJpaEntity tag) {
        this.portfolioTags.add(tag);
        tag.attachPortfolio(this);
    }
}
