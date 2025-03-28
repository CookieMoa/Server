#!/bin/bash

# 설정
TEMPLATE_REPO="https://github.com/connorcoco/SecurityWithRedis.git"
OLD_PROJECT_NAME="SecurityWithRedis"
NEW_PROJECT_NAME=$1
REMOTE_REPO_URL=$2

# 사용법 안내
if [ -z "$NEW_PROJECT_NAME" ] || [ -z "$REMOTE_REPO_URL" ]; then
  echo "❌ 새 프로젝트 이름과 원격 레포지토리 URL을 입력하세요!"
  echo "사용법: ./start-new-project.sh <새 프로젝트 이름> <원격 레포지토리 URL>"
  exit 1
fi

# 새 프로젝트 디렉토리 생성
echo "✅ $NEW_PROJECT_NAME 프로젝트 생성 시작..."
git clone $TEMPLATE_REPO $NEW_PROJECT_NAME

# 디렉토리 이동
cd $NEW_PROJECT_NAME || exit

# 기존 Git 기록 제거
rm -rf .git

# 파일 내용 치환
echo "🔄 $OLD_PROJECT_NAME 이름을 $NEW_PROJECT_NAME 로 변경 중..."
find . -type f -exec sed -i '' "s/$OLD_PROJECT_NAME/$NEW_PROJECT_NAME/g" {} +

# 패키지 이름 치환
OLD_PACKAGE_PATH="src/main/java/com/example/$(echo $OLD_PROJECT_NAME | tr '[:upper:]' '[:lower:]')"
NEW_PACKAGE_PATH="src/main/java/com/example/$(echo $NEW_PROJECT_NAME | tr '[:upper:]' '[:lower:]')"

echo "OLD_PACKAGE_PATH: $OLD_PACKAGE_PATH"
echo "NEW_PACKAGE_PATH: $NEW_PACKAGE_PATH"

mv "$OLD_PACKAGE_PATH" "$NEW_PACKAGE_PATH"

OLD_PACKAGE_PATH="src/test/java/com/example/$(echo $OLD_PROJECT_NAME | tr '[:upper:]' '[:lower:]')"
NEW_PACKAGE_PATH="src/test/java/com/example/$(echo $NEW_PROJECT_NAME | tr '[:upper:]' '[:lower:]')"

echo "OLD_PACKAGE_PATH (test): $OLD_PACKAGE_PATH"
echo "NEW_PACKAGE_PATH (test): $NEW_PACKAGE_PATH"

# Git 초기화 및 원격 레포지토리 설정
git init
git remote add origin "$REMOTE_REPO_URL"
git add .
git commit -m "Initial commit for $NEW_PROJECT_NAME"
git branch -M main
git push -u origin main

echo "✅ $NEW_PROJECT_NAME 프로젝트 생성 및 원격 레포지토리 연결 완료!"
echo "📂 프로젝트 경로: $(pwd)"
