package com.planetory.io;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class PunchTracker extends View{

    static class PunchHistoryItem implements Serializable{
        private int EMP_STATE;
        private String START_TIME;

        public int getEMP_STATE() {
            return EMP_STATE;
        }

        public void setEMP_STATE(int EMP_STATE) {
            this.EMP_STATE = EMP_STATE;
        }

        public String getSTART_TIME() {
            return START_TIME;
        }

        public void setSTART_TIME(String START_TIME) {
            this.START_TIME = START_TIME;
        }
    }

    /*
        사용법
        Timer에서는 refreshView를 호출한다
        근무자의 상태가 변경되면 addState를 호출한다
        강제로 History를 초기화시키고 싶으면 resetTrigger를 호출한다
        서버의 데이터로 덮어씌우려면 overWriteHistory를 호출한다
        출퇴근 시간을 설정하려면 setPunchTime을 호출한다
     */

    private final String historyFileName = "punch_history.srl";

    private final int colorWork;
    private final int colorBreak;
    private final int colorLeave;
    private final int colorCircle;

    private Paint mPaint;
    private RectF mRectF;
    private Context mContext;

    private Bitmap mImgPunchIn;
    private Bitmap mImgPunchOut;
    private Matrix mMatrix;

    private String punchInTime;
    private String punchOutTime;
    private String currentTime;
    private ArrayList<PunchHistoryItem> TodayHistory;

    private boolean resetSwitch = false;

    public PunchTracker(Context context, AttributeSet attrs){
        super(context, attrs);
        mContext = context;
        mPaint = new Paint();
        mRectF = new RectF();

        mImgPunchIn = BitmapFactory.decodeResource(getResources(), R.drawable.ic_flag_punch_in);
        mImgPunchOut = BitmapFactory.decodeResource(getResources(), R.drawable.ic_flag_punch_out);
        mMatrix = new Matrix();

        if (!loadHistory()) resetHistory(MainActivity.EMPLOYEE_STATE_LEAVE);

        colorWork = ContextCompat.getColor(mContext, R.color.IO_basic);
        colorBreak = ContextCompat.getColor(mContext, R.color.lightGray);
        colorLeave = ContextCompat.getColor(mContext, R.color.lightGray);
        colorCircle = ContextCompat.getColor(mContext, R.color.tLightGray);
    }

    private int timeToSecond(String time){
        int value = 0;
        /*
            HH:mm:ss 형식의 문자열을 초 단위의 숫자로.
            테스트를 위해 mm:ss를 사용.
         */

        try{
            String timeformat[] = time.split(":", 3);
            //value += Integer.parseInt(timeformat[0]) * 60 * 60;
            value += Integer.parseInt(timeformat[1]) * 60;
            value += Integer.parseInt(timeformat[2]);
        } catch(Exception e){
            e.printStackTrace();
            Log.d("Riemann", "잘못된 시간 형식");
        }

        return value;
    }

    private float secondToAngle(int second){
        float value = second * 360.0f / 60.0f / 60.0f + 90;
        //float value = second * 360.0f / 24.0f / 60.0f / 60.0f + 90;
        value = value > 360 ? value - 360 : value;
        return value;
    }


    private final float arcLocation = 0.8f;
    private final float arcWidth = 0.03f;
    private final float innerCircleR = 0.025f;
    private final float outerCircleR = 0.05f;
    private final float dotLocation = 0.7f;
    private final float dotR = 0.007f;
    private final float flagLocation = 0.9f;
    private final float flagSize = 0.06f;
    private final int dotCount = 24;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (currentTime == null) return;

        int viewSize = getWidth();

        mPaint.setStrokeWidth(viewSize * arcWidth);
        float rLeft = viewSize * (1 - arcLocation)  / 2;
        float rTop = viewSize * (1 - arcLocation) / 2;
        float rRight = viewSize * (1 + arcLocation) / 2;
        float rBottom = viewSize * (1 + arcLocation) / 2;
        mRectF.set(rLeft, rTop, rRight, rBottom);

        for (int i = 0; i < TodayHistory.size(); i++){
            PunchHistoryItem beginItem = TodayHistory.get(i);
            String sEndTime = i < TodayHistory.size() - 1 ?
                    TodayHistory.get(i + 1).getSTART_TIME() : currentTime;

            int startState = beginItem.getEMP_STATE();
            float startAngle = secondToAngle(timeToSecond(beginItem.getSTART_TIME()));
            float endAngle = secondToAngle(timeToSecond(sEndTime));
            float sweepAngle = endAngle - startAngle < 0 ? endAngle - startAngle + 360 :
                    endAngle - startAngle;

            setPaintColor(startState);

            float cX = viewSize * (1 + arcLocation * (float) Math.cos(Math.toRadians(startAngle))) / 2;
            float cY = viewSize * (1 + arcLocation * (float) Math.sin(Math.toRadians(startAngle))) / 2;
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawArc(mRectF, startAngle, sweepAngle, false, mPaint);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(cX, cY, viewSize * arcWidth / 2, mPaint);

            if (i == TodayHistory.size() - 1){
                float tcX = viewSize * (1 + arcLocation * (float) Math.cos(Math.toRadians(endAngle))) / 2;
                float tcY = viewSize * (1 + arcLocation * (float) Math.sin(Math.toRadians(endAngle))) / 2;
                mPaint.setColor(colorCircle);
                canvas.drawCircle(tcX, tcY, viewSize * outerCircleR, mPaint);
                setPaintColor(startState);
                canvas.drawCircle(tcX, tcY, viewSize * innerCircleR, mPaint);
            }
        }

        float inAngle = -1f;
        float outAngle = -1f;

        if (punchInTime != null && punchOutTime != null){
            inAngle = secondToAngle(timeToSecond(punchInTime));
            outAngle = secondToAngle(timeToSecond(punchOutTime));
            float fiX = viewSize * (1 + flagLocation * (float) Math.cos(Math.toRadians(inAngle))) / 2;
            float fiY = viewSize * (1 + flagLocation * (float) Math.sin(Math.toRadians(inAngle))) / 2;
            float foX = viewSize * (1 + flagLocation * (float) Math.cos(Math.toRadians(outAngle))) / 2;
            float foY = viewSize * (1 + flagLocation * (float) Math.sin(Math.toRadians(outAngle))) / 2;
            float siX = viewSize * flagSize / mImgPunchIn.getWidth();
            float siY = viewSize * flagSize / mImgPunchIn.getHeight();
            float soX = viewSize * flagSize / mImgPunchOut.getWidth();
            float soY = viewSize * flagSize / mImgPunchOut.getHeight();

            mMatrix.reset();
            mMatrix.postTranslate(-mImgPunchIn.getWidth()/2, -mImgPunchIn.getHeight()/2);
            mMatrix.postScale(siX, siY);
            mMatrix.postRotate(inAngle + 90.0f);
            mMatrix.postTranslate(fiX, fiY);
            canvas.drawBitmap(mImgPunchIn, mMatrix, mPaint);

            mMatrix.reset();
            mMatrix.postTranslate(-mImgPunchOut.getWidth()/2, -mImgPunchOut.getHeight()/2);
            mMatrix.postScale(soX, soY);
            mMatrix.postRotate(outAngle + 90.0f);
            mMatrix.postTranslate(foX, foY);
            canvas.drawBitmap(mImgPunchOut, mMatrix, mPaint);
        }

        for(int angle = 0; angle < 360; angle += 360 / dotCount){
            float dX = viewSize * (1 + dotLocation * (float) Math.cos(Math.toRadians(angle))) / 2;
            float dY = viewSize * (1 + dotLocation * (float) Math.sin(Math.toRadians(angle))) / 2;

            if (angle >= inAngle && angle < outAngle){
                mPaint.setColor(colorWork);
                canvas.drawCircle(dX, dY, viewSize * dotR, mPaint);
            } else{
                mPaint.setColor(colorLeave);
                canvas.drawCircle(dX, dY, viewSize * dotR, mPaint);
            }
        }
    }

    private void setPaintColor(int employeeState){
        switch (employeeState){
            case MainActivity.EMPLOYEE_STATE_WORK:
                mPaint.setColor(colorWork);
                break;
            case MainActivity.EMPLOYEE_STATE_BREAK:
                mPaint.setColor(colorBreak);
                break;
            case MainActivity.EMPLOYEE_STATE_LEAVE:
                mPaint.setColor(colorLeave);
                break;
        }
    }


    public void refreshView(String currentTime){
        //view를 새로 그린다. 하루가 지난 경우 TodayHistory를 리셋한다.
        //현재는 이전 날의 마지막 상태를 유지한다. 바꾸려면 resetHistory 부분을 바꾸면 된다.
        PunchHistoryItem lastItem = TodayHistory.get(TodayHistory.size() - 1);
        int last = timeToSecond(lastItem.getSTART_TIME());
        int now = timeToSecond(currentTime);

        if (last > now || resetSwitch){
            resetHistory(lastItem.getEMP_STATE());
        }

        Log.d("Riemann", "refreshView Called");
        this.currentTime = currentTime;
        invalidate();
    }

    private void resetHistory(int employeeState){
        if (TodayHistory == null) {
            TodayHistory = new ArrayList<>();
        } else{
            TodayHistory.clear();
        }
        resetSwitch = false;

        PunchHistoryItem firstItem = new PunchHistoryItem();
        firstItem.setSTART_TIME("00:00:00");
        firstItem.setEMP_STATE(employeeState);
        TodayHistory.add(firstItem);
    }


    public void resetTrigger(){
        //Timer가 Tick될 때 TodayHistory를 강제로 리셋하도록 한다.
        resetSwitch = true;
    }

    public void addState(String currentTime, int employeeState){
        //출퇴근 상태가 바뀌었을 때 TodayHistory에 기록한다.
        PunchTracker.PunchHistoryItem item = new PunchTracker.PunchHistoryItem();
        item.setSTART_TIME(currentTime);
        item.setEMP_STATE(employeeState);
        TodayHistory.add(item);
        saveHistory();
    }

    public void overWriteHistory(ArrayList<PunchHistoryItem> history){
        //서버에서 얻어온 History를 덮어쓴다.
        TodayHistory = history;
        saveHistory();
    }

    public void setPunchTime(String punchInTime, String punchOutTime){
        this.punchInTime = punchInTime;
        this.punchOutTime = punchOutTime;
    }


    private boolean saveHistory() {
        try {
            FileOutputStream fos = mContext.openFileOutput(historyFileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(TodayHistory);
            os.close();
            fos.close();
            return true;
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean loadHistory(){
        try{
            FileInputStream fis = mContext.openFileInput(historyFileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            TodayHistory = (ArrayList<PunchHistoryItem>) is.readObject();
            is.close();
            fis.close();
            return true;
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
