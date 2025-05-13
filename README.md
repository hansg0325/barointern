# 🔐 JWT 인증/인가 기반 Spring Boot API 서비스

## 📌 프로젝트 개요

Spring Boot 기반의 REST API 서버로, JWT를 이용한 사용자 인증 및 인가 기능을 제공합니다.  
역할(Role) 기반의 접근 제어와 Swagger를 통한 API 문서화, Junit 테스트 작성, AWS EC2 배포까지 포함된 실전형 프로젝트입니다.

---

## 🛠 사용 기술

- **Spring Boot 3.4.5**
- **Java 17**
- **Spring Security**
- **JWT (jjwt 0.12.6)**
- **Spring Web**
- **Spring Validation**
- **Junit5**
- **Swagger / Springdoc OpenAPI**
- **Gradle**
- **AWS EC2 (Ubuntu 22.04)**

---

## 📁 프로젝트 구조
```
src
├── main
│ ├── java/com/example/jwtapp
│ │ ├── config # Security & Swagger 설정
│ │ ├── controller # API 컨트롤러
│ │ ├── dto # 요청/응답 DTO
│ │ ├── exception # 예외 처리
│ │ ├── model # 엔티티 및 Role 정의
│ │ ├── repository # 사용자 저장소 (메모리 기반)
│ │ ├── security # JWT 유틸, 필터, 인증 관련 클래스
│ │ └── service # 비즈니스 로직
│ └── resources
│ └── application.yml # 설정 파일
├── test
└ └── java/com/example/jwtapp # 단위 테스트
```

---

## ⚙️ 실행 방법

### 1. 로컬 실행

```bash
git clone https://github.com/hansg0325/barotintern.git
cd barointern
./gradlew clean build
java -jar build/libs/barointern.jar
```
접속 주소: http://localhost:8080

### 2. AWS EC2 배포

접속 주소: http://15.165.17.12:8080

---

## 🧪 주요 기능
### ✅ 회원가입 (일반 사용자 / 관리자)
- POST /signup

```
Request:
{
  "username": "JIN HO",
  "password": "12341234",
  "nickname": "Mentos"
}
```
```
Response:
{
  "username": "JIN HO",
  "nickname": "Mentos",
  "roles": [ { "role": "USER" } ]
}
```
### ✅ 로그인 및 JWT 발급
- POST /login

```
Request:
{
  "username": "JIN HO",
  "password": "12341234"
}
```
```
Response:
{
  "token": "JWT_ACCESS_TOKEN"
}
✅ 관리자 권한 부여 (관리자 전용)
PATCH /admin/users/{userId}/roles
```
```
Response:
{
  "username": "JIN HO",
  "nickname": "Mentos",
  "roles": [ { "role": "ADMIN" } ]
}
```
> 모든 보호된 요청에는 Authorization: Bearer {token} 헤더가 필요합니다.

---

## 🧪 테스트 방법
- JUnit 테스트 실행:

```
./gradlew test
```
- 테스트 대상:

  - 회원가입 성공/실패

  - 로그인 성공/실패

  - 토큰 검증

  - 관리자 권한 부여 (성공/실패/비인가 접근)

---

## 📚 API 명세 (Swagger UI)
- Swagger URL: [http://15.165.17.12:8080/swagger-ui/index.html](http://15.165.17.12:8080/swagger-ui/index.html)

- 문서 경로: /v3/api-docs

---

## 🚀 배포 정보
- EC2 주소: [http://15.165.17.12:8080](http://15.165.17.12:8080)

- Swagger UI 주소: [http://15.165.17.12:8080/swagger-ui/index.html](http://15.165.17.12:8080/swagger-ui/index.html)

---

## 🔒 보안 및 인증
### JWT 구조
- Header: alg, typ

- Payload:

  - sub: 사용자 ID

  - roles: USER / ADMIN

  - iat: 발급 시간

  - exp: 만료 시간 (2시간)

  - Signature: HS256 알고리즘 기반 서명

### 인증 방식
- 모든 보호된 API는 Bearer Token 방식으로 인증

- 인증 실패 또는 권한 부족 시, 명확한 JSON 에러 메시지 반환

---

## 🧾 예외 처리 포맷
```
{
  "error": {
    "code": "INVALID_TOKEN",
    "message": "유효하지 않은 인증 토큰입니다."
  }
}
```

---

## 📎 GitHub Repository
- 🔗 https://github.com/hansg0325/barointern
