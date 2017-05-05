package com.planetory.io;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    /*
        첫 로드 화면 뒤에 로그인 버튼 누르면
        실행되는 Activity.
     */

    private final String LoginError = "로그인에 실패하였습니다.";

    EditText TxtphoneNumber;
    EditText Txtpassword;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TxtphoneNumber = (EditText) findViewById(R.id.activity_login_phone_number);
        Txtpassword = (EditText) findViewById(R.id.activity_login_password);

        fab = (FloatingActionButton) findViewById(R.id.activity_login_fab_next);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = TxtphoneNumber.getText().toString();
                String password = Txtpassword.getText().toString();

                if(LoginCheck(phone, password)) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Snackbar.make(view, LoginError, Snackbar.LENGTH_LONG).show();
                }

            }
        });
    }

    public boolean LoginCheck(String phone, String password) {
        boolean flag = true;

        /*
            Login 루틴 나중에 Case 별로 분리
         */

        return flag;
    }

}
