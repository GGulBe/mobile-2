package com.example.practice;

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
