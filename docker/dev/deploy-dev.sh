#!/usr/bin/env bash
# [목적]
# : 안전옵션 처리하여 쉘 스크립트 실행도중 문제 발생시 실행중단을 위함
set -Eeuo pipefail

# [목적]
# :스크립트가 있는 경로로 이동하여, 다른 작업 디렉토리에서 실행시 상대 경로 깨짐 방지 위함
# [개념]
#   - $(..) == 명령어 실행 // ${..} == 변수의 값을 꺼내는 행위
#   - ${BASH_SOURCE[0]} == BASH_SOURCE 라는 Bash 내부 변수(배열)의 0번째 값
# [결과]
# :deploy-dev.sh가 위치한 디렉토리로 이동하고, 그 경로(pwd)를 DEPLOY_DIR 변수에 저장한다.
DEPLOY_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${DEPLOY_DIR}"

# [목적]
# :배포에 필요한 환경파일 3개가 현재 디렉토리에 존재하는지 확인하고, 하나라도 없으면 에러 메시지를 출력한 뒤 스크립트를 즉시 종료하는 코드
# [개념]
#   - [[..]] == 조건식 평가 문법
#   - -f == 파일 테스트 연산자로, 일반 파일이 존재한다면 True 그렇지 않다면 False(파일이 없을 경우, 디렉토리인 경우)
if [[ ! -f ".env.dev.app" ]]; then
  echo "[ERROR] .env.dev.app file is missing in ${DEPLOY_DIR}"
  exit 1
fi

if [[ ! -f ".env.dev.mysql" ]]; then
  echo "[ERROR] .env.dev.mysql file is missing in ${DEPLOY_DIR}"
  exit 1
fi

if [[ ! -f ".env.dev.deploy" ]]; then
  echo "[ERROR] .env.dev.deploy file is missing in ${DEPLOY_DIR}"
  exit 1
fi

# [목적]
# :env.dev.deploy파일의 변수 설정들을 스크립트 파일로 가져오는 것
source .env.dev.deploy

# [목적]
# :방어적 코드
# [수행 방식]
#   - AWS_REGION 이 없거나 비어 있으면 에러를 내고 스크립트를 중단
#   - APP_IMAGE 이 없거나 비어 있으면 에러를 내고 스크립트를 중단
: "${AWS_REGION:?AWS_REGION is required}"
: "${APP_IMAGE:?APP_IMAGE is required}"

# [목적]
# : 문자열을 잘라서 변수에 넣는다.
# [개념]
#   - %pattern,%%pattern == 뒤에서부터 pattern 과 가장 짧게 매칭되는 부분 제거
#   - #pattern,##pattern == 앞에서부터 pattern 과 가장 짧게 매칭되는 부분 제거
#   - * == 앞에 또는 뒤에 오는 값들을 의미
ECR_REGISTRY="${APP_IMAGE%%/*}"
REPOSITORY_WITHOUT_TAG="${APP_IMAGE%:*}"
CURRENT_TAG="${APP_IMAGE##*:}"

echo "[INFO] Deploy directory: ${DEPLOY_DIR}"
echo "[INFO] App image: ${APP_IMAGE}"
echo "[INFO] ECR registry: ${ECR_REGISTRY}"


# [목적]
# : ECR 로그인
# [개념]
#   - \ == 줄바꿈
#   - | == 두 명령이 파이프(|)로 연결하며, 왼쪽 명령의 출력 결과를 오른쪽 명령의 입력으로 넘긴다.
#   - docker login == Docker Hub의 전용 명령어가 아니며 어떤 컨테이너 레지스트리에 인증할지 지정해서 로그인하는 명령이다. 즉 대상이 꼭 Docker Hub일 필요가 없다. (프로젝트에서는 ECR사용)
# [상세 설명]
#   - aws ecr get-login-password --region "${AWS_REGION}"
#     :지정한 AWS 리전에 있는 ECR에 로그인할 때 필요한 비밀번호(token) 를 출력
#      즉, AWS CLI가 ECR registry 접근용 임시 인증값 비밀번호(token)를 만들어 표준 출력(stdout)으로 내보낸다.
#   - docker login --username AWS --password-stdin "${ECR_REGISTRY}"
#     : (1) 파이프(|)와 --password-stdin 덕분에 왼쪽의 표준 출력(stdout)을 표준 입력(stdin)으로 받을 수 있게 되어, 임시 인증 값 비밀번호를 받을 수 있게 된다.
#       (2) 신원 확인 절차는 aws ecr get-login-password 를 호출할 때 내부 네트워크 요청을 통해 AWS Credential을 넘겨준다.
# [요약]
# : 현재 서버가 가진 AWS 권한으로, 지정한 리전의 ECR에 접근 가능한 임시 인증 토큰을 발급받는다. 그 토큰을 Docker에게 전달해서, 지정한 ECR 레지스트리에 인증된 상태를 만든다.
aws ecr get-login-password --region "${AWS_REGION}" \
  | docker login --username AWS --password-stdin "${ECR_REGISTRY}"


# [목적]
# :Compose 설정 기준으로 app 서비스 이미지를 pull과 컨테이너를 재생성/재기동
# [상세 설명]
#   - docker compose --env-file ./.env.dev.deploy -f ./docker-compose.dev.yml pull app
#     : docker-compose.dev.yml 에서 app 서비스가 참조하는 image를 레지스트리(ECR)에서 가져온다.
#   - docker compose --env-file .env.dev.deploy -f ./docker-compose.dev.yml up -d --remove-orphans
#    :해당 설정대로 컨테이너들을 실제로 실행하거나 갱신하고, 더 이상 필요 없는 고아 컨테이너는 제거한다.
docker compose --env-file .env.dev.deploy -f docker-compose.dev.yml pull app
docker compose --env-file .env.dev.deploy -f docker-compose.dev.yml up -d --remove-orphans

cleanup_local_dev_images() {
  echo "[INFO] Cleaning local dev images. Keep latest 5 dev images."

  mapfile -t old_images < <(
    docker image ls "${REPOSITORY_WITHOUT_TAG}" --format '{{.Repository}}:{{.Tag}}' \
      | grep ':dev-' \
      | grep -v ":${CURRENT_TAG}$" \
      | while read -r image; do
          created="$(docker image inspect --format '{{.Created}}' "${image}" 2>/dev/null || true)"
          if [[ -n "${created}" ]]; then
            printf '%s %s\n' "${created}" "${image}"
          fi
        done \
      | sort -r \
      | awk 'NR>4 {print $2}'
  )

  if ((${#old_images[@]} > 0)); then
    docker image rm -f "${old_images[@]}" || true
  fi

  docker image prune -f || true
}

cleanup_local_dev_images

echo "[INFO] Current compose status:"
docker compose --env-file .env.dev.deploy -f docker-compose.dev.yml ps