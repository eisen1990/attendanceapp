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
import android.widget.TextView;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainNewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final int LEAVE_TIMER_ID = 0;

    String TIME_AM;
    String TIME_PM;

    TextView txtLeaveTime;
    TextView txtLeaveDate;
    TextView txtLeaveMeridiem;

    Timer leaveTimer;

    Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == LEAVE_TIMER_ID) {
                Calendar calendar = Calendar.getInstance();

                String sTime = DateFormat.format("hh:mm:ss", calendar).toString();
                String sMeridiem = calendar.get(Calendar.HOUR_OF_DAY) >= 12 ? TIME_PM : TIME_AM;
                CharSequence cDate = calendar.get(Calendar.MONTH) >= 10 ? DateFormat.format("MM월 dd일", calendar) :
                        DateFormat.format("M월 dd일", calendar);
                String sDate = cDate.toString() + " (" + toDayofWeek(calendar.get(Calendar.DAY_OF_WEEK)) + ')';

                txtLeaveTime.setText(sTime);
                txtLeaveMeridiem.setText(sMeridiem);
                txtLeaveDate.setText(sDate);
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

        TIME_AM = getString(R.string.activity_main_leave_am);
        TIME_PM = getString(R.string.activity_main_leave_pm);

        txtLeaveDate = (TextView) findViewById(R.id.activity_main_leave_txt_date);
        txtLeaveTime = (TextView) findViewById(R.id.activity_main_leave_txt_time);
        txtLeaveMeridiem = (TextView) findViewById(R.id.activity_main_leave_txt_meridiem);

        basicSetting();
        timerSetting();
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

    private void timerSetting(){
        leaveTimer = new Timer(true);
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
