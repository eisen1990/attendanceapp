package com.planetory.io;

import android.app.Activity;
import android.widget.Toast;

public class BackPressCloseHandler {
    /*
    Back 버튼 두번 2초 안에 터치했을 경우 종료
    한 번 눌렀을 경우 메세지 띄워준다.
    기본적인 Interval은 2000ms 이지만 추후 수정 가능
    */

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            activity.finish();
        }
        else {
            backPressedTime = tempTime;
            Toast.makeText(activity, "Press again to exit", Toast.LENGTH_SHORT).show();
        }
    }
}
