package com.planetory.io;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.text.format.DateFormat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    BackPressCloseHandler backPressCloseHandler;
    WifiControl wifiControl;
    private URLTask urlTask = null;
    private String user_phone; //아이디

    static final String DEFAULT_PREFERENCE = "io_pref";
    static final int REQUEST_BREAK = 0;

    static final String INTENT_USER_PHONE = "user_phone";
    static final String INTENT_USER_PASSWORD = "user_password";

    static final int EMPLOYEE_STATE_LEAVE = 0;
    static final int EMPLOYEE_STATE_WORK = 1;
    static final int EMPLOYEE_STATE_BREAK = 2;

    private final String PREF_EMPLOYEE_STATE = "employee_state";
    private final String PREF_LAST_LOGIN = "last_login";
    private final String PREF_LAST_LOGIN_ID = "last_login_id";
    private int EMPLOYEE_STATE;

    private final int LEAVE_TIMER_ID = 0;
    private final int WORK_TIMER_ID = 1;

    String TIME_AM;
    String TIME_PM;
    SharedPreferences APP_PREF;

    FrameLayout frameLayout;

    RelativeLayout layoutLeave;
    RelativeLayout layoutWork;
    TextView txtLeaveTime;
    TextView txtLeaveDate;
    TextView txtLeaveMeridiem;
    TextView txtWorkClock;
    Button btnPunchin;
    Button btnPause;
    Button btnReturn;
    Button btnPunchout;
    Button NavigationBtn;
    ImageButton btnAlarm;

    private Timer leaveTimer;
    private Timer workTimer;


    Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            Calendar calendar = Calendar.getInstance();

            String sMeridiem = calendar.get(Calendar.HOUR_OF_DAY) >= 12 ? TIME_PM : TIME_AM;
            CharSequence cDate = calendar.get(Calendar.MONTH) >= 10 ? DateFormat.format("MM월 dd일", calendar) :
                    DateFormat.format("M월 dd일", calendar);
            String sWeekday = "(" + toDayofWeek(calendar.get(Calendar.DAY_OF_WEEK)) + ')';
            String sDate = cDate.toString() + ' ' + sWeekday;

            if (msg.what == LEAVE_TIMER_ID) {
                CharSequence cTime = DateFormat.format("hh:mm:ss", calendar);

                txtLeaveTime.setText(cTime);
                txtLeaveMeridiem.setText(sMeridiem);
                txtLeaveDate.setText(sDate);
            } else if (msg.what == WORK_TIMER_ID) {
                CharSequence cTime = calendar.get(Calendar.HOUR) >= 10 ? DateFormat.format("hh:mm", calendar) :
                        DateFormat.format("h:mm", calendar);
                String sClock = sDate + ' ' + sMeridiem + ' ' + cTime.toString();

                txtWorkClock.setText(sClock);
            }
            return true;
        }
    });

    //TODO:"월", "일", 요일 문자열을 xml에서 얻어오도록 바꾸기.
    public String toDayofWeek(int i) {
        String day = "";

        switch (i) {
            case 1:
                day = "일";
                break;
            case 2:
                day = "월";
                break;
            case 3:
                day = "화";
                break;
            case 4:
                day = "수";
                break;
            case 5:
                day = "목";
                break;
            case 6:
                day = "금";
                break;
            case 7:
                day = "토";
                break;
        }
        return day;
    }

    private final int FORMAT_ALL = 0;
    private final int FORMAT_DATE = 1;
    private final int FORMAT_TIME = 2;

    private String getCurrentTime(int format) {
        //띄어쓰기가 있으면 서버 에러 발생?
        //현재 시간을 여러 포맷으로 돌려주는 함수
        long time = System.currentTimeMillis();
        SimpleDateFormat day = new SimpleDateFormat();

        switch(format){
            case FORMAT_ALL:
                day = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
                break;
            case FORMAT_DATE:
                day = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                break;
            case FORMAT_TIME:
                day = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                break;
        }

        return day.format(new Date(time));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_phone = getIntent().getExtras().getString(INTENT_USER_PHONE);

        backPressCloseHandler = new BackPressCloseHandler(this);
        wifiControl = new WifiControl(this);

        TIME_AM = getString(R.string.activity_main_am);
        TIME_PM = getString(R.string.activity_main_pm);
        APP_PREF = getSharedPreferences(DEFAULT_PREFERENCE, MODE_PRIVATE);

        frameLayout = (FrameLayout) findViewById(R.id.content_main);
        layoutLeave = (RelativeLayout) findViewById(R.id.activity_main_layout_leave);
        layoutWork = (RelativeLayout) findViewById(R.id.activity_main_layout_work);
        txtLeaveDate = (TextView) findViewById(R.id.activity_main_leave_txt_date);
        txtLeaveTime = (TextView) findViewById(R.id.activity_main_leave_txt_time);
        txtLeaveMeridiem = (TextView) findViewById(R.id.activity_main_leave_txt_meridiem);
        txtWorkClock = (TextView) findViewById(R.id.activity_main_work_txt_clock);
        btnPunchin = (Button) findViewById(R.id.activity_main_leave_btn_punch_in);
        btnPause = (Button) findViewById(R.id.activity_main_work_btn_pause);
        btnReturn = (Button) findViewById(R.id.activity_main_work_btn_return);
        btnPunchout = (Button) findViewById(R.id.activity_main_work_btn_punch_out);
        btnAlarm = (ImageButton) findViewById(R.id.toolbar_main_btn_alarm);
        NavigationBtn = (Button) findViewById(R.id.role_switching_btn);

        basicSetting();
        buttonSetting();
        getDatafromServer();
    }

    private void basicSetting() {
        //자동 생성된 기본 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void buttonSetting() {
        btnPunchin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wifiControl.isWifi()) {
                    //Wifi가 활성화 되있는 경우
                    //서버에 출근 시각을 전송한다.

                    urlTask = new URLTask(user_phone, getCurrentTime(FORMAT_ALL), wifiControl.scanWifi(""));
                    urlTask.execute(RestURL.CHECK_PARAM_IN);

                } else {
                     //wifi 활성화되지 않았을 경우
                    Log.d("eisen", "wifi disabled");
                    LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                    View dView = inflater.inflate(R.layout.dialog_main_wifi_enabled, frameLayout, false);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setView(dView);
                    builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton(R.string.dialog_main_wifi_enabled, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("eisen", "Wifi be enabled");
                            wifiControl.setWifi();
                        }
                    });
                    builder.create().show();
                }
            }
        });

        btnPunchout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wifiControl.isWifi()) {

                    //Wifi가 활성화 되있는 경우
                    //서버에 퇴근 시간을 전송한다.
                    urlTask = new URLTask(user_phone, getCurrentTime(FORMAT_ALL), wifiControl.scanWifi(""));
                    urlTask.execute(RestURL.CHECK_PARAM_OUT);

                } else {
                    //wifi 활성화되지 않았을 경우
                    Log.d("eisen", "wifi disabled");
                    LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                    View dView = inflater.inflate(R.layout.dialog_main_wifi_enabled, frameLayout, false);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setView(dView);
                    builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton(R.string.dialog_main_wifi_enabled, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("eisen", "Wifi be enabled");
                            wifiControl.setWifi();
                        }
                    });
                    builder.create().show();
                }
            }
        });

        NavigationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LeaderMainActivity.class);
                intent.putExtra("user_phone", user_phone);
                startActivity(intent);
                finish();
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.dialog_main_pause_title);
                builder.setMessage(R.string.dialog_main_pause_message);
                builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, BreakRequestActivity.class);
                        startActivityForResult(intent, REQUEST_BREAK);
                    }
                });
                builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EMPLOYEE_STATE = EMPLOYEE_STATE_WORK;
                stateSetting(true);
            }
        });

        //테스트용
        btnAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TestActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getDatafromServer(){
        //해당 날짜에 앱을 처음 킨 경우 서버에서 데이터 얻어오도록,
        String LastLogin = APP_PREF.getString(PREF_LAST_LOGIN, "");
        String LastLoginID = APP_PREF.getString(PREF_LAST_LOGIN_ID, "");

        if (LastLoginID.equals(user_phone) && LastLogin.equals(getCurrentTime(FORMAT_DATE))){
            stateSetting(false);
        } else{
            SharedPreferences.Editor editor = APP_PREF.edit();
            editor.putString(PREF_LAST_LOGIN, getCurrentTime(FORMAT_DATE));
            editor.putString(PREF_LAST_LOGIN_ID, user_phone);
            editor.apply();
            urlTask = new URLTask(user_phone);
            urlTask.execute(RestURL.CHECK_PARAM_WORKON);
        }
    }


    private void stateSetting(boolean set) {
        /*
            SharedPreference에서 앱의 상태를 가져오거나
            앱의 상태를 SharedPreference에 저장
         */
        if (set) {
            SharedPreferences.Editor editor = APP_PREF.edit();
            editor.putInt(PREF_EMPLOYEE_STATE, EMPLOYEE_STATE);
            editor.apply();
        } else {
            EMPLOYEE_STATE = APP_PREF.getInt(PREF_EMPLOYEE_STATE, EMPLOYEE_STATE_LEAVE);
        }

        switch (EMPLOYEE_STATE) {
            case EMPLOYEE_STATE_LEAVE:
                leaveSetting();
                break;
            case EMPLOYEE_STATE_BREAK:
                workSetting();
                breakSetting(true);
                break;
            case EMPLOYEE_STATE_WORK:
                workSetting();
                breakSetting(false);
                break;
        }
    }

    private void leaveSetting() {
        leaveTimer = new Timer(true);
        layoutWork.setVisibility(View.GONE);
        layoutLeave.setVisibility(View.VISIBLE);

        if (workTimer != null) {
            workTimer.cancel();
        }

        leaveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    handler.sendEmptyMessage(LEAVE_TIMER_ID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000);
    }

    private void workSetting() {
        workTimer = new Timer(true);
        layoutLeave.setVisibility(View.GONE);
        layoutWork.setVisibility(View.VISIBLE);

        if (leaveTimer != null) {
            leaveTimer.cancel();
        }

        workTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    handler.sendEmptyMessage(WORK_TIMER_ID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000);
    }

    private void breakSetting(boolean pause) {
        if (pause) {
            btnPause.setVisibility(View.GONE);
            btnReturn.setVisibility(View.VISIBLE);
        } else {
            btnPause.setVisibility(View.VISIBLE);
            btnReturn.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_BREAK) {
            if (resultCode == RESULT_OK) {
                EMPLOYEE_STATE = EMPLOYEE_STATE_BREAK;
                stateSetting(true);
            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            backPressCloseHandler.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent intent;

        int id = item.getItemId();

        if (id == R.id.vacation_request) {
            // Handle the camera action
        } else if (id == R.id.loginout) {

        } else if (id == R.id.settings) {
            intent = new Intent(MainActivity.this, WifiListActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        if (leaveTimer != null) leaveTimer.cancel();
        if (workTimer != null) workTimer.cancel();
        super.onDestroy();
    }


    private class URLTask extends AsyncTask<String, Void, String> {

        private String phone;
        private String time;
        private String wifiaplist;

        URLTask(String phone) {
            this.phone = phone;
            this.time = null;
            this.wifiaplist = null;
        }

        URLTask(String phone, String time, String wifiaplist) {
            this.phone = phone;
            this.time = time;
            this.wifiaplist = wifiaplist;
        }

        @Override
        protected void onPostExecute(String s) {
            urlTask = null;
            if (s == null) s = RestURL.NULL_STRING + "\0";
            Log.d("eisen", s);
            s = s.substring(0, s.length() - 1);

            if (s.equals(RestURL.PUNCHIN_SUCCESS)) {
                //나중에 얼굴 인식 부분을 넣어야 하지만, 일단은 넘어가기
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                View dView = inflater.inflate(R.layout.dialog_main_work_complete, layoutLeave, false);
                TextView txtTime = (TextView) dView.findViewById(R.id.dialog_main_work_time);
                TextView txtMessage = (TextView) dView.findViewById(R.id.dialog_main_work_txt_complete);
                Button btnOk = (Button) dView.findViewById(R.id.dialog_main_work_btn_ok);

                //현재 시간을 텍스트뷰에 표시하는 부분
                Calendar calendar = Calendar.getInstance();
                CharSequence cTime = DateFormat.format("hh:mm:ss", calendar);
                String sMeridiem = calendar.get(Calendar.HOUR_OF_DAY) >= 12 ? getString(R.string.dialog_main_pm)
                        : getString(R.string.dialog_main_am);
                String sTime = cTime.toString() + ' ' + sMeridiem;
                txtTime.setText(sTime);
                txtMessage.setText(getString(R.string.dialog_main_punchin_complete));

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(dView);

                //다이얼로그를 표시하는 부분
                final AlertDialog dialog = builder.create();
                dialog.show();

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                //출근을 처리하는 부분
                EMPLOYEE_STATE = EMPLOYEE_STATE_WORK;
                stateSetting(true);
            } else if (s.equals(RestURL.PUNCHIN_FAIL)) {
                //신규 사업장 등록 코드가 들어갈 부분.
            } else if (s.equals(RestURL.PUNCHOUT_SUCCESS)) {
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                View dView = inflater.inflate(R.layout.dialog_main_work_complete, layoutLeave, false);
                TextView txtTime = (TextView) dView.findViewById(R.id.dialog_main_work_time);
                TextView txtMessage = (TextView) dView.findViewById(R.id.dialog_main_work_txt_complete);
                Button btnOk = (Button) dView.findViewById(R.id.dialog_main_work_btn_ok);

                //현재 시간을 텍스트뷰에 표시하는 부분
                Calendar calendar = Calendar.getInstance();
                CharSequence cTime = DateFormat.format("hh:mm:ss", calendar);
                String sMeridiem = calendar.get(Calendar.HOUR_OF_DAY) >= 12 ? getString(R.string.dialog_main_pm)
                        : getString(R.string.dialog_main_am);
                String sTime = cTime.toString() + ' ' + sMeridiem;
                txtTime.setText(sTime);
                txtMessage.setText(getString(R.string.dialog_main_punchout_complete));

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(dView);

                //다이얼로그를 표시하는 부분
                final AlertDialog dialog = builder.create();
                dialog.show();

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                //퇴근을 처리하는 부분
                EMPLOYEE_STATE = EMPLOYEE_STATE_LEAVE;
                stateSetting(true);
            }
            //로그인할 때 리더와 근무자를 구분하기?
            else if (s.equals("WORKONleader")) {
                EMPLOYEE_STATE = EMPLOYEE_STATE_WORK;
                stateSetting(true);
            } else if (s.equals("WORKNOTYETleader")) {
                EMPLOYEE_STATE = EMPLOYEE_STATE_LEAVE;
                stateSetting(true);
            } else if (s.equals("WORKONworker")){
                EMPLOYEE_STATE = EMPLOYEE_STATE_WORK;
                stateSetting(true);
                NavigationBtn.setVisibility(View.INVISIBLE);
            } else if (s.equals("WORKNOTYETworker")) {
                EMPLOYEE_STATE = EMPLOYEE_STATE_LEAVE;
                stateSetting(true);
                NavigationBtn.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        protected void onCancelled() {
            urlTask = null;
        }

        @Override
        protected String doInBackground(String... params) {
            String urlString;
            if (phone.equals("") || (phone == null)) return "phonenull";

            if (params[0].equals("in")) {
                urlString = RestURL.CHECKINTOWORK_URL + "phone=" + phone + "&time=" + time + "&wifiaplist=" + wifiaplist;
            } else if (params[0].equals("out")) {
                urlString = RestURL.CHECKOUTOFWORK_URL + "phone=" + phone + "&time=" + time + "&wifiaplist=" + wifiaplist;
            } else if (params[0].equals("workon")) {
                urlString = RestURL.CHECKWORKON_URL + "phone=" + phone;
            } else {
                urlString = RestURL.SERVER_URL;
                Log.d("eisen", "");
            }

            Log.d("eisen", urlString);

            StringBuilder output = new StringBuilder();

            try {
                URL url = new URL(urlString);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setRequestMethod("GET");
                int resCode = httpURLConnection.getResponseCode();
                if (resCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        output.append(line);
                        output.append("\n");
                    }
                }
                return output.toString();

            } catch (Exception e) {
                Log.d("MainActivity", "URL exception");
            }

            return RestURL.NULL_STRING + "\0";
        }
    }
}
