package com.planetory.io;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class TestActivity extends AppCompatActivity {

    private final String DEFAULT_PREFERENCE = "test_pref";
    private final String PREF_EMPLOYEE_STATE = "employee_state";

    static final int EMPLOYEE_STATE_LEAVE = 0;
    static final int EMPLOYEE_STATE_WORK = 1;
    static final int EMPLOYEE_STATE_BREAK = 2;

    private int EMPLOYEE_STATE;

    SharedPreferences APP_PREF;

    Button btnPunchIn;
    Button btnPunchOut;
    Button btnBreak;
    Button btnReturn;
    PunchTracker tracker;

    private final int TIMER_TICK = 10;

    Timer timer;
    Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == TIMER_TICK) {
                tracker.refreshView(getCurrentTime(FORMAT_TIME));
            }
            return true;
        }
    });

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
        setContentView(R.layout.activity_test);

        btnPunchIn = (Button) findViewById(R.id.button_punch_in);
        btnPunchOut = (Button) findViewById(R.id.button_punch_out);
        btnBreak = (Button) findViewById(R.id.button_break);
        btnReturn = (Button) findViewById(R.id.button_return);
        tracker = (PunchTracker) findViewById(R.id.punchTracker);

        APP_PREF = getSharedPreferences(DEFAULT_PREFERENCE, MODE_PRIVATE);

        stateSetting(false);
        buttonSetting();
        tracker.setPunchTime("00:10:00", "00:40:00");

        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    handler.sendEmptyMessage(TIMER_TICK);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000);
    }

    private void buttonSetting(){
        btnPunchIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //출근 처리가 완료된 경우 들어갈 코드
                EMPLOYEE_STATE = EMPLOYEE_STATE_WORK;
                stateSetting(true);

                tracker.addState(getCurrentTime(FORMAT_TIME), EMPLOYEE_STATE);
            }
        });

        btnPunchOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //퇴근 처리가 완료된 경우 들어갈 코드
                EMPLOYEE_STATE = EMPLOYEE_STATE_LEAVE;
                stateSetting(true);

                tracker.addState(getCurrentTime(FORMAT_TIME), EMPLOYEE_STATE);
            }
        });

        btnBreak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //자리비움 처리가 완료된 경우 들어갈 코드
                EMPLOYEE_STATE = EMPLOYEE_STATE_BREAK;
                stateSetting(true);

                tracker.addState(getCurrentTime(FORMAT_TIME), EMPLOYEE_STATE);
            }
        });

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //복귀 처리가 완료된 경우 들어갈 코드
                EMPLOYEE_STATE = EMPLOYEE_STATE_WORK;
                stateSetting(true);

                tracker.addState(getCurrentTime(FORMAT_TIME), EMPLOYEE_STATE);
            }
        });
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
                btnPunchIn.setEnabled(true);
                btnBreak.setVisibility(View.INVISIBLE);
                btnReturn.setVisibility(View.INVISIBLE);
                btnPunchOut.setEnabled(false);
                break;
            case EMPLOYEE_STATE_BREAK:
                btnBreak.setVisibility(View.INVISIBLE);
                btnReturn.setVisibility(View.VISIBLE);
                break;
            case EMPLOYEE_STATE_WORK:
                btnPunchIn.setEnabled(false);
                btnPunchOut.setEnabled(true);
                btnBreak.setVisibility(View.VISIBLE);
                btnReturn.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }
}
