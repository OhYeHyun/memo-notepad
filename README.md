# 📝 나만의 메모장 (Notepad Web Application)

> Spring 기반의 간단하고 직관적인 메모장 웹 서비스입니다.  
> 로그인하지 않아도 메모를 작성할 수 있고, 회원가입 후에는 본인의 메모를 저장 및 관리할 수 있습니다.  
>  
> 🚀 **배포 링크**: [https://memo-notepad.onrender.com](https://memo-notepad.onrender.com)  
(첫 접속시, 로딩이 걸릴 수 있습니다. 기다려주세요 😊)

---

## 📌 주요 기능


### ✅ 메모 기능
- 메모 작성, 수정, 삭제, 체크 여부 토글
- 로그인 사용자 / 비로그인 사용자 각각에 대한 메모 관리


### ✅ 회원 기능
- 회원가입 / 로그인 / 로그아웃 (세션 기반)
- 로그인한 사용자는 세션을 통해 본인의 메모를 지속적으로 관리 가능


### ✅ 비회원(게스트) 기능
- 로그인하지 않아도 메모 작성 가능
- UUID를 통해 게스트 사용자 임시 식별 및 세션 유지


### ✅ 예외 처리 및 검증
- Bean Validation을 통한 입력값 검증
- 예외 발생 시 사용자 친화적인 에러 메시지 출력
- 로그 출력으로 디버깅 편의성 확보

---

## 🔧 향후 개선 기능 (예정)

- 로그인한 사용자에게 자신의 메모만 필터링해서 표시
- 비회원(게스트)의 메모는 세션 종료 시 삭제되도록 처리
- 체크 여부에 따른 메모 필터
- 페이지당 일정 개수만 보여주는 기능
- 메모 필터링해서 검색
- 메모 작성 및 수정 시 작성/수정 시각 표시
- 테스트 코드 보완 및 기본적인 커버리지 확보 (Controller, Service 단위 테스트)
- Spring Security를 도입해 인증/인가 구조 개선
- Thymeleaf 템플릿 개선: 오류 메시지 표시, UI 정리
- 메모에 간단한 제목 필드 추가
- 메모 고정(Pin) 기능: 상단 고정 표시
- 메모 간단한 색상 지정: 색깔로 시각적 구분
- 삭제된 메모 복구 기능 (소프트 딜리트)

---

## 🛠 사용 기술 스택

| 영역       | 기술 |  
|------------|------|  
| Language   | Java 21 |  
| Framework  | Spring Boot, Spring MVC |  
| Template   | Thymeleaf |  
| DB         | H2 (개발용), PostgreSQL|  
| ORM        | Spring Data JPA, Hibernate|  
| 인증       | Spring Session |  
| 검증       | Spring Validation (JSR-380) |  
| 빌드 도구   | Gradle |  
| 기타       | Lombok, Git / GitHub |  

---

## 📁 프로젝트 구조
src  
└── main  
├── controller # 웹 컨트롤러 (LoginController, MemoController)  
├── domain # JPA Entity 클래스 (Member, Memo)  
├── service # 비즈니스 로직 (MemberService, MemoService)  
├── repository # JPA Repository 인터페이스  
├── dto # 폼 요청/응답 DTO (LoginForm, MemberSaveForm 등)  
├── config # 설정 클래스 및 필터, 인터셉터  
└── templates # Thymeleaf 템플릿 (login, memo 등)  

---

## ▶️ 실행 방법

```bash
git clone https://github.com/your-username/notepad.git
cd notepad
./gradlew bootRun
```
웹 브라우저 접속: http://localhost:8080


---

## 👨‍💻 개발자


이름: 오예현  
Email: ohyhohyl@gmail.com  
Blog: https://tyulsjjava.tistory.com  
GitHub: https://github.com/OhYeHyun  
