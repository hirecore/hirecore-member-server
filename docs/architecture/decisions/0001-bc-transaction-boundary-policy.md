# ADR-0001: BC 간 트랜잭션 경계 정책 (ACID vs Eventual Consistency)

## 상태

Proposed (#172)

## 컨텍스트

본 서비스는 단일 데이터베이스를 공유하는 **모듈러 모놀리스(modular monolith)** 구조이며, `portfolio`·`file`·`storage`·`category`·`account` 등 여러 Bounded Context(이하 BC)가 같은 프로세스 안에서 협력합니다. BC 간 협력은 sharedkernel의 out 포트를 통한 의존성 역전 패턴으로 구현되어 있습니다.

현재 구조에서는 한 BC의 UseCase가 다른 BC의 UseCase(어댑터를 경유)를 호출할 때, Spring `@Transactional`의 기본 전파(REQUIRED)에 의해 **외곽 트랜잭션이 내부까지 흡수**됩니다. 즉 portfolio 등록 한 번에 다음이 단일 ACID 트랜잭션으로 묶입니다.

```
PortfolioController
  └─ CreatePortfolioUseCase  [@Transactional]
      ├─ markUploaded            (file BC)
      ├─ saveUserStorageUsage    (storage BC, 현재 stub)
      ├─ findIdByCode            (category BC, 읽기)
      └─ savePortfolio           (portfolio BC)
```

이 구조는 다음 세 측면에서 의사결정이 필요합니다.

1. **일관성 모델**: 강한 일관성(ACID) 유지 vs 결과적 일관성(Eventual Consistency)
2. **실패 격리**: 한 BC의 실패가 전체 롤백할지, 부분 보상할지
3. **확장 경로**: 향후 BC를 별도 서비스로 분리할 가능성에 대한 준비

## 검토한 대안

### A. 순수 ACID (현재)

모든 BC 간 호출이 단일 트랜잭션으로 묶입니다.

**장점**
- 추론·디버깅이 단순. 부분 실패 상태가 존재하지 않음
- 추가 인프라(이벤트 브로커, outbox 등) 불필요
- 사용자 응답 시점에 모든 부수효과가 확정되어 있음

**단점**
- BC 간 결합도가 트랜잭션 차원에서 강해짐 → 한 BC의 변경이 다른 BC의 트랜잭션 행위에 영향
- 한 BC의 느린 쿼리·락이 전체를 블록
- 수평 확장·서비스 분리 시 분산 트랜잭션이 필요 → 마이그레이션 비용 큼
- 성격이 다른 부수효과(예: 사용량 통계 갱신)도 핵심 흐름과 같은 트랜잭션에 강제 편입

### B. 순수 Eventual Consistency

모든 BC 간 효과를 도메인 이벤트로 전파. 핵심 BC만 동기 트랜잭션, 나머지는 비동기 핸들러로 처리.

**장점**
- BC 자율성 극대화
- 실패 격리 가능 (한 BC가 다운되어도 나머지 동작)
- 향후 서비스 분리 시 자연스러운 경로

**단점**
- 인프라 복잡도 급증: 트랜잭셔널 outbox, 이벤트 발행/구독, idempotent 핸들러, dead-letter, 보상 트랜잭션
- 사용자 응답 시점에는 부수효과가 미반영 → UI에서 "처리 중"·"잠시 후 갱신" 같은 의미 처리 필요
- 동기적 검증(예: 이미지 소유권)은 여전히 동기 호출 필요 → 부분적으로 ACID와 혼재 불가피
- 모놀리스 단계에서 도입 비용 대비 이득이 작음

### C. 하이브리드: 핵심 ACID + 비핵심 도메인 이벤트 (권장)

핵심 일관성이 필요한 경로는 ACID로 유지하고, 결과적 일관성이 허용되는 부수효과는 도메인 이벤트로 분리.

**장점**
- 인프라 부담을 낮은 수준으로 유지하면서 BC 자율성을 점진적으로 확보
- 현재 모놀리스 단계에 적합
- 향후 BC 추출 시 이벤트 경로가 그대로 외부 메시지로 진화 가능 (점진 마이그레이션)

**단점**
- "무엇을 동기로 두고 무엇을 이벤트로 분리할지"의 분류 기준이 일관되어야 함 → 가이드 없으면 임의적 분할 발생 위험
- 두 모델이 공존하므로 학습 곡선 증가

## 결정

본 시점(모놀리스 단계)에서는 **C. 하이브리드**를 채택한다.

### 분류 기준

각 BC 간 호출은 다음 기준으로 동기/비동기를 결정한다.

| 호출 성격 | 처리 방식 |
|---|---|
| 호출 결과가 핵심 응답에 반영되어야 함 | **ACID 동기** |
| 핵심 흐름의 검증·권한·데이터 정합성에 필수 | **ACID 동기** |
| 부수효과(통계, 알림, 로그, 인덱싱) | **도메인 이벤트 비동기** |
| 별도 BC가 자체 시점에 갱신해도 무방 | **도메인 이벤트 비동기** |

### 현재 호출 흐름의 분류 (portfolio 등록 기준)

| 호출 | 분류 | 근거 |
|---|---|---|
| `markUploaded` (file) | ACID 동기 | 이미지 소유권 검증 + 상태 전이가 portfolio 정합성에 필수 |
| `findIdByCode` (category) | ACID 동기 (읽기) | 등록 시 카테고리 식별자 해석이 선행 조건 |
| `savePortfolio` (portfolio) | ACID 동기 | aggregate 본 데이터 저장 |
| `saveUserStorageUsage` (storage) | **도메인 이벤트 비동기** (목표) | 사용량 갱신은 결과적 일관성 허용. 현재는 stub이며 ACID로 묶여 있으나 향후 이벤트 기반으로 전환 |
| `saveUserStorageUsageLog` (storage) | **도메인 이벤트 비동기** (목표) | 로그성 데이터, 즉시 반영 불요 |

### 도메인 이벤트 인프라 단계적 도입

본 결정만으로는 즉시 모든 호출을 이벤트화하지 않는다. 다음 단계로 진행한다.

1. **현재 (Phase 1)**: 모든 호출 ACID. 포트 시그니처와 BC 경계는 이미 정리됨
2. **단기 (Phase 2)**: 도메인 이벤트 발행 인프라 도입 — `AbstractDomainEventPublisher`/`AbstractPersistableAggregateRoot`의 기존 패턴 + Spring `@TransactionalEventListener(AFTER_COMMIT)` 활용. 트랜잭셔널 outbox는 모놀리스 단계에서는 보류
3. **중기 (Phase 3)**: 비핵심 부수효과를 이벤트 핸들러로 이전. `saveUserStorageUsage` 등이 첫 후보
4. **장기 (Phase 4)**: BC 추출 필요 시점에 이벤트 발행을 외부 브로커로 전환 (Kafka/SQS 등). 이때 outbox 도입

### 적용 가이드

- 새 BC 간 호출을 추가할 때 위 [분류 기준](#분류-기준) 표를 따른다
- 기본은 ACID 동기. 비동기로 가려는 경우 ADR 갱신 또는 별 의사결정 기록을 남긴다
- BC 간 호출 시 `@Transactional` 전파는 REQUIRED 유지 (현 구조 그대로)
- `@TransactionalEventListener(phase = AFTER_COMMIT)`로 commit 후 핸들링하여 이벤트 누락을 줄인다

## 결과 / 영향

### 즉시 영향 (Phase 1 유지)

- 코드 변경 없음. 현재 구조가 본 결정에 부합함
- `saveUserStorageUsage`/`saveUserStorageUsageLog`의 stub 상태는 의도적 보류이며, Phase 3에서 이벤트 기반으로 구현 예정

### Phase 2 이후 영향

- `ImageFileMeta` 등 도메인이 의미 있는 이벤트(예: `ImageUploadedEvent`)를 발행하기 시작 (#170과 직결)
- 이벤트 발행 경로 정비 시 영속화 경로 분리(#169)가 선행되어야 함

### 트레이드오프 수용

- 모놀리스 단계에서는 분류가 일부 모호한 경계가 있을 수 있음. 합의된 분류 기준으로 일관성을 유지
- 하이브리드 모델은 두 사고방식을 동시에 요구 → 신규 합류자에게 본 ADR로 컨텍스트를 안내

## 후속 작업 (별 이슈 분리)

본 ADR이 Accepted 되면 다음 이슈가 후속 진행된다.

- **#169** refactor(file): ImageFileMeta 영속화 경로의 도메인 mutation 분리
  → Phase 2 이벤트 인프라의 토대
- **#170** feat(file): ImageFileMeta 도메인 이벤트 도입 및 발행 경로 정비
  → 본 ADR의 Phase 2를 구체화
- **(신규)** feat(storage): UserStorageUsage 갱신을 도메인 이벤트 기반으로 전환
  → 본 ADR의 Phase 3 첫 후보. #170 머지 후 진행

## 참고

- `AbstractDomainEventPublisher` (sharedkernel/domain): 도메인 이벤트 누적·방출 기반
- `AbstractPersistableAggregateRoot` (sharedkernel/infrastructure): Spring Data `@DomainEvents` 발행 메커니즘 — `Repository.save()` 시점 자동 발행
- 관련 컨벤션: ArchUnit 규칙 — in 어댑터의 out 어댑터 직접 의존 차단 (#163)
