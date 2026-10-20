# 시험 전 실전 연습: "회원 로그인 그림판" 처음부터 끝까지 따라 만들기

수업 녹음에서 나온 출제 방향(메뉴·대화상자 필수, 라디오·체크박스 주고받기, 터치로 메뉴 띄우기, 작년 기출인 회원가입 데이터 일치 확인)을 **한 문제로 합친 예상 문제**입니다.
A~N 단계를 순서대로 따라 하고, **✅ 확인** 표시가 나올 때마다 실행해서 화면을 확인하세요.

- 이 가이드는 새 프로젝트(Empty Views Activity, Java)에서 **적힌 그대로 따라 했을 때 빌드되고 동작하는 것을 확인**했습니다.
- 완성본은 [`practice/`](practice/) 폴더에 있습니다. 막히면 비교해 보세요.
- 목표 시간: **100분** (실제 시험은 약 2시간)

---

## 문제

> **[문제] 회원 로그인 그림판 앱을 작성하시오.**
>
> 1. 앱이 처음 실행되면 내장 메모리 파일 `members.txt`에 계정 3개(user1/1111, user2/2222, user3/3333)를 저장한다. 이미 있으면 다시 저장하지 않는다.
> 2. 옵션 메뉴(메뉴 XML)에 **로그인, 회원가입, 설정 >>(펜 색상, 펜 옵션), 로그아웃**을 만든다.
> 3. **로그인**: 사용자 정의 대화상자에서 아이디·비밀번호를 입력받아 파일의 데이터와 비교하고, 결과(없는 아이디 / 비밀번호 불일치 / 성공)를 **메시지창**으로 보여 준다. 성공하면 화면 위쪽에 "OOO 님 로그인됨"을 표시한다.
> 4. **회원가입**: 같은 대화상자로 입력받아 아이디가 중복되지 않으면 파일에 추가하고 결과를 메시지창으로 보여 준다.
> 5. 메인 화면에 펜 색상 **라디오버튼 3개**(빨강·초록·파랑)와 **체크박스 2개**(굵은 선·채우기)를 둔다.
>    - 메뉴 **펜 색상** → 라디오 목록 대화상자. 화면의 라디오버튼 값으로 시작하고, 확인을 누르면 화면 라디오버튼에 반영한다.
>    - 메뉴 **펜 옵션** → 체크박스 목록 대화상자. 화면의 체크박스 값으로 시작하고, 확인을 누르면 화면 체크박스에 반영한다.
> 6. 아래 그림 영역을 **드래그하면 원**을 그린다. 로그인하지 않았으면 "먼저 로그인하세요" 토스트를 띄운다.
> 7. 그림 영역을 **짧게 탭하면 컨텍스트 메뉴**(마지막 원 지우기, 모두 지우기, 펜 색상 선택)가 뜬다.
> 8. **로그아웃**은 확인/취소 대화상자로 묻는다.

| 단계 | 내용 | 시간 |
|---|---|---|
| A~B | 프로젝트 만들기, 테마 고치기 | 5분 |
| C~D | 화면 XML, MainActivity 뼈대 | 15분 |
| E~F | 옵션 메뉴, 메시지창 | 10분 |
| G~I | 파일, 로그인, 회원가입, 로그아웃 | 25분 |
| J~K | 라디오·체크박스 ↔ 대화상자 | 15분 |
| L~M | 터치로 원 그리기, 터치 → 컨텍스트 메뉴 | 20분 |
| N | 최종 점검 | 10분 |

---

## A. 프로젝트 만들기

1. Android Studio → **New Project** → **Empty Views Activity** → Next
2. Name `practice`, Language **Java**, Minimum SDK는 기본값 → Finish
3. Gradle 동기화가 끝날 때까지 기다립니다(오른쪽 아래 진행 막대).

---

## B. 테마 고치기 (안 하면 ⋮ 메뉴가 안 보임)

`res/values/themes.xml`을 열어 `parent="Theme.Material3.DayNight.NoActionBar"`에서 **`.NoActionBar`를 지웁니다.**

<!-- apply: edit app/src/main/res/values/themes.xml | parent="Theme.Material3.DayNight.NoActionBar" => parent="Theme.Material3.DayNight" -->
```xml
    <style name="Base.Theme.Practice" parent="Theme.Material3.DayNight">
```

`res/values-night/themes.xml`(Project 창에서 `themes` 폴더를 펼치면 `themes.xml (night)`)도 **똑같이** 고칩니다. 이걸 빼먹으면 다크 모드에서만 메뉴가 사라집니다.

<!-- apply: edit app/src/main/res/values-night/themes.xml | parent="Theme.Material3.DayNight.NoActionBar" => parent="Theme.Material3.DayNight" -->
```xml
    <style name="Base.Theme.Practice" parent="Theme.Material3.DayNight">
```

<!-- checkpoint: B -->
✅ **확인** ▶ Run → 화면 위에 보라/회색 **앱바(제목줄)**가 생기고 "Hello World!"가 보이면 성공.

---

## C. 화면 XML 만들기

### C-1. `res/layout/activity_main.xml`
파일을 열고 오른쪽 위 **Code** 탭을 누른 뒤, 내용을 **전부 지우고** 아래로 바꿉니다.

<!-- apply: write app/src/main/res/layout/activity_main.xml -->
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true"
    android:orientation="vertical" >

    <TextView
        android:id="@+id/tvStatus"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="로그인이 필요합니다"
        android:textSize="20dp" />

    <RadioGroup
        android:id="@+id/rgColor"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal" >

        <RadioButton
            android:id="@+id/rdoRed"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:checked="true"
            android:text="빨강" />

        <RadioButton
            android:id="@+id/rdoGreen"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="초록" />

        <RadioButton
            android:id="@+id/rdoBlue"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="파랑" />
    </RadioGroup>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal" >

        <CheckBox
            android:id="@+id/chkBold"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="굵은 선" />

        <CheckBox
            android:id="@+id/chkFill"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="채우기" />
    </LinearLayout>

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="드래그: 원 그리기 / 짧게 탭: 메뉴" />

    <LinearLayout
        android:id="@+id/pictureLayout"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:background="#EEEEEE"
        android:orientation="vertical" >
    </LinearLayout>

</LinearLayout>
```
> `android:id="@+id/main"`이 없어졌기 때문에 지금 MainActivity의 `R.id.main`에 **빨간 줄(빌드 에러)**이 생깁니다. 정상입니다. D단계에서 MainActivity를 바꾸면 사라집니다.

### C-2. 대화상자 레이아웃 `res/layout/dialog_login.xml`
`res/layout` 우클릭 → New → **Layout Resource File** → File name `dialog_login`, Root element `LinearLayout` → OK → Code 탭에서 전부 바꾸기.

<!-- apply: write app/src/main/res/layout/dialog_login.xml -->
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp"
    android:orientation="vertical" >

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="아이디"
        android:textSize="20dp" />

    <EditText
        android:id="@+id/dlgId"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="textVisiblePassword" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="비밀번호"
        android:textSize="20dp" />

    <EditText
        android:id="@+id/dlgPw"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="textPassword" />

</LinearLayout>
```
> 아이디 칸의 `textVisiblePassword`는 키보드가 한글로 바뀌어 `user1`이 `ㅕㄴㄷㄱ1`로 들어가는 실수를 막아 줍니다.

### C-3. 메뉴 `res/menu/menu1.xml`
`res` 우클릭 → New → **Android Resource File** → File name `menu1`, Resource type **Menu** → OK → Code 탭에서 전부 바꾸기.

<!-- apply: write app/src/main/res/menu/menu1.xml -->
```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android" >

    <item
        android:id="@+id/itemLogin"
        android:title="로그인" />
    <item
        android:id="@+id/itemSignup"
        android:title="회원가입" />

    <item android:title="설정 >>">
        <menu>
            <item
                android:id="@+id/itemColor"
                android:title="펜 색상 (라디오)" />
            <item
                android:id="@+id/itemOption"
                android:title="펜 옵션 (체크박스)" />
        </menu>
    </item>

    <item
        android:id="@+id/itemLogout"
        android:title="로그아웃" />

</menu>
```

---

## D. MainActivity 뼈대

`MainActivity.java`를 열고 **맨 위 `package ...;` 줄만 남기고 나머지를 전부 지운 뒤** 아래를 붙여 넣습니다. (새 프로젝트에 있던 `EdgeToEdge`·`R.id.main` 코드도 이때 함께 사라집니다.)

`// [E] ...`처럼 생긴 줄은 **나중 단계에서 코드로 바꿀 자리 표시**입니다. 지우지 마세요.

<!-- apply: write app/src/main/java/com/example/practice/MainActivity.java -->
```java
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // ===== 위젯 변수 =====
    TextView tvStatus;
    RadioGroup rgColor;
    RadioButton[] rdoColor = new RadioButton[3];
    CheckBox chkBold, chkFill;
    LinearLayout pictureLayout;
    MyGraphicView graphicView;

    // ===== 공용 데이터 (그림판 클래스에서도 쓰므로 static) =====
    static String loginUser = null;           // 로그인한 아이디 (없으면 null)
    static int curColor = Color.RED;
    static boolean bold = false, fill = false;
    final static int[] colors = { Color.RED, Color.GREEN, Color.BLUE };
    final static String[] colorNames = { "빨강", "초록", "파랑" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("회원 로그인 그림판");

        tvStatus = (TextView) findViewById(R.id.tvStatus);
        rgColor = (RadioGroup) findViewById(R.id.rgColor);
        rdoColor[0] = (RadioButton) findViewById(R.id.rdoRed);
        rdoColor[1] = (RadioButton) findViewById(R.id.rdoGreen);
        rdoColor[2] = (RadioButton) findViewById(R.id.rdoBlue);
        chkBold = (CheckBox) findViewById(R.id.chkBold);
        chkFill = (CheckBox) findViewById(R.id.chkFill);
        pictureLayout = (LinearLayout) findViewById(R.id.pictureLayout);

        // [G-2] 계정 3개 준비
        // [J-1] 라디오버튼 리스너
        // [K-1] 체크박스 리스너
        // [L-1] 그림판 붙이기
        // [M-1] 그림판에 컨텍스트 메뉴 등록
    }

    // [E] 옵션 메뉴
    // [F] 메시지창
    // [G] 파일 처리
    // [H] 로그인 · 로그아웃
    // [I] 회원가입
    // [J] 라디오 대화상자
    // [K] 체크박스 대화상자
    // [M] 컨텍스트 메뉴
}
```
> 빨간 줄(import 관련)이 보이면 클릭 후 **Alt+Enter**(맥은 Option+Enter)로 import를 추가하세요. 위 import 목록을 그대로 붙였다면 생기지 않습니다.
> `MyGraphicView`에 빨간 줄이 있으면 정상입니다. L단계에서 만듭니다. 대신 지금은 **실행이 안 되므로** 아래처럼 임시로 막아 둡니다.

`MyGraphicView graphicView;` 줄 앞에 `//`를 붙여 잠시 주석 처리합니다.

<!-- apply: edit app/src/main/java/com/example/practice/MainActivity.java |     MyGraphicView graphicView; =>     // MyGraphicView graphicView; -->
```java
    // MyGraphicView graphicView;
```

<!-- checkpoint: D -->
✅ **확인** ▶ Run → 제목 "회원 로그인 그림판", "로그인이 필요합니다", 라디오버튼 3개(빨강 선택됨), 체크박스 2개, 회색 그림 영역이 보이면 성공.

---

## E. 옵션 메뉴

`// [E] 옵션 메뉴` 줄을 아래 코드로 바꿉니다. 아직 기능이 없으니 메뉴를 누르면 "준비 중" 토스트만 띄웁니다.

<!-- apply: replace app/src/main/java/com/example/practice/MainActivity.java | // [E] 옵션 메뉴 -->
```java
    // ===== [E] 옵션 메뉴 =====
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.menu1, menu);
        return true;
    }

    // R.id는 switch에 쓰면 에러 → if 문으로
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.itemLogin) {
            Toast.makeText(getApplicationContext(), "로그인 준비 중", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.itemSignup) {
            Toast.makeText(getApplicationContext(), "회원가입 준비 중", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.itemColor) {
            Toast.makeText(getApplicationContext(), "펜 색상 준비 중", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.itemOption) {
            Toast.makeText(getApplicationContext(), "펜 옵션 준비 중", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.itemLogout) {
            Toast.makeText(getApplicationContext(), "로그아웃 준비 중", Toast.LENGTH_SHORT).show();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
```

<!-- checkpoint: E -->
✅ **확인** ▶ Run → 오른쪽 위 **⋮** → 로그인 / 회원가입 / 설정 >> / 로그아웃이 보이고, 누르면 "○○ 준비 중" 토스트가 뜸. "설정 >>"를 누르면 펜 색상·펜 옵션 서브메뉴가 나옴.

---

## F. 메시지창 (결과 알려 주기)

`// [F] 메시지창` 줄을 바꿉니다.

<!-- apply: replace app/src/main/java/com/example/practice/MainActivity.java | // [F] 메시지창 -->
```java
    // ===== [F] 메시지창 : 확인 버튼만 있는 대화상자 =====
    void showMessage(String title, String msg) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle(title);
        dlg.setMessage(msg);
        dlg.setIcon(R.mipmap.ic_launcher);
        dlg.setPositiveButton("확인", null);
        dlg.show();
    }
```

---

## G. 파일 처리 (계정 저장·읽기·찾기)

### G-1. `// [G] 파일 처리` 줄을 바꿉니다.

<!-- apply: replace app/src/main/java/com/example/practice/MainActivity.java | // [G] 파일 처리 -->
```java
    // ===== [G] 파일 처리 : members.txt 에 한 줄씩 "아이디,비밀번호" =====
    // 파일 끝에 이어 쓰기 (MODE_PRIVATE 로 쓰면 덮어써서 기존 회원이 사라짐)
    void writeMember(String id, String pw) {
        try {
            FileOutputStream outFs = openFileOutput("members.txt", Context.MODE_APPEND);
            String str = id + "," + pw + "\n";
            outFs.write(str.getBytes());
            outFs.close();
        } catch (IOException e) {
        }
    }

    // 파일 전체 읽기 (파일이 없으면 "")
    String readMembers() {
        try {
            FileInputStream inFs = openFileInput("members.txt");
            byte[] txt = new byte[inFs.available()];
            inFs.read(txt);
            inFs.close();
            return new String(txt);
        } catch (IOException e) {
            return "";
        }
    }

    // 아이디로 비밀번호 찾기 (없는 아이디면 null)
    String findPw(String id) {
        String[] lines = readMembers().split("\n");
        for (int i = 0; i < lines.length; i++) {
            String[] data = lines[i].split(",");
            if (data.length == 2 && data[0].equals(id))
                return data[1];
        }
        return null;
    }
```

### G-2. onCreate 안의 `// [G-2] 계정 3개 준비` 줄을 바꿉니다.

<!-- apply: replace app/src/main/java/com/example/practice/MainActivity.java | // [G-2] 계정 3개 준비 -->
```java
        // [G-2] 처음 실행이면 계정 3개를 파일에 저장
        if (readMembers().equals("")) {
            writeMember("user1", "1111");
            writeMember("user2", "2222");
            writeMember("user3", "3333");
        }
```

<!-- checkpoint: G -->
✅ **확인** ▶ Run → 화면은 그대로. 오른쪽 아래 **Device Explorer** → `data/data/com.example.practice/files/members.txt`를 더블클릭하면 계정 3줄이 보임.

---

## H. 로그인 · 로그아웃

### H-1. `// [H] 로그인 · 로그아웃` 줄을 바꿉니다.

<!-- apply: replace app/src/main/java/com/example/practice/MainActivity.java | // [H] 로그인 · 로그아웃 -->
```java
    // ===== [H] 로그인 : 사용자 정의 대화상자 + 파일 데이터와 비교 + 메시지창 =====
    void showLoginDialog() {
        View dialogView = (View) View.inflate(MainActivity.this, R.layout.dialog_login, null);
        // 대화상자 안 위젯은 dialogView.findViewById 로!
        final EditText dlgId = (EditText) dialogView.findViewById(R.id.dlgId);
        final EditText dlgPw = (EditText) dialogView.findViewById(R.id.dlgPw);

        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("로그인");
        dlg.setIcon(R.mipmap.ic_launcher);
        dlg.setView(dialogView);
        dlg.setPositiveButton("로그인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String id = dlgId.getText().toString().trim();
                String pw = dlgPw.getText().toString().trim();
                String savedPw = findPw(id);

                if (savedPw == null) {
                    showMessage("로그인 실패", "없는 아이디입니다 : " + id);
                } else if (!savedPw.equals(pw)) {          // 문자열 비교는 equals
                    showMessage("로그인 실패", "비밀번호가 일치하지 않습니다");
                } else {
                    loginUser = id;
                    tvStatus.setText(id + " 님 로그인됨");
                    showMessage("로그인 성공", "아이디와 비밀번호가 일치합니다");
                }
            }
        });
        dlg.setNegativeButton("취소", null);
        dlg.show();
    }

    // ===== [H] 로그아웃 : 확인/취소 대화상자 =====
    void showLogoutDialog() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("로그아웃");
        dlg.setMessage("로그아웃 할까요?");
        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                loginUser = null;
                tvStatus.setText("로그인이 필요합니다");
            }
        });
        dlg.setNegativeButton("취소", null);
        dlg.show();
    }
```

### H-2. 메뉴에 연결: [E]에서 만든 `onOptionsItemSelected` 안의 두 줄을 바꿉니다.

`Toast.makeText(getApplicationContext(), "로그인 준비 중", ...` 줄 →
<!-- apply: replace app/src/main/java/com/example/practice/MainActivity.java | Toast.makeText(getApplicationContext(), "로그인 준비 중", Toast.LENGTH_SHORT).show(); -->
```java
            showLoginDialog();
```
`Toast.makeText(getApplicationContext(), "로그아웃 준비 중", ...` 줄 →
<!-- apply: replace app/src/main/java/com/example/practice/MainActivity.java | Toast.makeText(getApplicationContext(), "로그아웃 준비 중", Toast.LENGTH_SHORT).show(); -->
```java
            showLogoutDialog();
```

<!-- checkpoint: H -->
✅ **확인** ▶ Run → ⋮ → 로그인 에서 아래 세 가지를 해 봅니다.
| 입력 | 나와야 하는 메시지창 |
|---|---|
| `nobody` / `1` | 로그인 실패 · 없는 아이디입니다 : nobody |
| `user1` / `9999` | 로그인 실패 · 비밀번호가 일치하지 않습니다 |
| `user2` / `2222` | 로그인 성공 → 화면 위쪽 "user2 님 로그인됨" |

그다음 ⋮ → 로그아웃 → 확인 → "로그인이 필요합니다"로 돌아오면 성공.

---

## I. 회원가입

### I-1. `// [I] 회원가입` 줄을 바꿉니다. (로그인과 같은 dialog_login.xml을 재사용)

<!-- apply: replace app/src/main/java/com/example/practice/MainActivity.java | // [I] 회원가입 -->
```java
    // ===== [I] 회원가입 : 중복 확인 후 파일에 추가 =====
    void showSignupDialog() {
        View dialogView = (View) View.inflate(MainActivity.this, R.layout.dialog_login, null);
        final EditText dlgId = (EditText) dialogView.findViewById(R.id.dlgId);
        final EditText dlgPw = (EditText) dialogView.findViewById(R.id.dlgPw);

        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("회원가입");
        dlg.setView(dialogView);
        dlg.setPositiveButton("가입", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String id = dlgId.getText().toString().trim();
                String pw = dlgPw.getText().toString().trim();

                if (id.equals("") || pw.equals("")) {
                    showMessage("회원가입 실패", "아이디와 비밀번호를 입력하세요");
                } else if (id.contains(",") || pw.contains(",")) {
                    showMessage("회원가입 실패", "쉼표(,)는 쓸 수 없습니다");
                } else if (findPw(id) != null) {
                    showMessage("회원가입 실패", "이미 있는 아이디입니다");
                } else {
                    writeMember(id, pw);
                    showMessage("회원가입 완료", id + " 님이 가입되었습니다");
                }
            }
        });
        dlg.setNegativeButton("취소", null);
        dlg.show();
    }
```

### I-2. 메뉴 연결: `"회원가입 준비 중"` 토스트 줄 →
<!-- apply: replace app/src/main/java/com/example/practice/MainActivity.java | Toast.makeText(getApplicationContext(), "회원가입 준비 중", Toast.LENGTH_SHORT).show(); -->
```java
            showSignupDialog();
```

<!-- checkpoint: I -->
✅ **확인** ▶ Run
1. 회원가입 `user1` / `0000` → "이미 있는 아이디입니다"
2. 회원가입 `kim` / `4321` → "kim 님이 가입되었습니다"
3. 로그인 `kim` / `4321` → 로그인 성공
4. Device Explorer의 `members.txt`에 `kim,4321`이 추가됨 (안 보이면 우클릭 → Synchronize)

---

## J. 라디오버튼 ↔ 라디오 대화상자

### J-1. onCreate 안의 `// [J-1] 라디오버튼 리스너` 줄을 바꿉니다. (라디오버튼이 바뀔 때마다 펜 색상 저장)

<!-- apply: replace app/src/main/java/com/example/practice/MainActivity.java | // [J-1] 라디오버튼 리스너 -->
```java
        // [J-1] 화면 라디오버튼이 바뀌면 펜 색상 저장
        rgColor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                curColor = colors[getColorIndex()];
            }
        });
```

### J-2. `// [J] 라디오 대화상자` 줄을 바꿉니다.

<!-- apply: replace app/src/main/java/com/example/practice/MainActivity.java | // [J] 라디오 대화상자 -->
```java
    // ===== [J] 라디오 =====
    // 화면 라디오버튼 중 몇 번째가 선택됐는지 (0~2)
    int getColorIndex() {
        int checkedId = rgColor.getCheckedRadioButtonId();
        for (int i = 0; i < rdoColor.length; i++)
            if (rdoColor[i].getId() == checkedId) return i;
        return 0;
    }

    // 라디오 목록 대화상자 ↔ 화면 라디오버튼
    void showColorDialog() {
        final int[] selected = { getColorIndex() };          // 화면 → 대화상자 처음 선택값
        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("펜 색상");
        dlg.setSingleChoiceItems(colorNames, selected[0], new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                selected[0] = which;                          // 고른 번호 기억
            }
        });
        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                rdoColor[selected[0]].setChecked(true);      // 대화상자 → 화면
            }
        });
        dlg.setNegativeButton("취소", null);
        dlg.show();
    }
```

### J-3. 메뉴 연결: `"펜 색상 준비 중"` 토스트 줄 →
<!-- apply: replace app/src/main/java/com/example/practice/MainActivity.java | Toast.makeText(getApplicationContext(), "펜 색상 준비 중", Toast.LENGTH_SHORT).show(); -->
```java
            showColorDialog();
```

<!-- checkpoint: J -->
✅ **확인** ▶ Run
1. 화면에서 **파랑** 라디오 선택 → ⋮ → 설정 >> → 펜 색상 → 대화상자에 **파랑**이 선택된 채로 열림 (화면 → 대화상자)
2. 대화상자에서 **초록** → 확인 → 화면 라디오버튼이 **초록**으로 바뀜 (대화상자 → 화면)
3. 다시 열어 빨강 고르고 **취소** → 화면은 초록 그대로

---

## K. 체크박스 ↔ 체크박스 대화상자

### K-1. onCreate 안의 `// [K-1] 체크박스 리스너` 줄을 바꿉니다.

<!-- apply: replace app/src/main/java/com/example/practice/MainActivity.java | // [K-1] 체크박스 리스너 -->
```java
        // [K-1] 화면 체크박스가 바뀌면 펜 옵션 저장
        chkBold.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                bold = isChecked;
            }
        });
        chkFill.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fill = isChecked;
            }
        });
```

### K-2. `// [K] 체크박스 대화상자` 줄을 바꿉니다.

<!-- apply: replace app/src/main/java/com/example/practice/MainActivity.java | // [K] 체크박스 대화상자 -->
```java
    // ===== [K] 체크박스 목록 대화상자 ↔ 화면 체크박스 =====
    void showOptionDialog() {
        final String[] optArray = { "굵은 선", "채우기" };
        final boolean[] checkArray = { chkBold.isChecked(), chkFill.isChecked() };   // 화면 → 대화상자
        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("펜 옵션");
        dlg.setMultiChoiceItems(optArray, checkArray, new DialogInterface.OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkArray[which] = isChecked;
            }
        });
        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                chkBold.setChecked(checkArray[0]);           // 대화상자 → 화면
                chkFill.setChecked(checkArray[1]);
            }
        });
        dlg.setNegativeButton("취소", null);
        dlg.show();
    }
```

### K-3. 메뉴 연결: `"펜 옵션 준비 중"` 토스트 줄 →
<!-- apply: replace app/src/main/java/com/example/practice/MainActivity.java | Toast.makeText(getApplicationContext(), "펜 옵션 준비 중", Toast.LENGTH_SHORT).show(); -->
```java
            showOptionDialog();
```

<!-- checkpoint: K -->
✅ **확인** ▶ Run
1. 화면에서 **굵은 선** 체크 → ⋮ → 설정 >> → 펜 옵션 → 대화상자에 굵은 선이 체크된 채로 열림
2. 대화상자에서 **채우기**도 체크 → 확인 → 화면 체크박스 두 개 모두 체크됨

---

## L. 그림판: 드래그해서 원 그리기 (9장)

### L-1. 클래스 맨 아래, 마지막 `}` **바로 위**에 그림판 클래스를 추가합니다.
`// [M] 컨텍스트 메뉴` 줄 **아래**에 붙여 넣으면 됩니다(그 줄은 M단계에서 씁니다).

<!-- apply: after app/src/main/java/com/example/practice/MainActivity.java | // [M] 컨텍스트 메뉴 -->
```java

    // ===== [L] 그림판 뷰 =====
    private static class MyGraphicView extends View {
        // 그린 원들 : {중심x, 중심y, 반지름, 색, 굵기(1/0), 채우기(1/0)}
        ArrayList<int[]> shapes = new ArrayList<int[]>();
        int startX = -1, startY = -1, stopX = -1, stopY = -1;
        boolean dragging = false;

        public MyGraphicView(Context context) {
            super(context);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (loginUser == null) {                          // 로그인 안 했으면 안 그림
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    Toast.makeText(getContext(), "먼저 로그인하세요", Toast.LENGTH_SHORT).show();
                return true;
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = stopX = (int) event.getX();
                    startY = stopY = (int) event.getY();
                    dragging = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    stopX = (int) event.getX();
                    stopY = (int) event.getY();
                    this.invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    stopX = (int) event.getX();
                    stopY = (int) event.getY();
                    dragging = false;
                    // [M-2] 탭이면 메뉴
                    shapes.add(new int[] { startX, startY, radius(), curColor, bold ? 1 : 0, fill ? 1 : 0 });
                    this.invalidate();
                    break;
            }
            return true;
        }

        int radius() {
            return (int) Math.sqrt(Math.pow(stopX - startX, 2) + Math.pow(stopY - startY, 2));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            for (int i = 0; i < shapes.size(); i++) {
                int[] s = shapes.get(i);
                drawCircle(canvas, s[0], s[1], s[2], s[3], s[4] == 1, s[5] == 1);
            }
            if (dragging)                                     // 끌고 있는 중인 원
                drawCircle(canvas, startX, startY, radius(), curColor, bold, fill);
        }

        void drawCircle(Canvas canvas, int x, int y, int r, int color, boolean b, boolean f) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(color);
            paint.setStrokeWidth(b ? 15 : 5);
            paint.setStyle(f ? Paint.Style.FILL : Paint.Style.STROKE);
            canvas.drawCircle(x, y, r, paint);
        }
    }
```

### L-2. D단계에서 주석 처리했던 줄의 `//`를 지웁니다.
<!-- apply: edit app/src/main/java/com/example/practice/MainActivity.java |     // MyGraphicView graphicView; =>     MyGraphicView graphicView; -->
```java
    MyGraphicView graphicView;
```

### L-3. onCreate 안의 `// [L-1] 그림판 붙이기` 줄을 바꿉니다.
<!-- apply: replace app/src/main/java/com/example/practice/MainActivity.java | // [L-1] 그림판 붙이기 -->
```java
        // [L-1] 회색 영역(pictureLayout)에 그림판 붙이기
        graphicView = new MyGraphicView(this);
        pictureLayout.addView(graphicView);
```

<!-- checkpoint: L -->
✅ **확인** ▶ Run
1. 로그인 전에 회색 영역 터치 → "먼저 로그인하세요"
2. user1/1111 로그인 → 회색 영역을 드래그 → 빨간 원이 따라 커지고, 손을 떼면 남음
3. 파랑 + 굵은 선 + 채우기 선택 후 드래그 → 굵은 파란 채운 원

---

## M. 터치하면 컨텍스트 메뉴 띄우기

### M-1. onCreate 안의 `// [M-1] 그림판에 컨텍스트 메뉴 등록` 줄을 바꿉니다.
<!-- apply: replace app/src/main/java/com/example/practice/MainActivity.java | // [M-1] 그림판에 컨텍스트 메뉴 등록 -->
```java
        // [M-1] 그림판에 컨텍스트 메뉴 등록 (빠뜨리면 메뉴 안 뜸)
        registerForContextMenu(graphicView);
```

### M-2. 그림판의 `// [M-2] 탭이면 메뉴` 줄과 바로 아래 `shapes.add(...)` 줄을 함께 바꿉니다.
<!-- apply: replace2 app/src/main/java/com/example/practice/MainActivity.java | // [M-2] 탭이면 메뉴 -->
```java
                    // [M-2] 거의 안 움직였으면 = 탭 → 메뉴, 아니면 원 저장
                    if (Math.abs(stopX - startX) < 10 && Math.abs(stopY - startY) < 10)
                        this.showContextMenu();
                    else
                        shapes.add(new int[] { startX, startY, radius(), curColor, bold ? 1 : 0, fill ? 1 : 0 });
```

### M-3. `// [M] 컨텍스트 메뉴` 줄을 바꿉니다.
<!-- apply: replace app/src/main/java/com/example/practice/MainActivity.java | // [M] 컨텍스트 메뉴 -->
```java
    // ===== [M] 컨텍스트 메뉴 (그림판을 탭하면 뜸) =====
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v == graphicView) {
            menu.setHeaderTitle("그림 메뉴");
            menu.add(0, 1, 0, "마지막 원 지우기");
            menu.add(0, 2, 0, "모두 지우기");
            menu.add(0, 3, 0, "펜 색상 선택");
        }
    }

    // menu.add 로 만든 숫자 ID는 switch 써도 됨
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                if (graphicView.shapes.size() > 0)
                    graphicView.shapes.remove(graphicView.shapes.size() - 1);
                graphicView.invalidate();
                return true;
            case 2:
                graphicView.shapes.clear();
                graphicView.invalidate();
                return true;
            case 3:
                showColorDialog();
                return true;
        }
        return super.onContextItemSelected(item);
    }
```

<!-- checkpoint: M -->
✅ **확인** ▶ Run → 로그인 후 원 2개 그리기 → 회색 영역을 **짧게 탭** → "그림 메뉴"(마지막 원 지우기 / 모두 지우기 / 펜 색상 선택)
1. 마지막 원 지우기 → 원 1개만 남음
2. 펜 색상 선택 → 라디오 대화상자 → 초록 → 화면 라디오버튼도 초록
3. 모두 지우기 → 전부 사라짐

---

## N. 최종 점검 (채점하듯이)

| # | 문제 요구사항 | 확인 |
|---|---|---|
| 1 | 처음 실행 시 members.txt에 계정 3개 | Device Explorer |
| 2 | 옵션 메뉴 XML (서브메뉴 포함) | ⋮ |
| 3 | 로그인: 대화상자 + 파일 비교 + 메시지창 3종 | H 표 |
| 4 | 회원가입: 중복 확인 + 파일 추가 + 메시지창 | I |
| 5 | 라디오 ↔ 대화상자, 체크박스 ↔ 대화상자 (양방향) | J, K |
| 6 | 드래그로 원 그리기, 로그인 전 토스트 | L |
| 7 | 탭 → 컨텍스트 메뉴 | M |
| 8 | 로그아웃 확인/취소 | H |
| + | 다크 모드에서도 ⋮ 보이는지 (설정 → 디스플레이 → 다크 모드) | B |

### 시간이 남으면 해 볼 변형 (시험에서 바뀔 수 있는 부분)
- 계정을 파일 대신 **배열** `String[] ids = {"user1","user2","user3"};`로 → `findPw()` 안만 바꾸면 됨 ([SNIPPETS.md 8번](SNIPPETS.md#8-로그인-계정-3개--일치-여부-메시지창))
- **미리 저장된 에디트텍스트** 값과 비교 → 화면에 EditText 두 개 추가, `findPw()` 대신 `getText().toString()` 비교
- 그림을 원 대신 **선/사각형**으로 → `drawCircle` 대신 `canvas.drawLine` / `drawRect` ([SNIPPETS.md 11번](SNIPPETS.md#11-그래픽-터치로-그리는-그림판))
- 메뉴를 XML 대신 **Java `menu.add`**로 → [SNIPPETS.md 2-2](SNIPPETS.md#2-2-java-코드만으로-xml-없이-숫자-id라서-switch-가능)
- 로그인 성공 시 **토스트를 커스텀 레이아웃**으로 (교재 실습 7-3 toast1.xml)

### 막혔을 때
| 증상 | 원인 |
|---|---|
| ⋮가 없음 | B단계 테마 (values-night 포함) |
| `R.id.main` 빌드 에러 | D단계(MainActivity 통째로 바꾸기)를 안 해서 새 프로젝트 코드가 남아 있음 |
| `MyGraphicView` 빌드 에러 (D~K단계) | D단계 끝의 `// MyGraphicView graphicView;` 주석 처리를 안 함 |
| `constant expression required` | `switch`에 `R.id.xxx` 사용 → if 문 |
| 로그인 대화상자에서 종료 | `findViewById` 대신 `dialogView.findViewById` |
| 로그인이 계속 실패 | 한글 입력 / `==` 대신 `equals` |
| 가입한 회원이 사라짐 | `MODE_APPEND` 대신 `MODE_PRIVATE` |
| 탭해도 메뉴 없음 | M-1 `registerForContextMenu(graphicView)` 빠짐 |
| 처음부터 다시 테스트하고 싶음 | 에뮬레이터에서 앱 아이콘 길게 누르기 → 앱 정보 → 저장공간 → 데이터 삭제 |
