package com.planetory.io;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    BackPressCloseHandler backPressCloseHandler;
    WifiControl wifiControl;

    //STORE_LOCATION 값은 추후 직원의 매장 정보를 가지고 오는데 사용될 변수
    private String STORE_LOCATION = "EASW";

    private final int TIMER_ID = 0;
    Button StartBtn;
    Button StopBtn;
    ToggleButton BreakBtn;
    private final int EMPLOY_STATE_WORK = 1;
    private final int EMPLOY_STATE_LEAVE = 2;
    private final int EMPLOY_STATE_BREAK = 3;
    private int EMPLOY_STATE = EMPLOY_STATE_LEAVE;

    private Timer timer;

    ClockView clockView;

    TextView dateNow;
    TextView LEAVE_TIME;
    TextView wifiScan;


    Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == TIMER_ID) {
                clockView.invalidate();
                String time = (String) msg.obj;
                dateNow.setText(time);
            }
            return true;
        }
    });


    public String getStartTimeFromDB() {
        /*
            DB 또는 앱에서 직원의 당일 스케줄 시작시간을 가지고 온다.
            start_time 변수는 DB로 부터 오늘의 일과 시작 시간을 받아온다.
            start_time의 유형은 HH:mm:ss 포맷으로 가져온다.
            DB에서 받아온 Text 값을 float 형으로 교체해주는 루틴이 들어가야된다.
         */
        String start_time = null;
//        start_time = "24:00:00";

        return start_time;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        backPressCloseHandler = new BackPressCloseHandler(this);
        wifiControl = new WifiControl(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dateNow = (TextView) findViewById(R.id.dateNow);
        wifiScan = (TextView) findViewById(R.id.wifi);

        clockView = (ClockView) findViewById(R.id.clock_view);

        setButtons();

        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            /*
                매 1초마다 Handler에 메세지 전송해서
                UI 업데이트를 해준다 디지털 시계 및 원호
             */
            @Override
            public void run() {
                try {
                    //시계 값 전달
                    Message msg = handler.obtainMessage();
                    msg.what = TIMER_ID;
                    msg.obj = clockView.getFormatDateTime();
                    handler.sendMessage(msg);
                } catch (Exception e) { e.printStackTrace(); }
            }
        }, 1000, 1000);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, MyWorkHistoryActivity.class);
                startActivity(intent);
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setButtons() {
        StartBtn = (Button) findViewById(R.id.start_btn);
        StopBtn = (Button) findViewById(R.id.stop_btn);
        BreakBtn = (ToggleButton) findViewById(R.id.break_btn);

        StartBtn.setOnClickListener(onClickListener);
        StopBtn.setOnClickListener(onClickListener);
        BreakBtn.setOnClickListener(onToggleListener);

        /*
            앱을 사용자가 자주 키고 끄고 할 수 있으므로,
            현재 근무 상태를 shared preference를 이용해서 앱에 저장하는 것이 나을 듯 하다.
         */

        if(EMPLOY_STATE == EMPLOY_STATE_LEAVE) {
            //출근 중이 아닐 때 활성화
            StopBtn.setEnabled(false);
            BreakBtn.setEnabled(false);
        }

        if(EMPLOY_STATE == EMPLOY_STATE_WORK) {
            //출근 했을 때 활성화 앱을 다시 켰을 때
            StartBtn.setEnabled(false);
        }
    }

    private void stateMessage(int STATE) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        switch(STATE) {
            case EMPLOY_STATE_WORK:
                alertBuilder.setTitle("출근 처리가 완료되었습니다.");
                alertBuilder.setMessage(clockView.getFormatDateTime());
                break;
            case EMPLOY_STATE_LEAVE:
                alertBuilder.setTitle("퇴근 처리가 완료되었습니다.");
                alertBuilder.setMessage(clockView.getFormatDateTime());
                break;
        }
        alertBuilder.show();
    }

    Button.OnClickListener onClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.start_btn:
                    /*
                        근무 시작 버튼
                        Wifi 스캔 및 실제 출근시간 기록
                     */
                    wifiControl.scanWifi(STORE_LOCATION);
                    clockView.setClockColor("#0000FF");
                    StopBtn.setEnabled(true);
                    BreakBtn.setEnabled(true);
                    StartBtn.setEnabled(false);
                    EMPLOY_STATE = EMPLOY_STATE_WORK;
                    break;
                case R.id.stop_btn:
                    /*
                        근무 종료 버튼
                    */
                    StartBtn.setEnabled(true);
                    StopBtn.setEnabled(false);
                    BreakBtn.setEnabled(false);
                    EMPLOY_STATE = EMPLOY_STATE_LEAVE;
                    break;

            }
            stateMessage(EMPLOY_STATE);
        }
    };

    ToggleButton.OnClickListener onToggleListener = new ToggleButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.break_btn:
                    /*
                        휴식 버튼
                     */
                    if( !BreakBtn.isChecked() ) {
                        /*
                            휴식 상태가 아닐 경우 휴식 상태로 바꾼다.
                            쉬는 시간을 DB에 등록
                         */
                        clockView.setClockColor("#00FF00");
                        Toast.makeText(MainActivity.this, "색깔아 바껴라", Toast.LENGTH_SHORT).show();
                        Log.d("isCheck toggle", "색깔바껴야되는디..");
                        EMPLOY_STATE = EMPLOY_STATE_BREAK;
                    } else {
                        /*
                            휴식 상태일 경우 휴식 종료로 바꾼다.
                            쉬는 시간 종료를 DB에 등록
                         */
                        Toast.makeText(MainActivity.this, "휴식 끝", Toast.LENGTH_SHORT).show();
                        Log.d("isCheck toggle", "휴식 종료 들어간다.");
                        EMPLOY_STATE = EMPLOY_STATE_WORK;
                    }
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if( drawerLayout.isDrawerOpen(GravityCompat.START) ) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            backPressCloseHandler.onBackPressed();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        timer.cancel();
        super.onDestroy();
    }
}
