package com.planetory.io;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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
                //DB에 없는 휴대폰 번호
                TxtphoneNumber.setError(UnregisteredNumber);
                Snackbar.make(view, UnregisteredNumber, Snackbar.LENGTH_LONG).show();
            } else if(!isLoginValid(phone, password)) {
                //휴대폰 번호는 있으나 비밀번호가 잘못된 경우
                Txtpassword.setError(WrongPassword);
                Snackbar.make(view, WrongPassword, Snackbar.LENGTH_LONG).show();
            } else{
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
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

        TextView link_reset = (TextView) findViewById(R.id.activity_login_reset_password);
        link_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordPhoneActivity.class);
                startActivity(intent);
            }
        });

        FabNext = (ImageButton) findViewById(R.id.activity_login_btn_next);
        TxtphoneNumber = (EditText) findViewById(R.id.activity_login_txt_phone);
        Txtpassword = (EditText) findViewById(R.id.activity_login_txt_password);

        fabSetting();
        txtSetting();
    }

    private void fabSetting(){
        FabNext.setOnClickListener(fabListener);
        FabNext.setEnabled(false);
    }

    private void txtSetting(){
        TxtphoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() >= 8 && Txtpassword.getText().length() >= 1){
                    FabNext.setEnabled(true);
                } else{
                    FabNext.setEnabled(false);
                }
            }
        });

        Txtpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() >= 1 && TxtphoneNumber.getText().length() >= 8){
                    FabNext.setEnabled(true);
                } else{
                    FabNext.setEnabled(false);
                }
            }
        });
    }


    public boolean isPhoneValid(String phone){
        boolean flag = true;

        /*
            한 루틴에서 세 결과를 내보낼지 두 루틴으로 분리할지 선택.
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
