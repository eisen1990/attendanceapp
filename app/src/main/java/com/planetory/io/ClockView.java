package com.planetory.io;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ClockView extends View {
    /*
        시계 표시 이미지 위에 업무시간 진행 원 그리는 View Class
     */

    private float START_ANGLE;
    private float ANGLE;
    private String ClockColor = "#808080";

    public ClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setStrokeWidth(6f);
        paint.setColor(Color.parseColor(ClockColor));
        paint.setStyle(Paint.Style.STROKE);

        long currentTimeSecond = System.currentTimeMillis() / 1000;

        float currentAngle = (currentTimeSecond * 360) / (24 * 3600);
        Log.d("currentAngle", String.valueOf(currentAngle));
        ANGLE = currentAngle - START_ANGLE;
        Log.d("ANGLE", String.valueOf(ANGLE));

        RectF rect = new RectF();
        rect.set(200,200,800,800);
        if( ANGLE > 0 && ANGLE < 360 )
            canvas.drawArc(rect, START_ANGLE, ANGLE, false, paint);
        else ;
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
        this.START_ANGLE = FromTimeToAngle(start_time);
    }

    public float FromTimeToAngle(String time) {
        float value = 0.0f;

        if( time == null ) {
            value = 225.0f;
        }

        return value;
    }

}
