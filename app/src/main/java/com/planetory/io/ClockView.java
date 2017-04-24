package com.planetory.io;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClockView extends View {
    /*
        시계 표시 이미지 위에 업무시간 진행 원 그리는 View Class
     */

    //90도 부터 그리기 시작 -> 0시(24시)
    private float START_ANGLE = 90.0f;
    private float ANGLE;
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

        float currentAngle = FromTimeToAngle(getFormatDateTime());
//        Log.d("startAngle,", String.valueOf(START_ANGLE));
//        Log.d("currentAngle", String.valueOf(currentAngle));

        RectF rect = new RectF();
        rect.set(200,200,800,800);
//        currentAngle = currentAngle - START_ANGLE;
        if( currentAngle < 360 ) {
            //원 그림에 안티얼라이징 추가 계단 현상 방지
            paint.setAntiAlias(true);
            canvas.drawArc(rect, START_ANGLE, currentAngle, false, paint);
        }
        else {
//            현재 시간에 대한 각도가 90도부터 그리기 시작해서 360도를 그리는 경우
//            Not reach 존재하지 않음.
        }
    }

    public void setANGLE(float i) {
        this.ANGLE = i;
    }

    public float getANGLE() {
        return this.ANGLE;
    }

    public String getClockColor() {
        return ClockColor;
    }

    public void setClockColor(String clockColor) {
        ClockColor = clockColor;
    }

    public float getSTART_ANGLE() {
        return START_ANGLE;
    }

    public void setSTART_ANGLE(String start_time) {
        this.START_ANGLE = FromTimeToAngle(null);
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

        if( time != null )  {
            String timeformat[] = time.split(":", 3);
            //시간:분을 초 단위로 환산 후 각으로 변경
            value += Float.parseFloat(timeformat[0]) * 60 * 60;
            value += Float.parseFloat(timeformat[1]) * 60;

            //한 바퀴(360도)를 24시간으로 나눔
            value = ( value * 360 ) / ( 24 * 60 * 60 );
        }

//         기준점 조정
//        value += 90.0f;

        return value;
    }

    public String getFormatDateTime() {
        /*
            현재 시간을 문자열로 반환하는 함수.
            Main Activity에서 시계 UI를 업데이트하는 Thread에서 사용된다.
         */
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        return new SimpleDateFormat("HH:mm:ss").format(date);
    }

}
