package com.planetory.io;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.READ_PHONE_STATE;

public class LoadActivity extends AppCompatActivity{

    private BackPressCloseHandler backPressCloseHandler;
    SharedPreferences LOGIN_PREF;
    static final String LOGIN_PREFERENCE = "login_pref";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        backPressCloseHandler = new BackPressCloseHandler(this);

        requestPermit();
        updateCheck();
    }


    private static final int REQUEST_PERMISSION = 0;

    protected boolean requestPermit(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }
        if (checkSelfPermission(READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{READ_PHONE_STATE}, REQUEST_PERMISSION);
            return false;
        }
        if (checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;

        if (requestCode == REQUEST_PERMISSION) {
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    //사용자가 권한을 거절
                    showPermissionRationale();
                    if (!shouldShowRequestPermissionRationale(permission)) {
                        //사용자가 권한을 완전히 거절
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse("package:com.planetory.io"));
                        startActivity(intent);
                        finish();
                        return;
                    }
                }
            }
            //권한을 다시 요청
            requestPermit();
        }
    }

    private void showPermissionRationale(){
        //사용자가 권한을 거부했을 때 띄워줄 메세지
        /*
            현재는 권한을 거부할 경우 즉각적으로 권한을 다시 요청하고 있지만,
            다음 액티비티로 넘어갈 때 확인해 넘어가지 못하게만 하는 방법도 있음.
         */
        Toast.makeText(this, "권한을 허용하지 않으면 애플리케이션의 정상적인 이용이 불가능합니다.", Toast.LENGTH_SHORT).show();
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
                            finish();
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
        LOGIN_PREF = getSharedPreferences(LOGIN_PREFERENCE, MODE_PRIVATE);
        if(LOGIN_PREF.contains(LOGIN_PREFERENCE)) {
//            Log.d("LoadActivity : ", LOGIN_PREF.getString(LOGIN_PREFERENCE, "shared에 자료 있음"));
            return true;
        }
        else {
//            Log.d("LoadActivity : ", LOGIN_PREF.getString(LOGIN_PREFERENCE, "shared에 자료 없음"));
            return false;
        }
    }


    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }
}
