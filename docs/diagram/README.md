# 다이어그램 문서 (Docs-as-Code)

다이어그램을 코드처럼 repo에서 관리합니다. **Mermaid**로 작성해 `.md`에 넣으면 GitHub·IDE가 그대로 렌더하므로, 리뷰어는 파일만 열면 그림을 봅니다(다운로드·플러그인 불필요).

## 문서 목록

| 문서 | 종류 | 내용 |
|---|---|---|
| [portfolio/register-flow.md](portfolio/register-flow.md) | 시퀀스 | 포트폴리오 등록: 계약 플로우 + 내부 처리(실패 분기 포함) |
| [portfolio/hexagonal.md](portfolio/hexagonal.md) | 컴포넌트 | 포트폴리오 헥사고날 계층 |
| [file/image-lifecycle.md](file/image-lifecycle.md) | 상태 | 이미지 업로드 상태 전이 |

## 구조 & 네이밍

- **도메인/기능별** 폴더로 묶는다(`portfolio/`, `file/` …). 다이어그램 유형이 아니라 주제로 나눈다.
- 파일명은 kebab-case(`register-flow.md`).
- `docs/diagram/` 만 git 추적된다. 그 밖의 `docs/` 는 로컬 스크래치(`.gitignore`).

## 규칙

- 코드를 바꾸면 관련 다이어그램도 **같은 PR에서** 고친다.
- 자동 생성 가능한 것(API 계약 → OpenAPI, DB → ERD)은 그리지 않는다.
- 고수준 그림은 **일부러 추상적으로** 유지한다(클래스명·필드를 박을수록 빨리 썩는다).

## 참고 · 시퀀스 프래그먼트

시퀀스 다이어그램에서 제어 구조는 **결합 프래그먼트(combined fragment)** 라는 박스로 묶어 표현한다.
박스 왼쪽 위 라벨이 종류를, `[조건]`(가드)이 실행 조건을 나타낸다.

| 라벨 | 뜻 | 의미 |
|---|---|---|
| `alt` | alternative | 여러 경우 중 하나 실행 (if / else) |
| `opt` | optional | 조건 참일 때만 실행 (else 없는 if) |
| `loop` | loop | 반복 실행 |
| `par` | parallel | 병렬 실행 |
| `break` | break | 조건 시 이후 흐름 중단 |
| `critical` | critical region | 중간에 끊기면 안 되는 필수 구간 (`option`으로 예외 처리) |

```
alt 용량 초과        %% if
    ... 400 응답
else 용량 충분       %% else
    ... 200 응답
end                 %% 분기 끝
```

> UML의 `ref`(다른 상호작용 다이어그램을 참조)는 Mermaid가 지원하지 않는다. 대신 해당 문서로의 링크로 대체한다.
