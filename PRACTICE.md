# 시험 전 실전 연습: "회원 로그인 그림판" 처음부터 끝까지 만들기

수업 녹음에서 나온 출제 방향(메뉴·대화상자 필수, 라디오·체크박스 주고받기, 터치로 메뉴 띄우기, 작년 기출인 회원가입 데이터 일치 확인)을 **한 문제로 합친 예상 문제**입니다.

- **파일마다 코드 블록이 하나**입니다. 파일을 열고 **전부 지운 뒤 통째로 붙여 넣으면** 끝납니다.
- 새 프로젝트(Empty Views Activity, Java)에서 **적힌 그대로 했을 때 빌드되고 동작하는 것을 확인**했습니다.
- 완성본은 [`practice/`](practice/) 폴더에 있습니다.
- 연습 방법: ① 먼저 코드를 보지 말고 문제만 보고 직접 짜 보기(목표 100분) → ② 막히면 아래 코드와 [코드 설명](#4-코드-설명-어디가-어떤-요구사항인지)을 보며 비교 → ③ [5. 테스트](#5-실행해서-테스트)로 채점

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

---

## 1. 프로젝트 만들기

1. Android Studio → **New Project** → **Empty Views Activity** → Next
2. Name `practice`, Language **Java**, Minimum SDK는 기본값 → Finish
3. Gradle 동기화가 끝날 때까지 기다립니다(오른쪽 아래 진행 막대).

---

## 2. 테마 고치기 (안 하면 ⋮ 메뉴가 안 보임)

두 파일에서 `parent="Theme.Material3.DayNight.NoActionBar"`의 **`.NoActionBar`만 지웁니다.**

- `res/values/themes.xml`
- `res/values-night/themes.xml` (Project 창에서 `themes` 폴더를 펼치면 `themes.xml (night)`). 빼먹으면 다크 모드에서만 메뉴가 사라집니다.

<!-- apply: edit app/src/main/res/values/themes.xml | parent="Theme.Material3.DayNight.NoActionBar" => parent="Theme.Material3.DayNight" -->
<!-- apply: edit app/src/main/res/values-night/themes.xml | parent="Theme.Material3.DayNight.NoActionBar" => parent="Theme.Material3.DayNight" -->
```xml
    <style name="Base.Theme.Practice" parent="Theme.Material3.DayNight">
```
(`Base.Theme.Practice`의 `Practice` 부분은 프로젝트 이름에 따라 다릅니다. **parent 부분만** 고치세요.)

---

## 3. 파일 4개 붙여 넣기

| 파일 | 만드는 방법 |
|---|---|
| ① `res/layout/activity_main.xml` | 이미 있음 → 열고 오른쪽 위 **Code** 탭 → 전부 지우고 붙여 넣기 |
| ② `res/layout/dialog_login.xml` | `res/layout` 우클릭 → New → **Layout Resource File** → 이름 `dialog_login` → OK → Code 탭 → 전부 지우고 붙여 넣기 |
| ③ `res/menu/menu1.xml` | `res` 우클릭 → New → **Android Resource File** → 이름 `menu1`, Resource type **Menu** → OK → 전부 지우고 붙여 넣기 |
| ④ `java/.../MainActivity.java` | 이미 있음 → **맨 위 `package ...;` 한 줄만 남기고** 전부 지운 뒤 그 아래에 붙여 넣기 |

### ① res/layout/activity_main.xml
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

### ② res/layout/dialog_login.xml
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

### ③ res/menu/menu1.xml
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

### ④ MainActivity.java (`package` 줄 아래에 통째로)
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

        // 처음 실행이면 계정 3개를 파일에 저장
        if (readMembers().equals("")) {
            writeMember("user1", "1111");
            writeMember("user2", "2222");
            writeMember("user3", "3333");
        }
        // 화면 라디오버튼이 바뀌면 펜 색상 저장
        rgColor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                curColor = colors[getColorIndex()];
            }
        });
        // 화면 체크박스가 바뀌면 펜 옵션 저장
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
        // 회색 영역(pictureLayout)에 그림판 붙이기
        graphicView = new MyGraphicView(this);
        pictureLayout.addView(graphicView);
        // 그림판에 컨텍스트 메뉴 등록 (빠뜨리면 메뉴 안 뜸)
        registerForContextMenu(graphicView);
    }

    // ===== 옵션 메뉴 =====
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
            showLoginDialog();
        } else if (id == R.id.itemSignup) {
            showSignupDialog();
        } else if (id == R.id.itemColor) {
            showColorDialog();
        } else if (id == R.id.itemOption) {
            showOptionDialog();
        } else if (id == R.id.itemLogout) {
            showLogoutDialog();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
    // ===== 메시지창 : 확인 버튼만 있는 대화상자 =====
    void showMessage(String title, String msg) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle(title);
        dlg.setMessage(msg);
        dlg.setIcon(R.mipmap.ic_launcher);
        dlg.setPositiveButton("확인", null);
        dlg.show();
    }
    // ===== 파일 처리 : members.txt 에 한 줄씩 "아이디,비밀번호" =====
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
    // ===== 로그인 : 사용자 정의 대화상자 + 파일 데이터와 비교 + 메시지창 =====
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

    // ===== 로그아웃 : 확인/취소 대화상자 =====
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
    // ===== 회원가입 : 중복 확인 후 파일에 추가 =====
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
    // ===== 라디오 =====
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
    // ===== 체크박스 목록 대화상자 ↔ 화면 체크박스 =====
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
    // ===== 컨텍스트 메뉴 (그림판을 탭하면 뜸) =====
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

    // ===== 그림판 뷰 =====
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
                    // 거의 안 움직였으면 = 탭 → 메뉴, 아니면 원 저장
                    if (Math.abs(stopX - startX) < 10 && Math.abs(stopY - startY) < 10)
                        this.showContextMenu();
                    else
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
}
```

<!-- checkpoint: final -->
▶ **Run**. 빌드 에러가 나면 맨 아래 [막혔을 때](#막혔을-때)를 보세요.

---

## 4. 코드 설명 (어디가 어떤 요구사항인지)

| 문제 번호 | MainActivity에서 찾을 곳 | 핵심 |
|---|---|---|
| 1. 계정 3개 저장 | `onCreate`의 `readMembers().equals("")` 부분, `writeMember()` | 파일이 비어 있을 때만 저장. `Context.MODE_APPEND`로 이어 쓰기 |
| 2. 옵션 메뉴 | `onCreateOptionsMenu()`, `onOptionsItemSelected()` | `getMenuInflater().inflate(R.menu.menu1, menu)`, **switch 대신 if** (R.id는 switch 불가) |
| 3. 로그인 | `showLoginDialog()`, `findPw()`, `showMessage()` | `View.inflate(…, R.layout.dialog_login, null)` → **`dialogView.findViewById`** → `findPw(id)`가 null이면 없는 아이디, 다르면 불일치 |
| 4. 회원가입 | `showSignupDialog()` | 빈칸·쉼표·중복 검사 후 `writeMember()` |
| 5. 라디오 ↔ 대화상자 | `getColorIndex()`, `showColorDialog()`, onCreate의 `rgColor.setOnCheckedChangeListener` | `setSingleChoiceItems(배열, 화면값, …)`로 시작, 확인에서 `rdoColor[번호].setChecked(true)` |
| 5. 체크박스 ↔ 대화상자 | `showOptionDialog()`, onCreate의 `chkBold/chkFill.setOnCheckedChangeListener` | `setMultiChoiceItems(배열, {화면값들}, …)`, 확인에서 `chk.setChecked()` |
| 6. 드래그로 원 | `MyGraphicView.onTouchEvent()`, `onDraw()` | DOWN에서 시작점, MOVE에서 `invalidate()`, UP에서 `shapes`에 저장. 반지름 = 두 점 거리 |
| 7. 탭 → 메뉴 | onCreate의 `registerForContextMenu(graphicView)`, UP의 탭 판정 → `showContextMenu()`, `onCreateContextMenu()`, `onContextItemSelected()` | 거의 안 움직였으면(10픽셀 미만) 탭으로 판단 |
| 8. 로그아웃 | `showLogoutDialog()` | 확인/취소 버튼 대화상자 |

`static`이 붙은 변수(`loginUser`, `curColor`, `bold`, `fill`)는 그림판 클래스(`static class MyGraphicView`)에서도 읽어야 해서 static으로 만들었습니다.

---

## 5. 실행해서 테스트

| # | 할 일 | 나와야 하는 결과 |
|---|---|---|
| 1 | 앱 실행 | 제목 "회원 로그인 그림판", "로그인이 필요합니다", 라디오 3개(빨강 선택), 체크박스 2개, 회색 그림 영역 |
| 2 | Device Explorer → `data/data/com.example.practice/files/members.txt` | 계정 3줄 |
| 3 | ⋮ | 로그인 / 회원가입 / 설정 >> (펜 색상, 펜 옵션) / 로그아웃 |
| 4 | 로그인 전에 회색 영역 터치 | "먼저 로그인하세요" 토스트 |
| 5 | 로그인 `nobody` / `1` | 메시지창 "없는 아이디입니다 : nobody" |
| 6 | 로그인 `user1` / `9999` | 메시지창 "비밀번호가 일치하지 않습니다" |
| 7 | 로그인 `user2` / `2222` | 메시지창 "로그인 성공", 화면 위 "user2 님 로그인됨" |
| 8 | 로그아웃 → 확인 | "로그인이 필요합니다" |
| 9 | 회원가입 `user1` / `0000` | "이미 있는 아이디입니다" |
| 10 | 회원가입 `kim` / `4321` → 로그인 `kim` / `4321` | 가입 완료 → 로그인 성공, members.txt에 `kim,4321` 추가 (안 보이면 우클릭 → Synchronize) |
| 11 | 화면에서 **파랑** → 설정 >> 펜 색상 | 대화상자가 **파랑** 선택된 채로 열림 |
| 12 | 대화상자에서 **초록** → 확인 / 다시 열어 빨강 → **취소** | 화면이 초록으로 바뀜 / 취소하면 그대로 초록 |
| 13 | 화면에서 **굵은 선** 체크 → 설정 >> 펜 옵션 → **채우기**도 체크 → 확인 | 대화상자는 굵은 선 체크된 채로 열리고, 확인 후 화면 체크박스 둘 다 체크 |
| 14 | 회색 영역 드래그 2번 | 손을 따라 원이 커지고, 떼면 남음 (초록·굵게·채움) |
| 15 | 회색 영역 **짧게 탭** | "그림 메뉴": 마지막 원 지우기 / 모두 지우기 / 펜 색상 선택 |
| 16 | 마지막 원 지우기 → 펜 색상 선택(→파랑) → 모두 지우기 | 원 1개 남음 → 화면 라디오도 파랑 → 전부 지워짐 |
| 17 | 폰 설정 → 디스플레이 → 다크 모드 켜고 ⋮ | 다크 모드에서도 메뉴가 보임 |

처음부터 다시 테스트하려면: 앱 아이콘 길게 누르기 → 앱 정보 → 저장공간 → **데이터 삭제**.

---

### 시간이 남으면 해 볼 변형 (시험에서 바뀔 수 있는 부분)
- 계정을 파일 대신 **배열** `String[] ids = {"user1","user2","user3"};`로 → `findPw()` 안만 바꾸면 됨 ([SNIPPETS.md 8번](SNIPPETS.md#8-로그인-계정-3개--일치-여부-메시지창))
- **미리 저장된 에디트텍스트** 값과 비교 → 화면에 EditText 두 개 추가, `findPw()` 대신 `getText().toString()` 비교
- 그림을 원 대신 **선/사각형**으로 → `drawCircle` 대신 `canvas.drawLine` / `drawRect` ([SNIPPETS.md 11번](SNIPPETS.md#11-그래픽-터치로-그리는-그림판))
- 메뉴를 XML 대신 **Java `menu.add`**로 → [SNIPPETS.md 2-2](SNIPPETS.md#2-2-java-코드만으로-xml-없이-숫자-id라서-switch-가능)
- 로그인 성공 시 **토스트를 커스텀 레이아웃**으로 (교재 실습 7-3 toast1.xml)

### 막혔을 때
| 증상 | 원인 |
|---|---|
| ⋮가 없음 | 2번 테마 (values-night 포함) |
| `R.id.main` 빌드 에러 | MainActivity를 통째로 바꾸지 않아서 새 프로젝트의 EdgeToEdge 코드가 남아 있음 |
| `package` 관련 에러 | MainActivity 맨 위 `package com.example.프로젝트이름;` 줄까지 지웠음 → 다시 써 넣기 |
| `constant expression required` | `switch`에 `R.id.xxx` 사용 → if 문 |
| 로그인 대화상자에서 종료 | `findViewById` 대신 `dialogView.findViewById` / dialog_login.xml의 id 오타 |
| 로그인이 계속 실패 | 한글 입력 / `==` 대신 `equals` |
| 가입한 회원이 사라짐 | `MODE_APPEND` 대신 `MODE_PRIVATE` |
| 탭해도 메뉴 없음 | onCreate의 `registerForContextMenu(graphicView)` 빠짐 |
| 처음부터 다시 테스트하고 싶음 | 에뮬레이터에서 앱 아이콘 길게 누르기 → 앱 정보 → 저장공간 → 데이터 삭제 |
