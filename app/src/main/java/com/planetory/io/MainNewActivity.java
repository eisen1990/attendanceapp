package com.planetory.io;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainNewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static final String DEFAULT_PREFERENCE = "io_pref";
    static final int REQUEST_BREAK = 0;

    static final int EMPLOYEE_STATE_LEAVE = 0;
    static final int EMPLOYEE_STATE_WORK = 1;
    static final int EMPLOYEE_STATE_BREAK = 2;

    private final String PREF_EMPLOYEE_STATE = "employee_state";
    private int EMPLOYEE_STATE;

    private final int LEAVE_TIMER_ID = 0;
    private final int WORK_TIMER_ID = 1;

    String TIME_AM;
    String TIME_PM;
    SharedPreferences APP_PREF;

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
    public String toDayofWeek(int i){
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);

        TIME_AM = getString(R.string.activity_main_am);
        TIME_PM = getString(R.string.activity_main_pm);
        APP_PREF = getSharedPreferences(DEFAULT_PREFERENCE, MODE_PRIVATE);

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

        basicSetting();
        buttonSetting();
        stateSetting(false);
    }

    private void basicSetting(){
        //자동 생성된 기본 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_break_request_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.activity_main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void buttonSetting(){
        btnPunchin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isWifiValid()){
                    //Wifi가 성공적으로 잡힌 경우
                    //나중에 얼굴 인식 부분을 넣어야 하지만, 일단은 넘어가기
                    LayoutInflater inflater = LayoutInflater.from(MainNewActivity.this);
                    View dView = inflater.inflate(R.layout.dialog_main_work_complete, layoutLeave, false);
                    TextView txtTime = (TextView) dView.findViewById(R.id.dialog_main_work_time);
                    Button btnOk = (Button) dView.findViewById(R.id.dialog_main_work_btn_ok);

                    //현재 시간을 텍스트뷰에 표시하는 부분
                    Calendar calendar = Calendar.getInstance();
                    CharSequence cTime = DateFormat.format("hh:mm:ss", calendar);
                    String sMeridiem = calendar.get(Calendar.HOUR_OF_DAY) >= 12 ? getString(R.string.dialog_main_pm)
                            : getString(R.string.dialog_main_am);
                    String sTime = cTime.toString() + ' ' + sMeridiem;
                    txtTime.setText(sTime);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainNewActivity.this);
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
                } else{
                    //신규매장 등록 묻는 코드가 들어갈 부분
                }
            }
        });

        /*
            이하는 일단 테스트용
         */
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainNewActivity.this);
                builder.setTitle(R.string.dialog_main_pause_title);
                builder.setMessage(R.string.dialog_main_pause_message);
                builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainNewActivity.this, BreakRequestActivity.class);
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

        btnPunchout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EMPLOYEE_STATE = EMPLOYEE_STATE_LEAVE;
                stateSetting(true);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_BREAK){
            if(resultCode == RESULT_OK){
                EMPLOYEE_STATE = EMPLOYEE_STATE_BREAK;
                stateSetting(true);
            }
        }
    }


    private void stateSetting(boolean set){
        /*
            SharedPreference에서 앱의 상태를 가져오거나
            앱의 상태를 SharedPreference에 저장
         */
        if(set){
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

    private void leaveSetting(){
        leaveTimer = new Timer(true);
        layoutWork.setVisibility(View.GONE);
        layoutLeave.setVisibility(View.VISIBLE);

        if (workTimer != null){
            workTimer.cancel();
        }

        leaveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try{
                    handler.sendEmptyMessage(LEAVE_TIMER_ID);
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }, 0, 1000);
    }

    private void workSetting(){
        workTimer = new Timer(true);
        layoutLeave.setVisibility(View.GONE);
        layoutWork.setVisibility(View.VISIBLE);

        if (leaveTimer != null){
            leaveTimer.cancel();
        }

        workTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try{
                    handler.sendEmptyMessage(WORK_TIMER_ID);
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }, 0, 1000);
    }

    private void breakSetting(boolean pause){
        if (pause){
            btnPause.setVisibility(View.GONE);
            btnReturn.setVisibility(View.VISIBLE);
        } else{
            btnPause.setVisibility(View.VISIBLE);
            btnReturn.setVisibility(View.GONE);
        }
    }


    private boolean isWifiValid(){
        //Wifi 리스트를 체크해서 서버에 등록된 Wi-fi가 있는지 확인.
        return true;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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
}
