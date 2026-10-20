package com.example.midterm;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
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

    // ===== 미리 만들어 둔 계정 3개 =====
    final static String[] ids = { "user1", "user2", "user3" };
    final static String[] pws = { "1111", "2222", "3333" };

    final static int LINE = 1, CIRCLE = 2, RECTANGLE = 3;
    static int curShape = LINE;
    static int curColor = Color.RED;
    static boolean fill = false, bold = false;
    static String loginUser = null;

    TextView tvUser;
    CheckBox chkFill, chkBold;
    Button btnLogin;
    RadioGroup rgColor;
    RadioButton[] rdoColor = new RadioButton[3];
    final static String[] colorNames = { "빨강", "초록", "파랑" };
    final static int[] colors = { Color.RED, Color.GREEN, Color.BLUE };
    View dialogView;
    MyGraphicView graphicView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("간단 그림판 + 로그인");

        tvUser = (TextView) findViewById(R.id.tvUser);
        chkFill = (CheckBox) findViewById(R.id.chkFill);
        chkBold = (CheckBox) findViewById(R.id.chkBold);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        rgColor = (RadioGroup) findViewById(R.id.rgColor);
        rdoColor[0] = (RadioButton) findViewById(R.id.rdoRed);
        rdoColor[1] = (RadioButton) findViewById(R.id.rdoGreen);
        rdoColor[2] = (RadioButton) findViewById(R.id.rdoBlue);

        LinearLayout pictureLayout = (LinearLayout) findViewById(R.id.pictureLayout);
        graphicView = new MyGraphicView(this);
        pictureLayout.addView(graphicView);
        registerForContextMenu(graphicView); // 탭하면 컨텍스트 메뉴

        chkFill.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fill = isChecked;
            }
        });
        chkBold.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                bold = isChecked;
            }
        });

        // 화면의 라디오버튼 = 현재 색상 (메뉴·대화상자도 이 라디오버튼을 바꿈)
        rgColor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                curColor = colors[getColorIndex()];
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showLoginDialog();
            }
        });

        showLoginDialog();
    }

    // ===== 대화상자 1: 사용자 정의 뷰(로그인) + 파일 읽기/쓰기 =====
    void showLoginDialog() {
        dialogView = (View) View.inflate(MainActivity.this, R.layout.dialog_login, null);
        final EditText dlgId = (EditText) dialogView.findViewById(R.id.dlgId);
        final EditText dlgPw = (EditText) dialogView.findViewById(R.id.dlgPw);
        final CheckBox dlgChkSave = (CheckBox) dialogView.findViewById(R.id.dlgChkSave);

        String savedId = readFile("saved_id.txt");
        if (savedId != null) {
            dlgId.setText(savedId);
            dlgChkSave.setChecked(true);
        }

        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("로그인");
        dlg.setIcon(R.drawable.ic_menu_allfriends);
        dlg.setView(dialogView);
        dlg.setPositiveButton("로그인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String id = dlgId.getText().toString().trim();
                String pw = dlgPw.getText().toString().trim();

                // 저장된 데이터와 일치하는지 확인 → 결과를 메시지창으로
                String realPw = findPw(id);
                if (realPw == null) {
                    showMessage("로그인 실패", "없는 아이디입니다: " + id + "\n(한/영 확인)", reopenLogin);
                } else if (!realPw.equals(pw)) {
                    showMessage("로그인 실패", "비밀번호가 일치하지 않습니다", reopenLogin);
                } else {
                    loginUser = id;
                    tvUser.setText(id + " 님 로그인됨");
                    btnLogin.setText("다시 로그인");
                    if (dlgChkSave.isChecked())
                        writeFile("saved_id.txt", id, Context.MODE_PRIVATE);
                    else
                        deleteFile("saved_id.txt");
                    showMessage("로그인 성공", id + " 님, 아이디와 비밀번호가 일치합니다", null);
                }
            }
        });
        dlg.setNeutralButton("회원가입", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                showSignupDialog();
            }
        });
        dlg.setNegativeButton("취소", null);
        dlg.show();
    }

    // 아이디의 비밀번호 찾기: 미리 만든 3개 계정 → 회원가입 파일(accounts.txt) 순서. 없으면 null
    String findPw(String id) {
        for (int i = 0; i < ids.length; i++)
            if (ids[i].equals(id)) return pws[i];

        String all = readFile("accounts.txt"); // 한 줄에 "아이디,비밀번호"
        if (all == null) return null;
        String[] lines = all.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String[] parts = lines[i].split(",");
            if (parts.length == 2 && parts[0].equals(id)) return parts[1];
        }
        return null;
    }

    // ===== 대화상자 1-2: 회원가입 (사용자 정의 뷰) + 파일에 추가 저장 =====
    void showSignupDialog() {
        dialogView = (View) View.inflate(MainActivity.this, R.layout.dialog_signup, null);
        final EditText sgnId = (EditText) dialogView.findViewById(R.id.sgnId);
        final EditText sgnPw = (EditText) dialogView.findViewById(R.id.sgnPw);
        final EditText sgnPw2 = (EditText) dialogView.findViewById(R.id.sgnPw2);

        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("회원가입");
        dlg.setIcon(R.drawable.ic_menu_allfriends);
        dlg.setView(dialogView);
        dlg.setPositiveButton("가입", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String id = sgnId.getText().toString().trim();
                String pw = sgnPw.getText().toString().trim();
                String pw2 = sgnPw2.getText().toString().trim();

                String msg = null;
                if (id.isEmpty() || pw.isEmpty())
                    msg = "아이디와 비밀번호를 입력하세요";
                else if (id.contains(",") || pw.contains(","))
                    msg = "쉼표(,)는 쓸 수 없어요";
                else if (!pw.equals(pw2))
                    msg = "비밀번호가 서로 다릅니다";
                else if (findPw(id) != null)
                    msg = "이미 있는 아이디입니다";

                if (msg != null) {
                    showMessage("회원가입 실패", msg, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            showSignupDialog();
                        }
                    });
                    return;
                }
                writeFile("accounts.txt", id + "," + pw + "\n", Context.MODE_APPEND);
                showMessage("회원가입 완료", id + " 님 가입 완료! 로그인하세요", reopenLogin);
            }
        });
        dlg.setNegativeButton("취소", null);
        dlg.show();
    }

    // 메시지창의 확인을 누르면 로그인 대화상자를 다시 띄움
    DialogInterface.OnClickListener reopenLogin = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            showLoginDialog();
        }
    };

    // ===== 메시지창: 확인 버튼만 있는 대화상자 (afterOk = 확인 누른 뒤 할 일, 없으면 null) =====
    void showMessage(String title, String msg, DialogInterface.OnClickListener afterOk) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle(title);
        dlg.setMessage(msg);
        dlg.setPositiveButton("확인", afterOk);
        dlg.show();
    }

    // ===== 파일 처리 (8장) =====
    // mode: Context.MODE_PRIVATE = 덮어쓰기, Context.MODE_APPEND = 뒤에 추가
    void writeFile(String fName, String str, int mode) {
        try {
            FileOutputStream outFs = openFileOutput(fName, mode);
            outFs.write(str.getBytes());
            outFs.close();
        } catch (IOException e) {
        }
    }

    String readFile(String fName) {
        try {
            FileInputStream inFs = openFileInput(fName);
            byte[] txt = new byte[inFs.available()]; // 파일 크기만큼
            inFs.read(txt);
            inFs.close();
            return (new String(txt)).trim();
        } catch (IOException e) {
            return null;
        }
    }

    // ===== 옵션 메뉴 (오른쪽 위 ⋮) =====
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.menu1, menu);
        return true;
    }

    // 주의: 최신 Android Studio는 switch(R.id.xxx)가 에러 → if 문 사용
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.itemLine) {
            curShape = LINE;
            item.setChecked(true);
        } else if (id == R.id.itemCircle) {
            curShape = CIRCLE;
            item.setChecked(true);
        } else if (id == R.id.itemRect) {
            curShape = RECTANGLE;
            item.setChecked(true);
        } else if (id == R.id.itemRed) {      // 메뉴 → 화면 라디오버튼
            rdoColor[0].setChecked(true);
        } else if (id == R.id.itemGreen) {
            rdoColor[1].setChecked(true);
        } else if (id == R.id.itemBlue) {
            rdoColor[2].setChecked(true);
        } else if (id == R.id.itemSetting) {
            showSettingDialog();
        } else if (id == R.id.itemSignup) {
            showSignupDialog();
        } else if (id == R.id.itemLogout) {
            showLogoutDialog();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    // ===== 대화상자 2: 체크박스 목록 ↔ 화면의 CheckBox 주고받기 =====
    void showSettingDialog() {
        final String[] optArray = new String[] { "채우기", "굵은 선" };
        final boolean[] checkArray = new boolean[] { chkFill.isChecked(), chkBold.isChecked() };

        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("그리기 설정");
        dlg.setMultiChoiceItems(optArray, checkArray, new DialogInterface.OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkArray[which] = isChecked;
            }
        });
        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                chkFill.setChecked(checkArray[0]);
                chkBold.setChecked(checkArray[1]);
            }
        });
        dlg.setNegativeButton("취소", null);
        dlg.show();
    }

    // 화면 라디오버튼 중 몇 번째가 선택됐는지 (0~2)
    int getColorIndex() {
        int checkedId = rgColor.getCheckedRadioButtonId();
        for (int i = 0; i < rdoColor.length; i++)
            if (rdoColor[i].getId() == checkedId) return i;
        return 0;
    }

    // ===== 대화상자 3: 라디오(단일 선택) ↔ 화면의 RadioGroup 주고받기 =====
    void showColorDialog() {
        final int[] selected = { getColorIndex() }; // 화면 라디오버튼 → 대화상자 처음 선택값

        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("색상 선택");
        dlg.setSingleChoiceItems(colorNames, selected[0], new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                selected[0] = which;
            }
        });
        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                rdoColor[selected[0]].setChecked(true); // 대화상자 선택값 → 화면 라디오버튼
            }
        });
        dlg.setNegativeButton("취소", null);
        dlg.show();
    }

    // ===== 대화상자 4: 확인/취소 =====
    void showLogoutDialog() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("로그아웃");
        dlg.setMessage(loginUser + " 님, 로그아웃 할까요?");
        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                loginUser = null;
                tvUser.setText("로그인이 필요합니다");
                btnLogin.setText("로그인");
            }
        });
        dlg.setNegativeButton("취소", null);
        dlg.show();
    }

    // ===== 컨텍스트 메뉴 (그림판을 짧게 탭하면 뜸) =====
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("그림 메뉴");
        MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.menu2, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.ctxUndo) {
            if (graphicView.shapes.size() > 0)
                graphicView.shapes.remove(graphicView.shapes.size() - 1);
            graphicView.invalidate();
        } else if (id == R.id.ctxClear) {
            graphicView.shapes.clear();
            graphicView.invalidate();
        } else if (id == R.id.ctxColor) {
            showColorDialog();
        } else {
            return super.onContextItemSelected(item);
        }
        return true;
    }

    // ===== 그래픽 + 터치 이벤트 (9장) =====
    static class MyShape {
        int shape, color, startX, startY, stopX, stopY;
        boolean fill, bold;
    }

    private static class MyGraphicView extends View {
        ArrayList<MyShape> shapes = new ArrayList<MyShape>();
        MyShape cur = null; // 드래그 중인 도형

        public MyGraphicView(Context context) {
            super(context);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (loginUser == null) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    Toast.makeText(getContext(), "먼저 로그인하세요", Toast.LENGTH_SHORT).show();
                return true;
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    cur = new MyShape();
                    cur.shape = curShape;
                    cur.color = curColor;
                    cur.fill = fill;
                    cur.bold = bold;
                    cur.startX = cur.stopX = (int) event.getX();
                    cur.startY = cur.stopY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    cur.stopX = (int) event.getX();
                    cur.stopY = (int) event.getY();
                    this.invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    cur.stopX = (int) event.getX();
                    cur.stopY = (int) event.getY();
                    // 거의 안 움직였으면 = 탭 → 메뉴 띄우기
                    if (Math.abs(cur.stopX - cur.startX) < 10 && Math.abs(cur.stopY - cur.startY) < 10)
                        this.showContextMenu();
                    else
                        shapes.add(cur);
                    cur = null;
                    this.invalidate();
                    break;
            }
            return true;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            for (int i = 0; i < shapes.size(); i++)
                drawShape(canvas, shapes.get(i));
            if (cur != null)
                drawShape(canvas, cur);
        }

        void drawShape(Canvas canvas, MyShape s) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(s.bold ? 15 : 5);
            paint.setStyle(s.fill ? Paint.Style.FILL : Paint.Style.STROKE);
            paint.setColor(s.color);

            switch (s.shape) {
                case LINE:
                    canvas.drawLine(s.startX, s.startY, s.stopX, s.stopY, paint);
                    break;
                case CIRCLE:
                    int radius = (int) Math.sqrt(Math.pow(s.stopX - s.startX, 2)
                            + Math.pow(s.stopY - s.startY, 2));
                    canvas.drawCircle(s.startX, s.startY, radius, paint);
                    break;
                case RECTANGLE:
                    canvas.drawRect(Math.min(s.startX, s.stopX), Math.min(s.startY, s.stopY),
                            Math.max(s.startX, s.stopX), Math.max(s.startY, s.stopY), paint);
                    break;
            }
        }
    }
}
