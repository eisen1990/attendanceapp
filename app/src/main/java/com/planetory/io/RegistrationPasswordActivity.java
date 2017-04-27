package com.planetory.io;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationPasswordActivity extends AppCompatActivity {

    private final String PasswordError = "입력한 비밀번호가 서로 다릅니다.";
    private final String RegistrationComplete = "회원가입이 완료되었습니다!";
    private final String RegistrationFail = "회원가입에 실패하였습니다. 다시 시도해주세요.";

    EditText passwordInputText;
    EditText passwordInputConfirmText;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_password);

        final Intent intent_data = getIntent();

        passwordInputText = (EditText) findViewById(R.id.activity_registration_password_password);
        passwordInputConfirmText = (EditText) findViewById(R.id.activity_registration_password_confirm);

        fab = (FloatingActionButton) findViewById(R.id.activity_registration_password_fab_next);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String passwordInput = passwordInputText.getText().toString();
                String passwordInputConfirm = passwordInputConfirmText.getText().toString();
                String tempPhoneNumber = intent_data.getExtras().getString("phoneNumber");

                if (passwordInput.equals(passwordInputConfirm)) {
                    /*
                        password 일치하는 case
                        메인 페이지로 이동한다. 근무자 그룹에 따라 UI 분기 일단 MainActivity
                        회원 정보를 DB에 저장하는 루틴 필요함
                     */

                    if( RegistrationOnServer( tempPhoneNumber, passwordInput) ) {
                        Toast.makeText(RegistrationPasswordActivity.this, RegistrationComplete, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegistrationPasswordActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegistrationPasswordActivity.this, RegistrationFail, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Snackbar.make(view, PasswordError, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    public boolean RegistrationOnServer(String phoneNumber, String password) {
        boolean flag = false;
        /*
            값이 성공적으로 바뀌면 return true, 아닐 경우 false
            성공했다 치고
         */
        flag = true;

        return flag;
    }

}
