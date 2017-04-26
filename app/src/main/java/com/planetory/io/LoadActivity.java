package com.planetory.io;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

public class LoadActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //이 액티비티에서 자료 등을 로딩?
        //일단 다음화면으로 넘어가도록
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Intent intent = new Intent(this, StartActivity.class);
            startActivity(intent);
            finish();
        }

        return true;
    }
}
