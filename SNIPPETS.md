# 시험장 복사용 코드 모음 (7장 메뉴·대화상자 / 8장 파일 / 9장 그래픽)

시험 형식: **약 2시간, AI 사용 금지, 깃허브·PDF 열람 가능**

- 모든 코드는 **최신 Android Studio(이 저장소와 같은 설정)에서 컴파일되는 것을 확인**했습니다.
- `R.id.xxx`를 비교할 때는 **switch가 아니라 if 문**을 씁니다. switch로 쓰면 "constant expression required" 에러가 납니다.
- 전체 앱 예시는 [`midterm/`](midterm/) 폴더(그림판 + 로그인 + 회원가입 + 메뉴 + 대화상자)를 보세요.

## 목차
0. [새 프로젝트 만들자마자 할 일](#0-새-프로젝트-만들자마자-할-일) ← **시험 시작하면 제일 먼저**
1. [import 모음](#1-import-모음)
2. [옵션 메뉴](#2-옵션-메뉴)
3. [컨텍스트 메뉴 (길게 누르기)](#3-컨텍스트-메뉴-길게-누르기)
4. [터치 이벤트로 메뉴 띄우기](#4-터치-이벤트로-메뉴-띄우기)
5. [대화상자 (메시지창, 목록, 라디오, 체크박스)](#5-대화상자)
6. [사용자 정의 대화상자 (dialog1.xml) + 값 주고받기](#6-사용자-정의-대화상자-dialog1xml--값-주고받기)
7. [라디오버튼·체크박스 ↔ 대화상자 주고받기](#7-라디오버튼체크박스--대화상자-주고받기)
8. [로그인: 계정 3개 + 일치 여부 메시지창](#8-로그인-계정-3개--일치-여부-메시지창) ← **작년 기출 유형**
9. [파일로 회원가입·로그인 (전체 코드)](#9-파일로-회원가입로그인-전체-코드) ← **작년 기출 유형**
10. [파일 처리 기본 (내장 메모리, raw, SD 카드)](#10-파일-처리-기본)
11. [그래픽: 터치로 그리는 그림판](#11-그래픽-터치로-그리는-그림판)
12. [이미지: 비트맵, 회전·확대, 효과](#12-이미지-비트맵-회전확대-효과)
13. [토스트](#13-토스트)
14. [에러 날 때](#14-에러-날-때)

---

## 0. 새 프로젝트 만들자마자 할 일

**New Project → Empty Views Activity → Language: Java**

### ① 테마 바꾸기 (안 하면 ⋮ 옵션 메뉴가 안 보임)
`res/values/themes.xml` **그리고** `res/values-night/themes.xml` 둘 다
```xml
parent="Theme.Material3.DayNight.NoActionBar"
```
를 아래처럼 바꿉니다. `values-night`를 빼먹으면 다크 모드에서만 메뉴가 사라집니다.
```xml
parent="Theme.Material3.DayNight"
```

### ② MainActivity의 EdgeToEdge 코드 지우기
새 프로젝트의 `onCreate()`에는 아래 코드가 들어 있습니다. activity_main.xml을 교재처럼 새로 짜면 `R.id.main`이 없어져서 **에러가 나거나 앱이 바로 종료**됩니다. 통째로 지우세요.
```java
EdgeToEdge.enable(this);                       // ← 지우기
ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {   // ← 여기부터
    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
    return insets;
});                                            // ← 여기까지 지우기
```
그리고 activity_main.xml 바깥 레이아웃에 `android:fitsSystemWindows="true"`를 넣으면 화면 위쪽이 상태바에 가려지지 않습니다.

### ③ 새 리소스 파일 만들기
- 메뉴: `res` 우클릭 → New → Android Resource File → Resource type: **Menu** → 이름 `menu1`
- 대화상자 레이아웃: `res/layout` 우클릭 → New → Layout Resource File → 이름 `dialog1`, Root element `LinearLayout`

---

## 1. import 모음
MainActivity 맨 위 `package ...;` 바로 아래에 통째로 붙여 넣으세요. 안 쓰는 import는 회색으로만 표시되고 에러가 나지 않습니다.

<!-- check:imports -->
```java
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
```

---

## 2. 옵션 메뉴

### 2-1. 메뉴 XML 방식 (`res/menu/menu1.xml`)
```xml
<menu xmlns:android="http://schemas.android.com/apk/res/android" >
    <item android:id="@+id/itemRed"   android:title="배경색 (빨강)" />
    <item android:id="@+id/itemGreen" android:title="배경색 (초록)" />
    <item android:id="@+id/itemBlue"  android:title="배경색 (파랑)" />
    <item android:title="버튼 변경 >>">
        <menu>
            <item android:id="@+id/subRotate" android:title="버튼 45도 회전" />
            <item android:id="@+id/subSize"   android:title="버튼 2배 확대" />
        </menu>
    </item>
</menu>
```
라디오처럼 하나만 고르는 메뉴는 `<group android:checkableBehavior="single">`로 item을 감싸고, Java에서 `item.setChecked(true);`를 호출합니다.

MainActivity (onCreate와 같은 높이, 즉 클래스 안에 붙여 넣기):
<!-- check:menuxml:member -->
```java
    LinearLayout baseLayout;   // onCreate에서 findViewById로 연결
    Button button1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.menu1, menu);
        return true;                                  // true여야 메뉴가 보임
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();                    // switch 대신 if 문!
        if (id == R.id.itemRed) {
            baseLayout.setBackgroundColor(Color.RED);
        } else if (id == R.id.itemGreen) {
            baseLayout.setBackgroundColor(Color.GREEN);
        } else if (id == R.id.itemBlue) {
            baseLayout.setBackgroundColor(Color.BLUE);
        } else if (id == R.id.subRotate) {
            button1.setRotation(45);
        } else if (id == R.id.subSize) {
            button1.setScaleX(2);
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
```

### 2-2. Java 코드만으로 (XML 없이, 숫자 ID라서 switch 가능)
<!-- check:menujava:member -->
```java
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, "항목 1");                  // add(그룹, 항목ID, 순서, 제목)
        menu.add(0, 2, 0, "항목 2");
        SubMenu sMenu = menu.addSubMenu("더보기 >>");
        sMenu.add(0, 3, 0, "항목 3");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Toast.makeText(getApplicationContext(), "항목 1", Toast.LENGTH_SHORT).show();
                return true;
            case 2:
                Toast.makeText(getApplicationContext(), "항목 2", Toast.LENGTH_SHORT).show();
                return true;
            case 3:
                Toast.makeText(getApplicationContext(), "항목 3", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
```

---

## 3. 컨텍스트 메뉴 (길게 누르기)

onCreate 안:
<!-- check:context:oncreate -->
```java
        button1 = (Button) findViewById(R.id.button1);
        registerForContextMenu(button1);              // 이거 빠지면 메뉴 안 뜸
        button2 = (Button) findViewById(R.id.button2);
        registerForContextMenu(button2);
```
클래스 안:
<!-- check:context:member -->
```java
    Button button1, button2;
    LinearLayout baseLayout;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v == button1) {                           // 어떤 위젯을 눌렀는지 구분
            menu.setHeaderTitle("배경색 변경");
            menu.add(0, 1, 0, "빨강");
            menu.add(0, 2, 0, "파랑");
        }
        if (v == button2) {
            menu.add(0, 3, 0, "버튼 45도 회전");
        }
        // XML로 하려면: getMenuInflater().inflate(R.menu.menu2, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1: baseLayout.setBackgroundColor(Color.RED);  return true;
            case 2: baseLayout.setBackgroundColor(Color.BLUE); return true;
            case 3: button2.setRotation(45);                   return true;
        }
        return super.onContextItemSelected(item);
    }
```

---

## 4. 터치 이벤트로 메뉴 띄우기

**전체 MainActivity**입니다. activity_main.xml 없이 동작합니다. 드래그하면 그림을 그리고, 짧게 탭하면 컨텍스트 메뉴가 뜨고, ⋮을 누르면 옵션 메뉴가 뜹니다.
<!-- check:touchmenu:file -->
```java
public class MainActivity extends AppCompatActivity {
    final static int LINE = 1, CIRCLE = 2;
    static int curShape = LINE;
    static int curColor = Color.RED;
    MyGraphicView graphicView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        graphicView = new MyGraphicView(this);
        setContentView(graphicView);
        setTitle("터치 + 메뉴");
        registerForContextMenu(graphicView);
    }

    // 옵션 메뉴 : 도형 선택
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, "선 그리기");
        menu.add(0, 2, 0, "원 그리기");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1: curShape = LINE;   return true;
            case 2: curShape = CIRCLE; return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 컨텍스트 메뉴 : 색상 선택 (터치로 띄움)
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("색상 변경");
        menu.add(0, 11, 0, "빨강");
        menu.add(0, 12, 0, "파랑");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 11: curColor = Color.RED;  return true;
            case 12: curColor = Color.BLUE; return true;
        }
        return super.onContextItemSelected(item);
    }

    private static class MyGraphicView extends View {
        int startX = -1, startY = -1, stopX = -1, stopY = -1;

        public MyGraphicView(Context context) {
            super(context);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = (int) event.getX();
                    startY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    stopX = (int) event.getX();
                    stopY = (int) event.getY();
                    this.invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    stopX = (int) event.getX();
                    stopY = (int) event.getY();
                    // 거의 안 움직였으면 = 탭 → 메뉴 띄우기
                    if (Math.abs(stopX - startX) < 10 && Math.abs(stopY - startY) < 10)
                        this.showContextMenu();
                    this.invalidate();
                    break;
            }
            return true;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(curColor);
            if (curShape == LINE) {
                canvas.drawLine(startX, startY, stopX, stopY, paint);
            } else if (curShape == CIRCLE) {
                int radius = (int) Math.sqrt(Math.pow(stopX - startX, 2) + Math.pow(stopY - startY, 2));
                canvas.drawCircle(startX, startY, radius, paint);
            }
        }
    }
}
```
- **터치로 옵션 메뉴(⋮)를 여는 `openOptionsMenu()`는 이 환경에서 동작하지 않습니다**(직접 확인함). 터치로 띄울 메뉴는 위처럼 **컨텍스트 메뉴**로 만들고, 옵션 메뉴와 같은 항목이 필요하면 `onCreateContextMenu`에도 똑같이 `menu.add(...)` 하세요.
- **길게 눌렀을 때만 메뉴를 띄우려면** 탭 판정 코드를 지우고, `return true;` 바로 앞에 `super.onTouchEvent(event);`를 넣고, 생성자에 `setLongClickable(true);`를 넣습니다.

---

## 5. 대화상자

### 5-1. 메시지창 (확인만) — 결과 알려 줄 때
<!-- check:dialogs:member -->
```java
    // 사용: showMessage("로그인 성공", "아이디와 비밀번호가 일치합니다");
    void showMessage(String title, String msg) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle(title);
        dlg.setMessage(msg);
        dlg.setIcon(R.mipmap.ic_launcher);
        dlg.setPositiveButton("확인", null);         // null = 그냥 닫기
        dlg.show();                                   // show() 빠뜨리면 안 뜸
    }
```

### 5-2. 확인 / 취소 버튼에 동작 넣기
<!-- check:dialogs:member -->
```java
    void showConfirm() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("확인");
        dlg.setMessage("정말 할까요?");
        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "확인을 눌렀네요", Toast.LENGTH_SHORT).show();
            }
        });
        dlg.setNegativeButton("취소", null);
        dlg.show();
    }
```

### 5-3. 목록 3종 (기본 / 라디오 / 체크박스)
<!-- check:dialogs:member -->
```java
    void showListDialog(final Button button1) {
        final String[] versionArray = new String[] { "Q(10)", "R(11)", "S(12)" };
        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("좋아하는 버전은?");
        // ① 기본 목록 (누르면 바로 닫힘)
        dlg.setItems(versionArray, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                button1.setText(versionArray[which]);
            }
        });
        dlg.setPositiveButton("닫기", null);
        dlg.show();
    }

    void showRadioDialog(final Button button1) {
        final String[] versionArray = new String[] { "Q(10)", "R(11)", "S(12)" };
        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("좋아하는 버전은?");
        // ② 라디오버튼 : (배열, 처음 선택 번호, 리스너)
        dlg.setSingleChoiceItems(versionArray, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                button1.setText(versionArray[which]);
            }
        });
        dlg.setPositiveButton("닫기", null);
        dlg.show();
    }

    void showCheckDialog(final Button button1) {
        final String[] versionArray = new String[] { "Q(10)", "R(11)", "S(12)" };
        final boolean[] checkArray = new boolean[] { true, false, false };
        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("좋아하는 버전은?");
        // ③ 체크박스 : 리스너가 OnMultiChoiceClickListener, 파라미터 3개
        dlg.setMultiChoiceItems(versionArray, checkArray, new DialogInterface.OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkArray[which] = isChecked;
            }
        });
        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String str = "";
                for (int i = 0; i < versionArray.length; i++)
                    if (checkArray[i]) str += versionArray[i] + " ";
                button1.setText(str);
            }
        });
        dlg.show();
    }
```

---

## 6. 사용자 정의 대화상자 (dialog1.xml) + 값 주고받기

`res/layout/dialog1.xml`:
```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp"
    android:orientation="vertical" >

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="사용자 이름"
        android:textSize="20dp" />

    <EditText
        android:id="@+id/dlgEdt1"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="이메일"
        android:textSize="20dp" />

    <EditText
        android:id="@+id/dlgEdt2"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
</LinearLayout>
```
MainActivity (메인 화면에 `tvName`, `tvEmail` TextView가 있다고 가정):
<!-- check:custom:member -->
```java
    TextView tvName, tvEmail;
    EditText dlgEdtName, dlgEdtEmail;
    View dialogView;

    void showUserDialog() {
        dialogView = (View) View.inflate(MainActivity.this, R.layout.dialog1, null);
        // ★ 대화상자 안 위젯은 dialogView.findViewById 로 찾기 (그냥 findViewById면 null → 앱 종료)
        dlgEdtName = (EditText) dialogView.findViewById(R.id.dlgEdt1);
        dlgEdtEmail = (EditText) dialogView.findViewById(R.id.dlgEdt2);

        // 메인 화면 → 대화상자
        dlgEdtName.setText(tvName.getText().toString());
        dlgEdtEmail.setText(tvEmail.getText().toString());

        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("사용자 정보 입력");
        dlg.setView(dialogView);
        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // 대화상자 → 메인 화면
                tvName.setText(dlgEdtName.getText().toString());
                tvEmail.setText(dlgEdtEmail.getText().toString());
            }
        });
        dlg.setNegativeButton("취소", null);
        dlg.show();
    }
```

---

## 7. 라디오버튼·체크박스 ↔ 대화상자 주고받기

activity_main.xml에 넣을 위젯:
```xml
    <RadioGroup
        android:id="@+id/rgColor"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal" >
        <RadioButton android:id="@+id/rdoRed"   android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="빨강" android:checked="true" />
        <RadioButton android:id="@+id/rdoGreen" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="초록" />
        <RadioButton android:id="@+id/rdoBlue"  android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="파랑" />
    </RadioGroup>

    <CheckBox android:id="@+id/chkFill" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="채우기" />
    <CheckBox android:id="@+id/chkBold" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="굵은 선" />
```
onCreate 안:
<!-- check:radio:oncreate -->
```java
        rgColor = (RadioGroup) findViewById(R.id.rgColor);
        rdoColor[0] = (RadioButton) findViewById(R.id.rdoRed);
        rdoColor[1] = (RadioButton) findViewById(R.id.rdoGreen);
        rdoColor[2] = (RadioButton) findViewById(R.id.rdoBlue);
        chkFill = (CheckBox) findViewById(R.id.chkFill);
        chkBold = (CheckBox) findViewById(R.id.chkBold);

        // 라디오버튼을 바꿀 때마다 할 일
        rgColor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rdoRed) Toast.makeText(getApplicationContext(), "빨강", Toast.LENGTH_SHORT).show();
            }
        });
        // 체크박스를 바꿀 때마다 할 일
        chkFill.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(getApplicationContext(), "채우기 " + isChecked, Toast.LENGTH_SHORT).show();
            }
        });
```
클래스 안:
<!-- check:radio:member -->
```java
    RadioGroup rgColor;
    RadioButton[] rdoColor = new RadioButton[3];
    CheckBox chkFill, chkBold;
    final String[] colorNames = { "빨강", "초록", "파랑" };

    // 화면 라디오버튼 중 몇 번째가 선택됐는지 (0~2)
    int getColorIndex() {
        int checkedId = rgColor.getCheckedRadioButtonId();
        for (int i = 0; i < rdoColor.length; i++)
            if (rdoColor[i].getId() == checkedId) return i;
        return 0;
    }

    // 라디오 대화상자 ↔ 화면 라디오버튼
    void showColorDialog() {
        final int[] selected = { getColorIndex() };          // 화면 → 대화상자 처음 선택값
        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("색상 선택");
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

    // 체크박스 대화상자 ↔ 화면 체크박스
    void showSettingDialog() {
        final String[] optArray = { "채우기", "굵은 선" };
        final boolean[] checkArray = { chkFill.isChecked(), chkBold.isChecked() };   // 화면 → 대화상자
        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("설정");
        dlg.setMultiChoiceItems(optArray, checkArray, new DialogInterface.OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkArray[which] = isChecked;
            }
        });
        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                chkFill.setChecked(checkArray[0]);           // 대화상자 → 화면
                chkBold.setChecked(checkArray[1]);
            }
        });
        dlg.setNegativeButton("취소", null);
        dlg.show();
    }

    // 정답 확인 (데이터가 맞는지 틀린지 메시지창)
    void checkAnswer() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("채점");
        if (getColorIndex() == 1 && chkFill.isChecked())
            dlg.setMessage("정답입니다! (초록 + 채우기)");
        else
            dlg.setMessage("틀렸습니다");
        dlg.setPositiveButton("확인", null);
        dlg.show();
    }
```
- 메뉴 항목에서 화면 라디오버튼을 바꾸려면 `rdoColor[2].setChecked(true);`
- XML 라디오버튼을 id로 비교하려면 `if (rgColor.getCheckedRadioButtonId() == R.id.rdoBlue)`

---

## 8. 로그인: 계정 3개 + 일치 여부 메시지창

`res/layout/dialog_login.xml` (아이디 EditText에 `textVisiblePassword`를 주면 한글로 입력되는 실수를 막을 수 있음):
```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp"
    android:orientation="vertical" >

    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="아이디" android:textSize="20dp" />
    <EditText
        android:id="@+id/dlgId"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="textVisiblePassword" />

    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="비밀번호" android:textSize="20dp" />
    <EditText
        android:id="@+id/dlgPw"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="textPassword" />
</LinearLayout>
```
클래스 안 (메인 화면에 `tvUser` TextView가 있다고 가정):
<!-- check:login3:member -->
```java
    // 미리 만들어 둔 계정 3개
    final String[] ids = { "user1", "user2", "user3" };
    final String[] pws = { "1111", "2222", "3333" };
    TextView tvUser;
    View dialogView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, "로그인");
        menu.add(0, 2, 0, "로그아웃");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1: showLoginDialog(); return true;
            case 2: tvUser.setText("로그인 전"); return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void showLoginDialog() {
        dialogView = (View) View.inflate(MainActivity.this, R.layout.dialog_login, null);
        final EditText dlgId = (EditText) dialogView.findViewById(R.id.dlgId);
        final EditText dlgPw = (EditText) dialogView.findViewById(R.id.dlgPw);

        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("로그인");
        dlg.setView(dialogView);
        dlg.setPositiveButton("로그인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String id = dlgId.getText().toString().trim();
                String pw = dlgPw.getText().toString().trim();

                String result = "없는 아이디입니다";
                for (int i = 0; i < ids.length; i++) {
                    if (ids[i].equals(id)) {                  // 문자열 비교는 equals!
                        if (pws[i].equals(pw)) {
                            result = "아이디와 비밀번호가 일치합니다";
                            tvUser.setText(id + " 님 로그인됨");
                        } else {
                            result = "비밀번호가 일치하지 않습니다";
                        }
                    }
                }
                // 결과를 메시지창으로
                AlertDialog.Builder msg = new AlertDialog.Builder(MainActivity.this);
                msg.setTitle("로그인 결과");
                msg.setMessage(result);
                msg.setPositiveButton("확인", null);
                msg.show();
            }
        });
        dlg.setNegativeButton("취소", null);
        dlg.show();
    }
```
- **"미리 저장된 에디트텍스트 값과 비교"** 문제라면: 메인 화면에 EditText `edtSavedId`, `edtSavedPw`를 두고 `ids[i]` 대신 `edtSavedId.getText().toString()`과 비교하면 됩니다.

---

## 9. 파일로 회원가입·로그인 (전체 코드)

회원 정보를 내장 메모리 파일 `members.txt`에 `아이디,비밀번호` 형식으로 한 줄씩 저장합니다. 처음 실행하면 계정 3개를 파일에 미리 만들어 둡니다. 레이아웃은 8번의 `dialog_login.xml`을 그대로 씁니다. activity_main.xml은 없어도 됩니다.
<!-- check:filelogin:file -->
```java
public class MainActivity extends AppCompatActivity {
    final String FILE_NAME = "members.txt";
    TextView tvStatus;
    View dialogView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvStatus = new TextView(this);
        tvStatus.setTextSize(20);
        tvStatus.setText("로그인 전");
        setContentView(tvStatus);
        setTitle("회원가입 / 로그인");

        // 처음 실행이면 계정 3개를 파일에 미리 만들어 둠
        if (readMembers().equals("")) {
            writeMember("user1", "1111");
            writeMember("user2", "2222");
            writeMember("user3", "3333");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, "회원가입");
        menu.add(0, 2, 0, "로그인");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1: showDialog(true);  return true;
            case 2: showDialog(false); return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // isJoin == true 이면 회원가입, false 이면 로그인
    void showDialog(final boolean isJoin) {
        dialogView = (View) View.inflate(MainActivity.this, R.layout.dialog_login, null);
        final EditText dlgId = (EditText) dialogView.findViewById(R.id.dlgId);
        final EditText dlgPw = (EditText) dialogView.findViewById(R.id.dlgPw);

        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle(isJoin ? "회원가입" : "로그인");
        dlg.setView(dialogView);
        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String id = dlgId.getText().toString().trim();
                String pw = dlgPw.getText().toString().trim();
                if (id.equals("") || pw.equals("")) {
                    showMessage("입력 오류", "아이디와 비밀번호를 입력하세요");
                    return;
                }
                if (isJoin) {
                    if (findPw(id) != null) {
                        showMessage("회원가입 실패", "이미 있는 아이디입니다");
                    } else {
                        writeMember(id, pw);
                        showMessage("회원가입 완료", id + " 님이 가입되었습니다");
                    }
                } else {
                    String savedPw = findPw(id);
                    if (savedPw == null) {
                        showMessage("로그인 실패", "없는 아이디입니다");
                    } else if (!savedPw.equals(pw)) {
                        showMessage("로그인 실패", "비밀번호가 틀렸습니다");
                    } else {
                        tvStatus.setText(id + " 님 환영합니다");
                        showMessage("로그인 성공", "아이디와 비밀번호가 일치합니다");
                    }
                }
            }
        });
        dlg.setNegativeButton("취소", null);
        dlg.show();
    }

    void showMessage(String title, String msg) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle(title);
        dlg.setMessage(msg);
        dlg.setPositiveButton("확인", null);
        dlg.show();
    }

    // 파일 끝에 "아이디,비밀번호" 한 줄 추가 (MODE_APPEND = 이어쓰기, MODE_PRIVATE = 덮어쓰기)
    void writeMember(String id, String pw) {
        try {
            FileOutputStream outFs = openFileOutput(FILE_NAME, Context.MODE_APPEND);
            String str = id + "," + pw + "\n";
            outFs.write(str.getBytes());
            outFs.close();
        } catch (IOException e) {
        }
    }

    // 파일 전체 읽기 (없으면 빈 문자열)
    String readMembers() {
        try {
            FileInputStream inFs = openFileInput(FILE_NAME);
            byte[] txt = new byte[inFs.available()];
            inFs.read(txt);
            inFs.close();
            return new String(txt);
        } catch (IOException e) {
            return "";
        }
    }

    // 아이디로 저장된 비밀번호 찾기 (없으면 null)
    String findPw(String id) {
        String[] lines = readMembers().split("\n");
        for (int i = 0; i < lines.length; i++) {
            String[] data = lines[i].split(",");
            if (data.length == 2 && data[0].equals(id))
                return data[1];
        }
        return null;
    }
}
```
저장된 파일 확인: View → Tool Windows → **Device Explorer** → `data/data/패키지명/files/members.txt`

---

## 10. 파일 처리 기본

<!-- check:files:member -->
```java
    // 내장 메모리에 쓰기 (/data/data/패키지명/files)
    void writeFile(String fName, String str) {
        try {
            FileOutputStream outFs = openFileOutput(fName, Context.MODE_PRIVATE);
            outFs.write(str.getBytes());
            outFs.close();
            Toast.makeText(getApplicationContext(), fName + " 저장됨", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
        }
    }

    // 내장 메모리에서 읽기 (없으면 null)
    String readFile(String fName) {
        try {
            FileInputStream inFs = openFileInput(fName);
            byte[] txt = new byte[inFs.available()];
            inFs.read(txt);
            inFs.close();
            return (new String(txt)).trim();
        } catch (IOException e) {
            return null;
        }
    }

    // /res/raw/raw_test.txt 읽기 (읽기 전용, res 우클릭 → New → Android Resource Directory → raw)
    String readRaw() {
        try {
            InputStream inputS = getResources().openRawResource(R.raw.raw_test);
            byte[] txt = new byte[inputS.available()];
            inputS.read(txt);
            inputS.close();
            return new String(txt);
        } catch (IOException e) {
            return null;
        }
    }

    // 날짜 → 파일명 "2026_10_28.txt" (월은 0부터라 +1)
    String dateFileName(DatePicker dp) {
        return dp.getYear() + "_" + (dp.getMonth() + 1) + "_" + dp.getDayOfMonth() + ".txt";
    }

    // 폴더 안 파일 목록 (SD 카드 Pictures)
    String listPictures() {
        File[] files = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/Pictures").listFiles();
        if (files == null) return "목록 없음 (권한 확인)";
        String str = "";
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) str += "<폴더> " + files[i].getName() + "\n";
            else str += "<파일> " + files[i].getName() + "\n";
        }
        return str;
    }
```
**SD 카드**를 쓸 때만 추가로:
- `AndroidManifest.xml`: `<application` 위에 `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>`, `<application` 안에 `android:requestLegacyExternalStorage="true"`
- `app/build.gradle.kts`: `targetSdk = 29`
- onCreate에서 `ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);`

---

## 11. 그래픽: 터치로 그리는 그림판
4번의 전체 코드가 그대로 그림판입니다(선/원 + 메뉴). 도형을 추가하는 부분은 `onDraw` 안에 이렇게 넣습니다.
<!-- check:draw:ondraw -->
```java
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);      // 채우기는 Paint.Style.FILL
            paint.setColor(Color.RED);               // 반투명: Color.argb(0x88, 0xff, 0, 0)
            int startX = 100, startY = 100, stopX = 400, stopY = 400;

            canvas.drawLine(startX, startY, stopX, stopY, paint);                         // 선
            canvas.drawRect(new Rect(startX, startY, stopX, stopY), paint);              // 사각형
            canvas.drawCircle(startX, startY, 100, paint);                               // 원
            canvas.drawOval(new RectF(startX, startY, stopX, stopY), paint);             // 타원
            canvas.drawRoundRect(new RectF(startX, startY, stopX, stopY), 40, 40, paint); // 둥근 사각형
            paint.setTextSize(60);
            canvas.drawText("안드로이드", startX, startY, paint);                           // 글자
```
- 그린 도형을 계속 남기려면 도형 정보를 `ArrayList`에 저장해 두고 `onDraw`에서 전부 다시 그립니다. [`midterm/`](midterm/)의 `MyShape`를 참고하세요.
- 레이아웃 안에 그림판을 넣으려면 `LinearLayout pictureLayout = (LinearLayout) findViewById(R.id.pictureLayout); pictureLayout.addView(graphicView);`

---

## 12. 이미지: 비트맵, 회전·확대, 효과
`onDraw` 안 (`/res/drawable`에 그림 파일 복사, 이름은 영어 소문자):
<!-- check:image:ondraw -->
```java
            Bitmap picture = BitmapFactory.decodeResource(getResources(), R.drawable.jeju14);
            // SD 카드 그림: BitmapFactory.decodeFile("/sdcard/Pictures/a.jpg");
            int cenX = this.getWidth() / 2;
            int cenY = this.getHeight() / 2;
            int picX = (this.getWidth() - picture.getWidth()) / 2;     // 가운데 정렬
            int picY = (this.getHeight() - picture.getHeight()) / 2;

            canvas.scale(1.5f, 1.5f, cenX, cenY);     // 확대/축소 (그리기 전에)
            canvas.rotate(45, cenX, cenY);            // 회전

            Paint paint = new Paint();
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0);                      // 0 = 흑백, 1 = 원래 색
            paint.setColorFilter(new ColorMatrixColorFilter(cm));
            // 블러: paint.setMaskFilter(new BlurMaskFilter(30, BlurMaskFilter.Blur.NORMAL));
            // 엠보싱: paint.setMaskFilter(new EmbossMaskFilter(new float[] {3, 3, 3}, 0.5f, 5, 10));

            canvas.drawBitmap(picture, picX, picY, paint);
            picture.recycle();
```
- 버튼을 눌러 값을 바꾸면 `graphicView.invalidate();`를 호출해야 다시 그려집니다.
- 블러나 엠보싱이 안 보이면 `AndroidManifest.xml`의 `<application` 안에 `android:hardwareAccelerated="false"`를 넣습니다.

---

## 13. 토스트
<!-- check:toast:member -->
```java
    void toastExamples() {
        Toast.makeText(getApplicationContext(), "메시지", Toast.LENGTH_SHORT).show();   // .show() 필수

        // 위치 지정
        Toast t = Toast.makeText(MainActivity.this, "위쪽에 표시", Toast.LENGTH_LONG);
        t.setGravity(Gravity.TOP | Gravity.LEFT, 100, 200);
        t.show();
    }
```
리스너 안에서는 `this` 대신 `MainActivity.this` 또는 `getApplicationContext()`를 씁니다.

---

## 14. 에러 날 때
| 증상 | 해결 |
|---|---|
| `constant expression required` | `switch (item.getItemId()) { case R.id.xxx:` → **if 문**으로 |
| 새 프로젝트 빌드 실패 `checkDebugAarMetadata` · `Dependency 'androidx.core:core:1.19.1' requires … compileSdk 37` | 새 프로젝트 라이브러리가 설치된 SDK보다 새것. `gradle/libs.versions.toml`에서 `coreKtx = "1.19.1"` → `coreKtx = "1.17.0"` 으로 바꾸고 Sync (또는 SDK Manager에서 Android 37 설치) |
| 실행하자마자 종료 · `ClassNotFoundException` · `import com.example….R;`이 자동으로 생김 | MainActivity 맨 위 `package com.example.프로젝트이름;` 줄이 지워졌거나 파일이 `java/com/example/프로젝트이름/` 폴더 밖에 있음 → package 줄 복구, 파일을 폴더 안으로 |
| ⋮ 메뉴가 안 보임 | `values/themes.xml`, `values-night/themes.xml`의 `NoActionBar` 지우기 (0번) |
| 앱이 켜지자마자 종료 | `R.id.main` EdgeToEdge 코드 지우기 (0번). 또는 findViewById한 id가 XML에 없는지 확인 |
| 대화상자 EditText에서 앱 종료 | `findViewById` → `dialogView.findViewById` |
| 대화상자/토스트가 안 뜸 | `.show()` 빠뜨림 |
| 컨텍스트 메뉴가 안 뜸 | `registerForContextMenu(뷰)` 빠뜨림 |
| 그림이 안 바뀜 | 값 바꾼 뒤 `invalidate()` |
| 로그인이 계속 실패 | 키보드가 한글이라 `user1` → `ㅕㄴㄷㄱ1`. 아이디 EditText에 `inputType="textVisiblePassword"` |
| 문자열 비교가 항상 false | `==` 대신 `.equals()` |
| 회원 파일에 쓸 때마다 기존 회원이 사라짐 | `MODE_PRIVATE` → `Context.MODE_APPEND` |
| 익명 리스너 안에서 지역변수 에러 | 변수를 `final`로, 또는 클래스 필드로 |
| `Cannot resolve symbol 'R'` (빌드는 됨) | File → Sync Project with Gradle Files |
| 에뮬레이터가 클릭에 반응 없음 | Device Manager → ⋮ → Cold Boot Now |
| 에뮬레이터 설치 실패 "not enough space" | Device Manager → ⋮ → Wipe Data |
| 학교 PC에서 Gradle 오류 | 프로젝트를 경로에 한글이 없는 곳(`C:\midterm`)에 복사해서 열기 |
