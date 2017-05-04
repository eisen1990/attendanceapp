package com.planetory.io;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

public class LoadActivity extends AppCompatActivity{

    private BackPressCloseHandler backPressCloseHandler;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        backPressCloseHandler = new BackPressCloseHandler(this);

        updateCheck();
    }

    private void updateCheck(){
        if (isAppUpadated()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.activity_load_update_dialog_title)
                    .setMessage(R.string.activity_load_update_dialog_message)
                    .setPositiveButton(R.string.activity_load_update_dialog_positive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //업데이트 받기에 동의한 경우 앱스토어로 연결. 일단은 무난한 메모장 앱 페이지로.
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=org.korosoft.simplenotepad.android")));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=org.korosoft.simplenotepad.android")));
                            }
                        }
                    })
                    .setNegativeButton(R.string.activity_load_update_dialog_negative, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private boolean isAppUpadated(){
        //업데이트 됐는지 확인하는 알고리즘이 들어갈 부분.
        return true;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //이 액티비티에서 자료 등을 로딩?
        //일단 다음화면으로 넘어가도록
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            toNextActivity();
        }

        return true;
    }

    private void toNextActivity(){
        Intent intent = isLoggedIn() ? new Intent(LoadActivity.this, MainActivity.class) :
                new Intent(LoadActivity.this, StartActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isLoggedIn(){
        //로그인 상태인지 확인하는 알고리즘이 들어갈 부분
        return false;
    }


    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }



}
