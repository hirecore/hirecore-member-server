# resume 도메인 모델

이력서 작업물을 관리한다. 구조는 `portfolio` 와 유사하되 **조회/관심 추적과 도메인 이벤트가 없다**.

## 구성 (클래스 다이어그램)

```mermaid
classDiagram
    class Resume {
        <<Aggregate Root>>
    }
    Resume "1" *-- "1" ResumeContent
    Resume "1" *-- "1" ResumeJobCategory
    Resume "1" *-- "0..*" ResumeTag
```

> 공통 `AuditingInfo` 는 생략한다.

## 애그리거트 · 불변식

| 요소 | 역할 | 비고 |
|---|---|---|
| `Resume` | 루트 | `create()` 로 생성. `portfolioIds`(List)로 포트폴리오 연계. 이벤트 미발행 |
| `ResumeContent` | 본문 | `@MapsId` 로 부모 ID 공유 |
| `ResumeTag` / `ResumeJobCategory` | 태그 / 직무 | portfolio 와 동일 개념 |

## 값 객체 · 상태

| VO | 값 |
|---|---|
| `ResumeStatus` | `TEMP_SAVE` · `PUBLISHED` |
| 공유 VO | `Visibility` · `CollaborationType` · `ExternalLink` · `AuditingInfo` |

> 본문은 다른 BC(portfolio)가 공용 포트로 가져간다 — 계약은 sharedkernel `LoadResumeContentSharedPort.Result`.
