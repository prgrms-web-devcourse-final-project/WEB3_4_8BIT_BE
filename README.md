# WEB3_4_8BIT_BE

> 프로그래머스 최종 프로젝트로 진행한 낚시 서비스 백엔드입니다.

이 프로젝트는 낚시 포인트, 어종 정보, 선상 낚시 게시글, 출조 모집, 예약, 리뷰, 채팅, 좋아요, 회원 인증 등을 제공하는 낚시 플랫폼 백엔드입니다. Spring Boot 기반의 단일 백엔드 애플리케이션으로 구성되어 있으며, JPA/QueryDSL, Redis, MongoDB, WebSocket, OAuth2/JWT, S3, 공간 데이터 처리를 활용합니다.

---

## 1. 프로젝트 개요

낚시 활동은 포인트 정보, 어종 정보, 선박/출조 정보, 예약, 후기, 모집 커뮤니티가 분산되어 있어 사용자가 정보를 찾기 어렵습니다. 이 프로젝트는 낚시 관련 정보를 한 곳에서 탐색하고, 출조 모집과 선상 낚시 예약까지 연결할 수 있도록 구성한 백엔드 서비스입니다.

### 주요 목표

- 낚시 포인트와 어종 정보를 제공
- 선상 낚시 게시글과 출조 모집 게시글 관리
- 예약 가능 날짜와 예약 기능 제공
- 게시글 댓글, 좋아요, 리뷰 기능 제공
- 실시간 채팅 기능 제공
- OAuth2/JWT 기반 사용자 인증
- Redis 캐싱 및 MongoDB 기반 채팅 데이터 처리
- QueryDSL과 공간 데이터 기반 검색 확장

---

## 2. 기술 스택

| 구분 | 기술 |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.4.4 |
| Build | Gradle |
| Persistence | Spring Data JPA |
| Database | MySQL, H2 |
| Query | QueryDSL, QueryDSL Spatial |
| Cache | Redis |
| Document DB | MongoDB |
| Realtime | WebSocket |
| Security | Spring Security, OAuth2 Client, JWT |
| API Docs | SpringDoc OpenAPI / Swagger UI |
| Storage | AWS S3 SDK |
| Spatial | JTS Core, Hibernate Spatial |
| Test | JUnit, Spring Boot Test, Fixture Monkey |
| Logging | Logback |
| Etc | Lombok, Validation, Mail, AOP, Retry |

---

## 3. 프로젝트 구조

```text
WEB3_4_8BIT_BE
└── backend
    ├── Dockerfile
    ├── build.gradle
    ├── settings.gradle
    └── src
        ├── main
        │   ├── java/com/backend
        │   │   ├── BackendApplication.java
        │   │   ├── domain
        │   │   │   ├── activityhistory
        │   │   │   ├── auth
        │   │   │   ├── captain
        │   │   │   ├── catchmaxlength
        │   │   │   ├── chat
        │   │   │   ├── comment
        │   │   │   ├── fish
        │   │   │   ├── fishencyclopedia
        │   │   │   ├── fishingtrippost
        │   │   │   ├── fishingtriprecruitment
        │   │   │   ├── fishpoint
        │   │   │   ├── fishpointsummary
        │   │   │   ├── like
        │   │   │   ├── member
        │   │   │   ├── region
        │   │   │   ├── reservation
        │   │   │   ├── reservationdate
        │   │   │   ├── review
        │   │   │   ├── ship
        │   │   │   └── shipfishingpost
        │   │   └── global
        │   │       ├── advice
        │   │       ├── aop
        │   │       ├── auth
        │   │       ├── config
        │   │       ├── event
        │   │       ├── exception
        │   │       ├── scheduler
        │   │       ├── storage
        │   │       ├── util
        │   │       ├── validator
        │   │       └── websocket
        │   └── resources
        │       └── logback-spring.xml
        └── test
```

---

## 4. 전체 아키텍처

```mermaid
flowchart TB
    Client[Web / Mobile Client] --> API[Spring Boot Backend]

    API --> Auth[Auth / Member]
    API --> Post[Fishing Trip Post]
    API --> ShipPost[Ship Fishing Post]
    API --> Reservation[Reservation]
    API --> Review[Review]
    API --> Chat[Chat]
    API --> FishInfo[Fish / Fish Point]

    API --> MySQL[(MySQL)]
    API --> Redis[(Redis)]
    API --> MongoDB[(MongoDB)]
    API --> S3[(AWS S3)]

    Chat --> WebSocket[WebSocket]
```

---

## 5. 핵심 도메인

| 도메인 | 설명 |
|---|---|
| `member` | 회원 정보, 인증 사용자 관리 |
| `auth` | 로그인, OAuth2, JWT 인증 흐름 |
| `fish` | 어종 정보 관리 |
| `fishencyclopedia` | 어종 백과/상세 정보 |
| `fishpoint` | 낚시 포인트 정보 |
| `fishpointsummary` | 포인트 요약 정보 |
| `fishingtrippost` | 출조/낚시 동행 게시글 |
| `fishingtriprecruitment` | 출조 모집 관리 |
| `ship` | 선박 정보 |
| `shipfishingpost` | 선상 낚시 게시글 |
| `reservation` | 예약 처리 |
| `reservationdate` | 예약 가능 날짜/잔여 수량 관리 |
| `review` | 예약/서비스 후기 |
| `comment` | 게시글 댓글 |
| `like` | 게시글 좋아요 |
| `chat` | 실시간 채팅 |
| `region` | 지역 정보 |
| `captain` | 선장/운영자 관련 정보 |
| `activityhistory` | 사용자 활동 이력 |
| `catchmaxlength` | 어종별 최대어 기록 관리 |

---

## 6. 주요 기능

### 회원/인증

- Spring Security 기반 인증/인가
- OAuth2 Client 기반 소셜 로그인 확장
- JWT 기반 인증 토큰 처리
- 이메일 기능 확장 가능

### 낚시 정보

- 어종 정보 조회
- 어종 백과 정보 관리
- 낚시 포인트 및 지역 기반 검색
- 공간 데이터 기반 위치 검색 확장

### 게시글/커뮤니티

- 출조 모집 게시글 작성/조회/수정/삭제
- 선상 낚시 게시글 작성/조회/수정/삭제
- 댓글/좋아요 기능
- 사용자 활동 이력 관리

### 예약

- 선상 낚시 게시글 기반 예약
- 예약 가능 날짜 관리
- 잔여 수량 관리
- 예약 후 리뷰 작성 흐름 확장

### 채팅

- WebSocket 기반 실시간 채팅
- MongoDB 기반 채팅 메시지 저장 구조 확장

### 운영/공통

- Global Exception Handling
- 공통 응답 DTO
- AOP 기반 공통 로직 분리
- Scheduler 기반 집계/후처리 확장
- Logback 기반 로그 설정
- S3 파일 저장소 연동 구조

---

## 7. 예약 흐름

```mermaid
sequenceDiagram
    participant U as User
    participant API as Backend API
    participant Post as ShipFishingPost
    participant Date as ReservationDate
    participant R as Reservation
    participant DB as MySQL

    U->>API: 선상 낚시 게시글 조회
    API->>Post: 게시글 상세 조회
    API-->>U: 예약 가능 날짜/잔여 수량 응답

    U->>API: 예약 요청
    API->>Date: 예약 가능 여부 확인
    Date->>Date: 잔여 수량 차감
    API->>R: 예약 생성
    R->>DB: 예약 정보 저장
    API-->>U: 예약 성공 응답
```

---

## 8. 채팅 흐름

```mermaid
sequenceDiagram
    participant A as User A
    participant B as User B
    participant WS as WebSocket Endpoint
    participant S as Chat Service
    participant M as MongoDB

    A->>WS: 채팅방 입장
    B->>WS: 채팅방 입장
    A->>WS: 메시지 전송
    WS->>S: 메시지 처리
    S->>M: 메시지 저장
    S-->>A: 메시지 브로드캐스트
    S-->>B: 메시지 브로드캐스트
```

---

## 9. 실행 방법

### 1) 백엔드 디렉터리 이동

```bash
cd backend
```

### 2) 빌드

```bash
./gradlew clean build
```

Windows 환경:

```bash
gradlew.bat clean build
```

### 3) 실행

```bash
./gradlew bootRun
```

또는 JAR 실행:

```bash
java -jar build/libs/backend-0.0.1-SNAPSHOT.jar
```

---

## 10. 환경 변수 예시

실제 secret 값은 Git에 올리지 않고 환경 변수 또는 배포 환경 secret으로 관리하는 것을 권장합니다.

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/fishing_service
    username: root
    password: ${DB_PASSWORD}

  data:
    redis:
      host: localhost
      port: 6379

    mongodb:
      uri: ${MONGODB_URI}

jwt:
  secret: ${JWT_SECRET}

cloud:
  aws:
    credentials:
      access-key: ${AWS_ACCESS_KEY}
      secret-key: ${AWS_SECRET_KEY}
    s3:
      bucket: ${S3_BUCKET}
```

---

## 11. API 문서

SpringDoc OpenAPI가 포함되어 있으므로 실행 후 Swagger UI를 통해 API를 확인할 수 있습니다.

```text
http://localhost:8080/swagger-ui/index.html
```

---

## 12. 테스트 포인트

| 영역 | 테스트 시나리오 |
|---|---|
| 인증 | 로그인 성공/실패, JWT 검증, 인증 필요 API 접근 제한 |
| 게시글 | 게시글 생성/조회/수정/삭제, 검색 조건 필터링 |
| 예약 | 잔여 수량 차감, 중복 예약 방지, 예약 가능 날짜 검증 |
| 리뷰 | 예약 완료 사용자만 리뷰 작성 가능 여부 |
| 좋아요 | 중복 좋아요 방지, 좋아요 취소 |
| 채팅 | 메시지 전송, 메시지 저장, 채팅방 입장/퇴장 |
| 검색 | QueryDSL 기반 조건 검색, 위치 기반 검색 |
| 파일 | 이미지 업로드, S3 저장 실패 처리 |

---

![1](https://github.com/user-attachments/assets/7644982b-8ad6-486c-8043-02a54705f1e8)
![2](https://github.com/user-attachments/assets/c3ced4d7-b029-4cd8-abf1-59db90329206)
![3](https://github.com/user-attachments/assets/c90bd1bb-8af8-4786-a829-692bb96f0374)
![4](https://github.com/user-attachments/assets/f61df4b5-ed73-40c5-a8c0-7433e65b8b80)
![5](https://github.com/user-attachments/assets/4eeb3faf-a272-4b80-96ae-28be2dadcb97)
![6](https://github.com/user-attachments/assets/86e22bdf-bbe4-4651-b72b-ea3b46196acf)
![7](https://github.com/user-attachments/assets/db4349b2-4b63-412d-9d78-1f613ad08806)
![8](https://github.com/user-attachments/assets/21c02c9f-372c-45f0-b481-37d485ca51bf)
![9](https://github.com/user-attachments/assets/50066b4b-cdeb-4839-9da4-90f7546cac4b)
![10](https://github.com/user-attachments/assets/695e12ce-11a0-445e-ae4f-069a81d8eac9)
![11](https://github.com/user-attachments/assets/52d47a47-0106-4c17-995d-257eb44bb3ac)
![12](https://github.com/user-attachments/assets/825c0587-f302-4f84-a33d-90b3d5e11df6)
![13](https://github.com/user-attachments/assets/e06e836d-3960-41d0-b143-463ad47a557e)
![14](https://github.com/user-attachments/assets/7735b303-da2c-4419-904f-850862cb85f1)
![15](https://github.com/user-attachments/assets/0a69ac2c-fc03-4d13-8f4c-1a8258aee3ad)
![16](https://github.com/user-attachments/assets/9c635742-afa0-45df-bff1-ac5004a50387)
![17](https://github.com/user-attachments/assets/0b58e1d7-3b7e-4bbe-93df-5cfc266bb6f6)
![18](https://github.com/user-attachments/assets/c53a1956-9f9b-4680-ab91-04ff9d28d267)
![19](https://github.com/user-attachments/assets/b579d563-8025-4645-9f23-840f5c9a65fd)
![20](https://github.com/user-attachments/assets/c975ba28-8956-4063-aafd-a464eb81d594)
![21](https://github.com/user-attachments/assets/39170e82-ee80-41d7-8e63-1418af88a8b9)
![22](https://github.com/user-attachments/assets/72e6066b-22bc-4485-bb5d-ac1c42ad8640)
![23](https://github.com/user-attachments/assets/6c5f2f12-a22a-4f81-8f2f-789754d8c5fd)
![24](https://github.com/user-attachments/assets/7d410ced-b881-46a3-b782-6e98ebbdbd82)
![25](https://github.com/user-attachments/assets/3661abde-14f3-406b-a1df-8ac2f103225f)
![26](https://github.com/user-attachments/assets/b63b7751-3a9c-4b5e-8bc4-6f6ec3425420)
![27](https://github.com/user-attachments/assets/850fdd07-d10d-46c3-975a-1961badb9758)
![28](https://github.com/user-attachments/assets/902d5e1f-c41c-48b6-8aa6-f9b260a90bcd)
![29](https://github.com/user-attachments/assets/701d1925-b0df-4699-8645-67a938495259)
![30](https://github.com/user-attachments/assets/8763eaaf-a1cd-4ac6-8cb1-d72146361984)
![31](https://github.com/user-attachments/assets/eaf541f9-af23-4b81-8502-d1cba7129b33)
![32](https://github.com/user-attachments/assets/d84bedfa-841a-4f4f-a2d0-f88f6d4cbb4e)
![33](https://github.com/user-attachments/assets/f4b8986d-ed3f-4c68-8b6f-2047dbde61de)
![34](https://github.com/user-attachments/assets/33192dfa-2fb8-4479-a030-45cdddbd3a29)
![35](https://github.com/user-attachments/assets/a4596bbb-86f7-4386-a117-6ed466c9e3ff)
![36](https://github.com/user-attachments/assets/eb4e64ea-5177-4c1a-a877-052e2b3fab17)
![37](https://github.com/user-attachments/assets/7d7f45e3-a322-457e-8062-0895c6e5ab64)
![38](https://github.com/user-attachments/assets/d984772a-f3c2-4754-bcc7-0cd2bacb598c)
![39](https://github.com/user-attachments/assets/9f786fe4-8b93-4861-872d-3505b7bf6fdb)
![40](https://github.com/user-attachments/assets/af20a924-c324-4778-b031-17746fac5a0f)
![41](https://github.com/user-attachments/assets/d2f54a49-b560-4f5e-baf4-2ae60348e9cc)
![42](https://github.com/user-attachments/assets/6533846a-ca79-443e-9d31-46eed7cbcec0)
![43](https://github.com/user-attachments/assets/fd715b83-79cb-403c-af92-18ae026661dc)
![44](https://github.com/user-attachments/assets/6dc9528c-6523-4c16-9688-f8a20bd5dcdb)
![45](https://github.com/user-attachments/assets/5edd6e5f-2f2d-417d-b3c6-61361a034bf6)
![46](https://github.com/user-attachments/assets/5f417449-ebfd-417f-a0dd-825e85095fde)
