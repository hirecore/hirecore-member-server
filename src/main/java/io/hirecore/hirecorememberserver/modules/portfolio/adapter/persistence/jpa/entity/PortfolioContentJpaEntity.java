package io.hirecore.hirecorememberserver.modules.portfolio.adapter.persistence.jpa.entity;

import io.hirecore.hirecorememberserver.common.adapter.out.persistence.jpa.AbstractPersistableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "portfolio_contents")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class PortfolioContentJpaEntity extends AbstractPersistableEntity<Long> {
    @Id
    private Long id;

    @MapsId
    @Comment("포트폴리오 ID")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false, unique = true)
    private PortfolioJpaEntity portfolio;

    @Comment("포트폴리오 본문 JSON 콘텐츠")
    @Column(name = "content_json", nullable = false, columnDefinition = "TEXT")
    private String contentJson;

    @Comment("포트폴리오 본문 HTML 콘텐츠")
    @Column(name = "content_html", nullable = false, columnDefinition = "TEXT")
    private String contentHtml;

    void setPortfolio(PortfolioJpaEntity portfolio) {
        this.portfolio = portfolio;
    }

    public void changeContent(String contentJson, String contentHtml) {
        this.contentJson = contentJson;
        this.contentHtml = contentHtml;
    }
}
