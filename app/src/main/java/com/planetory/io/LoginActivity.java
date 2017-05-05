package com.planetory.io;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class LoginActivity extends AppCompatActivity {
    /*
        첫 로드 화면 뒤에 로그인 버튼 누르면
        실행되는 Activity.
     */

    private String UnregisteredNumber;
    private String WrongPassword;

    EditText TxtphoneNumber;
    EditText Txtpassword;
    ImageButton FabNext;

    View.OnClickListener fabListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String phone = TxtphoneNumber.getText().toString();
            String password = Txtpassword.getText().toString();

            if(!isPhoneValid(phone)) {
                Snackbar.make(view, UnregisteredNumber, Snackbar.LENGTH_LONG).show();
            } else if(isLoginValid(phone, password)) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            } else{
                Snackbar.make(view, WrongPassword, Snackbar.LENGTH_LONG).show();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UnregisteredNumber = getString(R.string.activity_login_unregistered_number);
        WrongPassword = getString(R.string.activity_login_wrong_password);

        //View 설정
        ImageButton btn_back = (ImageButton) findViewById(R.id.activity_login_btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FabNext = (ImageButton) findViewById(R.id.activity_login_btn_next);
        TxtphoneNumber = (EditText) findViewById(R.id.activity_login_txt_phone);
        Txtpassword = (EditText) findViewById(R.id.activity_login_password);

        fabSetting();
        txtSetting();
    }

    private void fabSetting(){
        FabNext.setOnClickListener(fabListener);
        FabNext.setEnabled(false);
    }

    private void txtSetting(){

    }


    public boolean isPhoneValid(String phone){
        boolean flag = true;

        /*
            한 루틴에서 세 결과를 내보낼지 두 루틴으로 분리할지 선택.
            가능할 경우 true, 잘못될 경우 false;
         */

        return flag;
    }

    public boolean isLoginValid(String phone, String password) {
        boolean flag = true;

        /*
            Login 루틴 나중에 Case 별로 분리
         */

        return flag;
    }

}
