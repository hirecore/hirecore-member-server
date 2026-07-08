# 다이어그램 문서 (Docs-as-Code)

다이어그램을 코드처럼 repo에서 관리합니다. **Mermaid**로 작성해 `.md`에 넣으면 GitHub·IDE가 그대로 렌더하므로, 리뷰어는 파일만 열면 그림을 봅니다(다운로드·플러그인 불필요).

다이어그램 개념은 [DIAGRAM_CONCEPT.md](DIAGRAM_CONCEPT.md), Mermaid 문법은 [MERMAID_CONCEPT.md](MERMAID_CONCEPT.md) 참고.
프로젝트 전체 아키텍처는 [ARCHITECTURE.md](ARCHITECTURE.md) 참고.

## 문서 목록

| 문서 | 종류 | 내용 |
|---|---|---|
| [account/auth-flow.md](account/auth-flow.md) | 시퀀스·구조 | 소셜 로그인·인증: OAuth2 로그인 + JWT 쿠키 인증(횡단) + 토큰 폐기 원리(tokenVersion 세대 비교) + 로그아웃 + 프로필·소셜계정 자동 생성 이벤트 |
| [account/token-issue-and-verify-flow.md](account/token-issue-and-verify-flow.md) | 플로우차트 | JWT 토큰 발급·검증 내부 처리: 클레임·서명(HMAC 대칭키) + Base64 포장/서명 2계층 + 검증 2관문(서명·버전) + 예외 처리 |
| [portfolio/register-flow.md](portfolio/register-flow.md) | 시퀀스·구조 | 포트폴리오 등록: 계약 플로우 + 내부 처리(실패 분기) + 헥사고날 구조 |
| [portfolio/view-and-interest-flow.md](portfolio/view-and-interest-flow.md) | 시퀀스 | 포트폴리오 조회·관심·조회수: 조회수(AFTER_COMMIT·REQUIRES_NEW) + 관심 등록/해제(동기) |
| [portfolio/edit-and-update-flow.md](portfolio/edit-and-update-flow.md) | 시퀀스·구조 | 포트폴리오 편집 조회·수정: 소유 검증 + modify(태그·이미지 교체) + 이미지 언링크 이벤트(file BC·BEFORE_COMMIT) |
| [portfolio/delete-flow.md](portfolio/delete-flow.md) | 시퀀스·구조 | 포트폴리오 삭제: 하드 딜리트(태그 cascade·조회/관심 정리) + 이미지 회수 이벤트(file BC) |
| [portfolio/summaries-flow.md](portfolio/summaries-flow.md) | 시퀀스·구조 | 포트폴리오 목록: 내 목록 + 공개 목록(isInterested 배치 IN 쿼리로 N+1 회피·커서 페이징) |
| [file/image-lifecycle.md](file/image-lifecycle.md) | 상태·시퀀스 | file 도메인 흐름: 생명주기(PENDING→UPLOADED→ORPHANED→DELETED) + 확정·고아화·정리 |
| [storage/storage-usage.md](storage/storage-usage.md) | 시퀀스 | 스토리지 용량: 할당량(membership)·사용량(storage)·발급 전 용량 검증·이미지 이벤트 연동 |
| [category/job-category-hierarchy.md](category/job-category-hierarchy.md) | 시퀀스·구조 | 직무 카테고리 계층: 트리 조회(공개 API) + 계층 경로 조회(공용 포트·재귀 CTE) |
| [profile/profile-query-flow.md](profile/profile-query-flow.md) | 시퀀스·구조 | 프로필 요약(본인)·닉네임 조회: 닉네임은 웹 엔드포인트 없이 공용 포트로 portfolio가 소비 |
| [resume/content-and-titles-flow.md](resume/content-and-titles-flow.md) | 시퀀스·구조 | 이력서 본문·제목 조회: 웹 엔드포인트 없음, portfolio가 공용 포트로 소비(본문 마스킹·제목 IN 배치) |
| [coverletter/content-and-titles-flow.md](coverletter/content-and-titles-flow.md) | 시퀀스·구조 | 자기소개서 본문·제목 조회: resume와 동일 구조, 공용 포트로 portfolio가 소비 |
| [membership/entitlement-quota-flow.md](membership/entitlement-quota-flow.md) | 시퀀스·구조 | 멤버십 할당량 조회: 웹 엔드포인트 없음, storage가 공용 포트로 소비(스냅샷 할당량) |

## 도메인 모델

각 BC의 **애그리거트·자식 엔티티·값 객체·불변식·도메인 이벤트**를 도메인별로 분리해 정리한다. (흐름 문서는 "무엇이 순서대로 일어나나", 도메인 모델은 "무엇으로 이루어져 있나"를 답한다.)

| BC | 문서 | BC | 문서 |
|---|---|---|---|
| account | [account/domain-model.md](account/domain-model.md) | portfolio | [portfolio/domain-model.md](portfolio/domain-model.md) |
| profile | [profile/domain-model.md](profile/domain-model.md) | resume | [resume/domain-model.md](resume/domain-model.md) |
| category | [category/domain-model.md](category/domain-model.md) | coverletter | [coverletter/domain-model.md](coverletter/domain-model.md) |
| file | [file/domain-model.md](file/domain-model.md) | storage | [storage/domain-model.md](storage/domain-model.md) |
| membership | [membership/domain-model.md](membership/domain-model.md) | policy | [policy/domain-model.md](policy/domain-model.md) |

## 구조 & 네이밍

- **도메인/기능별** 폴더로 묶는다(`portfolio/`, `file/` …). 다이어그램 유형이 아니라 주제로 나눈다.
- 파일명은 부류에 따라 나눈다.
  - **콘텐츠 다이어그램** → 소문자 kebab-case (`register-flow.md`, `image-lifecycle.md`)
  - **레퍼런스/메타 문서** → 대문자 SNAKE_CASE (`README.md`, `DIAGRAM_CONCEPT.md`, `MERMAID_CONCEPT.md`)
- `docs/diagram/` 만 git 추적된다. 그 밖의 `docs/` 는 로컬 스크래치(`.gitignore`).

## 규칙

- 코드를 바꾸면 관련 다이어그램도 **같은 PR에서** 고친다.
- 자동 생성 가능한 것(API 계약 → OpenAPI, DB → ERD)은 그리지 않는다.
- 고수준 그림은 **일부러 추상적으로** 유지한다(클래스명·필드를 박을수록 빨리 썩는다).
