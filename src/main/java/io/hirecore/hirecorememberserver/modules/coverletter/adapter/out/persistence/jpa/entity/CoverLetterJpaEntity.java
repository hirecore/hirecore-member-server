package io.hirecore.hirecorememberserver.modules.coverletter.adapter.out.persistence.jpa.entity;

import io.hirecore.hirecorememberserver.modules.coverletter.domain.vo.CoverLetterStatus;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AbstractPersistableAggregateRoot;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AuditingJpaInfo;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.CollaborationType;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.ExternalLink;
import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cover_letters")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class CoverLetterJpaEntity extends AbstractPersistableAggregateRoot<Long> {

    // primitives
    @Id
    private Long id;

    @Comment("회원 계정 ID")
    @Column(name = "member_account_id", nullable = false)
    private Long memberAccountId;

    @Comment("썸네일 이미지 ID")
    @Column(name = "thumbnail_image_id")
    private Long thumbnailImageId;

    @Comment("자기소개서 제목")
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
            mappedBy = "coverLetter",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private CoverLetterJobCategoryJpaEntity coverLetterJobCategory;

    @OneToOne(
            mappedBy = "coverLetter",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private CoverLetterContentJpaEntity coverLetterContent;

    @Comment("사용자 입력 외부 링크")
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "external_link_json", columnDefinition = "json")
    @Builder.Default
    private List<ExternalLink> externalLinks = new ArrayList<>();

    @OneToMany(
            mappedBy = "coverLetter",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<CoverLetterTagJpaEntity> coverLetterTags = new ArrayList<>();

    @Comment("연결된 포트폴리오 ID 목록")
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "portfolio_ids_json", columnDefinition = "json")
    @Builder.Default
    private List<Long> portfolioIds = new ArrayList<>();

    // vo
    @Comment("자기소개서 상태")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(30)")
    private CoverLetterStatus status;

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

    // 양방향 연관관계 동기화 (cascade/orphanRemoval 정상 동작용)
    public void syncCoverLetterContent(CoverLetterContentJpaEntity newContent) {
        this.coverLetterContent = newContent;
        if (newContent != null) {
            newContent.setCoverLetter(this);
        }
    }

    public void syncCoverLetterJobCategory(CoverLetterJobCategoryJpaEntity newJobCategory) {
        this.coverLetterJobCategory = newJobCategory;
        if (newJobCategory != null) {
            newJobCategory.setCoverLetter(this);
        }
    }

    public void addCoverLetterTag(CoverLetterTagJpaEntity tag) {
        this.coverLetterTags.add(tag);
        tag.attachCoverLetter(this);
    }
}
