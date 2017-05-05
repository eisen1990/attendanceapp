package com.planetory.io;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class RegistrationPasswordActivity extends AppCompatActivity {

    private String PasswordError;
    private String RegistrationComplete;
    private String RegistrationFail;

    EditText TxtpasswordInput;
    EditText TxtpasswordInputConfirm;
    ImageButton FabNext;

    View.OnClickListener fabListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String passwordInput = TxtpasswordInput.getText().toString();
            String passwordInputConfirm = TxtpasswordInputConfirm.getText().toString();
            String tempPhoneNumber = getIntent().getExtras().getString(RegistrationActivity.PHONE_NUMBER);

            if (passwordInput.equals(passwordInputConfirm)) {
                /*
                    password 일치하는 case
                    메인 페이지로 이동한다. 근무자 그룹에 따라 UI 분기 일단 MainActivity
                    회원 정보를 DB에 저장하는 루틴 필요함
                 */

                if( RegistrationOnServer( tempPhoneNumber, passwordInput) ) {
                    Toast.makeText(RegistrationPasswordActivity.this, RegistrationComplete, Toast.LENGTH_SHORT).show();
                    Log.d("riemann", tempPhoneNumber);
                    Log.d("riemann", passwordInput);
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
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_password);

        PasswordError = getString(R.string.activity_registration_password_wrong_confirm);
        RegistrationComplete = getString(R.string.activity_registration_password_reg_complete);
        RegistrationFail = getString(R.string.activity_registration_password_reg_fail);

        //View 설정
        ImageButton btn_back = (ImageButton) findViewById(R.id.activity_registration_password_btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationPasswordActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        FabNext = (ImageButton) findViewById(R.id.activity_registration_password_btn_next);
        TxtpasswordInput = (EditText) findViewById(R.id.activity_registration_password_txt_pwd);
        TxtpasswordInputConfirm = (EditText) findViewById(R.id.activity_registration_password_txt_confirm);

        fabSetting();
        txtSetting();
    }

    private void fabSetting(){
        FabNext.setOnClickListener(fabListener);
        FabNext.setEnabled(false);
    }

    private void txtSetting(){
        TxtpasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() >= 6){
                    FabNext.setEnabled(true);
                } else{
                    FabNext.setEnabled(false);
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
