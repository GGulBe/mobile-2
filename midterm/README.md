# 중간고사 대비 앱 (7장 메뉴·대화상자 / 8장 파일 / 9장 그래픽)

코드는 전부 `app/src/main/java/com/example/midterm/MainActivity.java` 한 파일에 있어요. 교재(cookandroid) 스타일을 따랐습니다.

## 계정 3개 (MainActivity 맨 위)
| 아이디 | 비밀번호 |
|---|---|
| user1 | 1111 |
| user2 | 2222 |
| user3 | 3333 |

회원가입한 계정은 앱 내부 파일 `accounts.txt`에 한 줄에 `아이디,비밀번호` 형식으로 쌓여요. 로그인할 때는 위 3개 계정을 먼저 확인하고, 없으면 이 파일에서 찾아요.
- 가입 검사: 빈칸, 쉼표 포함, 비밀번호 불일치, 아이디 중복
- 파일 내용 확인: Android Studio → View → Tool Windows → Device Explorer → `data/data/com.example.midterm/files/`

## 기능 ↔ 교재 위치
| 기능 | 코드 위치 | 교재 |
|---|---|---|
| 로그인 대화상자 (사용자 정의 뷰 `dialog_login.xml`) | `showLoginDialog()` | 예제 7-25~27 |
| 아이디 저장 체크 → 파일 쓰기/읽기 | `writeFile()` / `readFile()` | 예제 8-2, 직접풀어보기 8-1 |
| 회원가입 (로그인창 "회원가입" 버튼 / 옵션 메뉴) → `accounts.txt`에 `MODE_APPEND`로 추가 | `showSignupDialog()` / `findPw()` | 7장 대화상자 + 8장 파일 |
| 옵션 메뉴 (XML `menu1.xml`, 그룹 단일선택 + 서브메뉴) | `onCreateOptionsMenu` / `onOptionsItemSelected` | 예제 7-2, 7-5, 직접풀어보기 7-1 |
| 체크박스 대화상자 ↔ 화면 CheckBox 값 주고받기 | `showSettingDialog()` | 예제 7-20 |
| 라디오(단일선택) 대화상자 | `showColorDialog()` | 예제 7-19 |
| 확인/취소 대화상자 | `showLogoutDialog()` | 예제 7-17 |
| 터치로 그리기 (선/원/사각형) | `MyGraphicView.onTouchEvent` / `onDraw` | 예제 9-5, 직접풀어보기 9-2 |
| **터치(짧게 탭) → 메뉴 띄우기** | `onTouchEvent`의 `ACTION_UP` → `showContextMenu()` | 7장 컨텍스트 메뉴 + 9장 |
| 컨텍스트 메뉴 (`menu2.xml`) | `onCreateContextMenu` / `onContextItemSelected` | 예제 7-11 |

## 시험 때 자주 막히는 것
- **`switch (item.getItemId()) { case R.id.xxx:` 에서 "Constant expression required" 에러**: 최신 Android Studio는 R.id가 상수가 아니에요. `if (id == R.id.xxx) ... else if` 로 바꾸면 됩니다. (`menu.add(0, 1, 0, "...")`처럼 숫자 ID를 쓰면 switch 그대로 써도 OK)
- **로그인 눌러도 대화상자가 계속 다시 뜸**: 키보드가 한글 모드라 `user1`이 `ㅕㄴㄷㄱ1`로 입력된 것. 아이디 EditText에 `android:inputType="textVisiblePassword"`를 주면 영문으로 고정됨 (이미 적용).
- **에뮬레이터가 클릭에 전혀 반응 없음**: 에뮬레이터 자체가 멈춘 것. Device Manager → ⋮ → Cold Boot Now.
- **메뉴(⋮)가 안 보임**: `themes.xml` 부모가 `...NoActionBar`이면 액션바가 없어서 옵션 메뉴도 안 보여요. `Theme.Material3.DayNight`로 두세요.
- **컨텍스트 메뉴가 안 뜸**: `registerForContextMenu(뷰)`를 빠뜨리지 않았는지 확인하세요.
- **그림이 안 바뀜**: 값을 바꾼 뒤 `invalidate()`를 호출해야 다시 그려져요.
- **checkable 메뉴의 체크 표시가 안 바뀜**: `item.setChecked(true)`를 직접 호출해야 해요.
- 새 레이아웃/메뉴 파일 만들기: `res` 우클릭 → New → Android Resource File (Resource type: Layout / Menu).

## 메뉴를 Java 코드로만 만들 때 (XML 없이, 예제 7-6)
```java
menu.add(0, 1, 0, "항목1");
SubMenu sMenu = menu.addSubMenu("서브 >>");
sMenu.add(0, 2, 0, "항목2");
// onOptionsItemSelected 에서 switch (item.getItemId()) { case 1: ... }
```

## 실행
Android Studio → Open → 이 폴더(`midterm`) → ▶ Run.
학교 Windows PC에서 열 경우 **경로에 한글이 없는 곳**(예: `C:\midterm`)에 복사해서 여세요. 경로에 한글이 있으면 Gradle 오류가 날 수 있어요.
