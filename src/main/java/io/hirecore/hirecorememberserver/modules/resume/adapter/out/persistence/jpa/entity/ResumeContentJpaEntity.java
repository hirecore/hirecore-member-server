package io.hirecore.hirecorememberserver.modules.resume.adapter.out.persistence.jpa.entity;

import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AbstractPersistableEntity;
import io.hirecore.hirecorememberserver.sharedkernel.adapter.out.persistence.jpa.AuditingJpaInfo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resume_contents")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class ResumeContentJpaEntity extends AbstractPersistableEntity<Long> {
    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false, unique = true)
    private ResumeJpaEntity resume;

    @Comment("이력서 본문 JSON 콘텐츠")
    @Column(name = "content_json", nullable = false, columnDefinition = "TEXT")
    private String contentJson;

    @Comment("이력서 본문 HTML 콘텐츠")
    @Column(name = "content_html", nullable = false, columnDefinition = "TEXT")
    private String contentHtml;

    @Comment("본문에서 사용 중인 이미지 식별자 집합")
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "image_ids_json", nullable = false, columnDefinition = "json")
    @Builder.Default
    private List<Long> imageIds = new ArrayList<>();

    @Embedded
    private AuditingJpaInfo auditingInfo;

    void setResume(ResumeJpaEntity resume) {
        this.resume = resume;
    }
}
