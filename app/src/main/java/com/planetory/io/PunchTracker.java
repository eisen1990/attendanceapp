package com.planetory.io;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class PunchTracker extends View{

    private class HistoryData {
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


    private final String historyFileName = "punch_history";
    private final float viewSize;
    private final float flagLocation = 0.95f;
    private final float arcLocation = 0.9f;
    private final float arcWidth = 0.05f;
    private final float circleLocation = 0.8f;
    private final float circleR = 0.03f;

    Paint mPaint;
    Context mContext;

    private ArrayList<HistoryData> TodayHistory;

    public PunchTracker(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mPaint = new Paint();

        viewSize = (getResources().getDimension(R.dimen.punch_tracker_size));
    }


    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
    }


    private boolean saveHistory(){
        try{
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
            TodayHistory = (ArrayList<HistoryData>) is.readObject();
            is.close();
            fis.close();
            return true;
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
