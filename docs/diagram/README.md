# 다이어그램 문서 (Docs-as-Code)

다이어그램을 코드처럼 repo에서 관리합니다. **Mermaid**로 작성해 `.md`에 넣으면 GitHub·IDE가 그대로 렌더하므로, 리뷰어는 파일만 열면 그림을 봅니다(다운로드·플러그인 불필요).

다이어그램 개념은 [DIAGRAM_CONCEPT.md](DIAGRAM_CONCEPT.md), Mermaid 문법은 [MERMAID_CONCEPT.md](MERMAID_CONCEPT.md) 참고.

## 문서 목록

| 문서 | 종류 | 내용 |
|---|---|---|
| [portfolio/register-flow.md](portfolio/register-flow.md) | 시퀀스 | 포트폴리오 등록: 계약 플로우 + 내부 처리(실패 분기 포함) |
| [portfolio/hexagonal.md](portfolio/hexagonal.md) | 컴포넌트 | 포트폴리오 헥사고날 계층 |
| [file/image-lifecycle.md](file/image-lifecycle.md) | 상태 | 이미지 업로드 상태 전이 |

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
