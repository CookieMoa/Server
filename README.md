# SecurityWithRedis

## 프로젝트 설명

> **SecurityWithRedis**는 Spring Boot 기반의 API 서버 템플릿으로, **Spring Security**와 **Redis**를 활용하여 **JWT 인증** 시스템에서 **refresh token**을 **Redis에 저장**하고 관리하는 방법을 제공합니다. 기본적으로 **MySQL**을 사용하여 사용자 정보를 저장하며, **refresh token**만 Redis에 저장하여 관리합니다. 또한, 본 템플릿은 **다중 토큰 처리**, **Swagger 설정**, **API 응답 통일**, **Exception 처리**, **Validation**, **JPA** 등을 기본으로 제공합니다.

## 주요 기능
- **다중 토큰 처리**: JWT를 이용한 다중 인증 처리
- **Swagger**: API 문서화를 위한 Swagger 설정
- **API 응답 통일**: 모든 API 응답의 구조를 통일하여 효율적인 API 관리
- **Exception 처리**: 공통 예외 처리 및 사용자 정의 예외 처리
- **Validation**: 입력 값 검증을 위한 다양한 검증 기능
- **JPA**: MySQL 데이터베이스 연동을 위한 JPA 설정
- **Redis 기반 refresh token 관리**: refresh token을 Redis에 저장하고 관리하여 효율적인 인증 시스템을 구현

## 데이터베이스 구조

이 프로젝트는 **MySQL** 데이터베이스를 사용하며, 사용자 정보는 MySQL에 저장됩니다. **Refresh Token**은 **Redis**에 저장됩니다.

### 1. UserEntity

`UserEntity`는 시스템 사용자 정보를 저장하는 엔티티로, 주로 회원가입 및 사용자 인증에 사용됩니다. 이 엔티티는 다음과 같은 필드를 가집니다:

- **id**: 사용자 고유 ID (자동 생성, 기본 키)
- **username**: 사용자의 이메일 주소를 저장합니다.
- **password**: 사용자의 비밀번호
- **role**: 사용자의 권한 (기본값: `ROLE_USER`)
- **accountStatus**: 사용자의 계정 상태 (기본값: `ACTIVE`)
- **nickname**: 사용자의 닉네임
- **gender**: 사용자의 성별

`UserEntity`는 **BaseEntity**를 상속받아 공통 필드(예: 생성일자, 수정일자 등)를 사용하며, **JPA**를 통해 자동으로 테이블과 매핑됩니다.

### 2. Refresh Token (Redis)

**refresh token**은 Redis에 저장되어, 사용자가 로그인할 때 발급된 refresh token을 관리하고, 토큰 만료 시 새로 발급받을 수 있게 해줍니다.

### Redis 연동

- **로그인 시**: 사용자가 로그인하면 **refresh token**이 생성되고, 이를 Redis에 저장합니다.
- **로그아웃 시**: 사용자가 로그아웃 요청을 보낼 때, **refresh token**을 Redis에서 삭제합니다.
- **토큰 재발행 시**: 사용자가 refresh token을 제공하면, Redis에서 해당 token을 검증하고, 유효하면 새로운 **access token**을 발급하며 **refresh token**은 새로 Redis에 저장합니다.
- **만료된 refresh token**: 일정 시간이 지난 후, Redis에서 자동으로 refresh token이 삭제됩니다.

## API 설명

### 1. 유저 API

- **유저 API**는 유저와 관련된 기능을 제공합니다.

#### 회원가입 API

- **경로**: `/auth/join`
- **메서드**: `POST`
- **설명**: 사용자 회원가입을 처리하는 API입니다. 클라이언트에서 전달받은 `username`, `password`, `nickname`, `gender` 값을 기반으로 새 사용자를 등록합니다.
- **요청 본문**: `username`, `password`, `nickname`, `gender` 필드를 포함한 JSON 형식의 데이터
- **응답**: 성공적인 회원가입 후, 생성된 사용자의 정보를 포함한 응답을 반환합니다.

---

### 2. 로그인 API

- **로그인 API**는 사용자의 로그인 처리를 담당하며, 로그인 성공 시 **JWT (JSON Web Token)** 을 발급하여 클라이언트에게 반환합니다.
- 로그인 요청은 `/auth/login` 경로로 전달되며, 사용자의 `username`과 `password`를 받아 인증을 처리합니다.

#### 로그인 필터

- **경로**: `/auth/login`
- **메서드**: `POST`
- **설명**: 사용자가 로그인 시 제공한 `username`과 `password`를 인증하는 API입니다. 인증에 성공하면 **access token**과 **refresh token**을 발급하여 응답합니다.
- **요청 본문**: `username`과 `password`를 포함한 로그인 정보
- **응답**: 인증 성공 시 `access token`을 헤더로, `refresh token`을 쿠키에 담아 응답합니다.

이 API는 **Spring Security**의 `UsernamePasswordAuthenticationFilter`를 확장한 `LoginFilter`에서 처리됩니다. 인증이 성공하면 JWT 토큰을 생성하여 응답합니다. 만약 인증 실패 시, 401 상태 코드와 함께 실패 응답을 반환합니다.

### 3. 로그아웃 API

- **경로**: `/logout`
- **메서드**: `POST`
- **설명**: 이 API는 사용자가 로그아웃 요청을 보낼 때, 클라이언트에서 전달된 **refresh token**을 검증하고, 만약 유효한 토큰이라면 해당 토큰을 **DB에서 삭제**하고, 클라이언트의 쿠키에서도 삭제하여 로그아웃을 처리합니다.
- **요청 본문**: `refresh token`이 포함된 **쿠키**를 전송해야 합니다.
- **응답**: 
  - 로그아웃이 정상적으로 처리된 경우 `HTTP 200 OK` 상태 코드가 반환됩니다.
  - 오류가 발생한 경우 적절한 상태 코드(`400 Bad Request`)와 함께 오류 응답이 반환됩니다.
---

### 3. 토큰 재발행 API

- **토큰 재발행 API**는 **refresh token**을 사용하여 **access token**을 재발급하는 기능을 제공합니다. 사용자가 refresh token을 제공하면, 새로운 access token을 발급해줍니다.

#### 토큰 재발행 API

- **경로**: `/reissue`
- **메서드**: `POST`
- **설명**: 클라이언트가 보유한 **refresh token**을 통해 새로운 **access token**을 발급하는 API입니다. 이를 통해 유효기간이 만료된 access token을 갱신할 수 있습니다.
- **요청 본문**: `refresh token`이 담긴 쿠키를 포함한 요청
- **응답**: 새로운 **access token**을 포함한 응답

이 API는 `ReissueService`를 통해 토큰 재발행 로직을 처리합니다. 클라이언트가 보유한 refresh token을 사용하여 새로운 access token을 발급해줍니다.

---

## 사용 방법

### 1. 새로운 레포지토리 생성
1. **새로운 레포지토리**를 생성합니다.
   - **주의**: **ReadMe** 파일을 생성하지 말고 빈 레포지토리로 생성합니다. (후속 작업을 위해)

### 2. `start-new-project.sh` 실행
1. 현재 레포지토리에서 `start-new-project.sh` 파일을 **다운로드**합니다.
2. 다운로드한 파일을 **로컬의 원하는 폴더**에 넣습니다.
3. 그 폴더의 경로에서 **Git Bash**를 실행합니다.
4. 아래 명령어를 실행하여 새 프로젝트를 시작합니다.

   ```bash
   ./start-new-project.sh TestProject https://github.com/your-username/testproject.git
   ```
- `TestProject` 자리에는 원하는 **프로젝트 이름**을 넣습니다.
- `https://github.com/your-username/testproject.git` 자리에는 **본인의 레포지토리 URL**을 넣습니다.

### 3. 프로젝트 푸시 후 IntelliJ에서 열기
1. 위 과정이 성공적으로 실행되면, 본인의 레포지토리에 해당 프로젝트가 **푸시**된 상태입니다.
2. 본인의 프로젝트를 **IntelliJ**에서 엽니다.

### 4. IntelliJ 설정
1. **`settings.gradle`** 파일에서 `rootProject.name`의 값을 **프로젝트 이름**으로 변경합니다.
2. 변경 후, IntelliJ의 **코끼리** 아이콘을 눌러서 프로젝트를 **리프레시**합니다.
3. **`main/java/com/example/securitywithredis`** 디렉토리에서 우클릭 후, **Refactor → Rename**을 선택하여 폴더 이름을 프로젝트 이름에 맞게 변경합니다.
4. **`resources`** 폴더 내의 파일에서도 **프로젝트 이름**에 맞게 변경해주면 좋습니다.


## 📌관련 이슈
- [refreshToken redis에 저장하기](https://github.com/connorcoco/SecurityWithRedis/issues/1)


## 📌연관 레포지토리

### [Spring Security + Mysql를 이용한 Refresh Token 관리](https://github.com/connorcoco/SecurityServer)
  기존의 SecurityServer 리포지토리는 MySQL을 사용하여 사용자 정보를 저장하고, refresh token도 MySQL에 저장하는 방식이었습니다. 이 프로젝트는 **SecurityWithRedis**에서 refresh token을 Redis에 저장하는 방법으로 개선되었습니다.

