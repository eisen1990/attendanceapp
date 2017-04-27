package com.planetory.io;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ClockView extends View {
    /*
        시계 표시 이미지 위에 업무시간 진행 원 그리는 View Class
     */

    /*
        시계 좌표 임시 값
     */

    private final int EMPLOY_STATE_WORK = 1;
    private final int EMPLOY_STATE_LEAVE = 2;
    private final int EMPLOY_STATE_BREAK_START = 3;
    private final int EMPLOY_STATE_BREAK_STOP = 4;
    private int EMPLOY_STATE = EMPLOY_STATE_LEAVE;

    private float start_x = 110.0f;
    private float start_y = 190.0f;
    private float end_x = 730.0f;
    private float end_y = 730.0f;

    TodayHistory todayHistory = new TodayHistory();
    private int BREAK_COUNT = 0;

    //90도 부터 그리기 시작 -> 0시(24시)
    private final float START_ANGLE = 90.0f;
    private float CURRENT_ANGLE;
    private String ClockColor = "#808080";

    public ClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setStrokeWidth(8f);
        paint.setColor(Color.parseColor(ClockColor));
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        CURRENT_ANGLE = FromTimeToAngle(getFormatDateTime());
//        Log.d("startAngle,", String.valueOf(START_ANGLE));
//        Log.d("currentAngle", String.valueOf(currentAngle));
//        Log.d("스타트 누른 시각",todayHistory.getStartTime());

        RectF rect = new RectF();
        rect.set(start_x, start_y, start_x + end_x, start_y + end_y);
        float start_temp = FromTimeToAngle(todayHistory.getStartTime());
        if (CURRENT_ANGLE < 360) {
            if(start_temp > 0) {
                paint.setColor(Color.parseColor("#5D5D5D"));
                canvas.drawArc(rect, START_ANGLE, start_temp, false, paint);
                paint.setColor(Color.parseColor("#1254FF"));
                canvas.drawArc(rect, start_temp, CURRENT_ANGLE-start_temp, false, paint);
            } else {
                canvas.drawArc(rect, START_ANGLE, CURRENT_ANGLE, false, paint);
            }
        } else {
//            현재 시간에 대한 각도가 90도부터 그리기 시작해서 360도를 그리는 경우
//            Not reach, 존재하지 않음.
        }
    }

    public void setCURRENT_ANGLE(float i) {
        this.CURRENT_ANGLE = i;
    }

    public float getCURRENT_ANGLE() {
        return this.CURRENT_ANGLE;
    }

    public String getClockColor() {
        return ClockColor;
    }

    public void setClockColor(String clockColor) {
        ClockColor = clockColor;
    }

    public float FromTimeToAngle(String time) {
        float value = 0.0f;

        /*
            time이 HH:mm:ss 포맷으로 전달되었는지 확인해야한다..
            정규식으로 Pattern과 Matcher 클래스 이용
            Pattern pattern = Pattern.compile(정규식)
            Matcher matcher = pattern.matcher(스트링);
            if(matcher.matches()) true else false
         */

        if (time != null) {
            String timeformat[] = time.split(":", 3);
            //시간:분을 초 단위로 환산 후 각으로 변경
            value += Float.parseFloat(timeformat[0]) * 60 * 60;
            value += Float.parseFloat(timeformat[1]) * 60;

            //한 바퀴(360도)를 24시간으로 나눔
            value = (value * 360) / (24 * 60 * 60);
        }
//         기준점 조정
//        value += 90.0f;

        return value;
    }

    public String getFormatDateTime() {
        /*
            현재 시간을 문자열로 반환하는 함수.
            Main Activity에서는 시계 UI를 업데이트하는 Thread에서 사용된다.
         */
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        return new SimpleDateFormat("HH:mm:ss").format(date);
    }

    public void setHISTORY_COUNT(String history_time, int STATE) {
        /*
            STATE에 따라 출근/휴식시작/휴식끝/퇴근으로 상태를 나눈다.
            break time은 순번 0부터 시작한다.
            0(짝수) 일때 휴식 시작
            1(홀수) 일때 휴식 끝
            따라서 BREAK_COUNT % 2 == 0 일 때 휴식 시작한 것.
         */
        if (STATE == EMPLOY_STATE_WORK) {
            todayHistory.setStartTime(history_time);
        } else if (STATE == EMPLOY_STATE_LEAVE) {
            todayHistory.setEndTime(history_time);
        } else if (STATE == EMPLOY_STATE_BREAK_START) {
            HashMap<String, String> temp = new HashMap<>();
            temp.put(String.valueOf(BREAK_COUNT), history_time);
            todayHistory.setBreakTimes(temp);
        } else if (STATE == EMPLOY_STATE_BREAK_STOP) {
            HashMap<String, String> temp = new HashMap<>();
            temp.put(String.valueOf(BREAK_COUNT), history_time);
            todayHistory.setBreakTimes(temp);
        } else ;
        this.BREAK_COUNT++;
    }
}
