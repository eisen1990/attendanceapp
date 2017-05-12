package com.planetory.io;

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

    private final int LEAVE_TIMER_ID = 0;
    private final int WORK_TIMER_ID = 1;

    String TIME_AM;
    String TIME_PM;

    RelativeLayout layoutLeave;
    RelativeLayout layoutWork;
    TextView txtLeaveTime;
    TextView txtLeaveDate;
    TextView txtLeaveMeridiem;
    TextView txtWorkClock;
    Button btnPunchin;

    Timer leaveTimer;
    Timer workTimer;

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

        layoutLeave = (RelativeLayout) findViewById(R.id.activity_main_layout_leave);
        layoutWork = (RelativeLayout) findViewById(R.id.activity_main_layout_work);
        txtLeaveDate = (TextView) findViewById(R.id.activity_main_leave_txt_date);
        txtLeaveTime = (TextView) findViewById(R.id.activity_main_leave_txt_time);
        txtLeaveMeridiem = (TextView) findViewById(R.id.activity_main_leave_txt_meridiem);
        txtWorkClock = (TextView) findViewById(R.id.activity_main_work_txt_clock);
        btnPunchin = (Button) findViewById(R.id.activity_main_leave_btn_punch_in);

        basicSetting();

        buttonSetting();
        leaveSetting();
    }

    private void basicSetting(){
        //자동 생성된 기본 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.activity_main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void leaveSetting(){
        leaveTimer = new Timer(true);
        layoutWork.setVisibility(View.GONE);
        layoutLeave.setVisibility(View.VISIBLE);

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

    private void buttonSetting(){
        btnPunchin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaveTimer.cancel();
                workSetting();
            }
        });
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
        leaveTimer.cancel();
        super.onDestroy();
    }
}
