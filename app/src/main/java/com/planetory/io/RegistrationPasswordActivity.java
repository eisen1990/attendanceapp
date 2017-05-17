package com.planetory.io;

import android.content.Intent;
import android.os.AsyncTask;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class RegistrationPasswordActivity extends AppCompatActivity {

    private URLTask urlTask = null;

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
                urlTask = new URLTask(tempPhoneNumber, passwordInput);
                urlTask.execute();

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

    private void fabSetting() {
        FabNext.setOnClickListener(fabListener);
        FabNext.setEnabled(false);
    }

    private void txtSetting() {
        TxtpasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() >= 6 && TxtpasswordInputConfirm.getText().length() >= 6) {
                    FabNext.setEnabled(true);
                } else {
                    FabNext.setEnabled(false);
                }
            }
        });

        TxtpasswordInputConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() >= 6 && TxtpasswordInput.getText().length() >= 6) {
                    FabNext.setEnabled(true);
                } else {
                    FabNext.setEnabled(false);
                }
            }
        });
    }

    private class URLTask extends AsyncTask<Void, Void, String> {

        private String reg_phone;
        private String reg_password;

        URLTask(String reg_phone, String reg_password) {
            this.reg_phone = reg_phone;
            this.reg_password = reg_password;
        }

        @Override
        protected void onPostExecute(String s) {
            urlTask = null;
            if (s == null) s = RestURL.NULL_STRING + "\0";
            Log.d("eisen", s);
            s = s.substring(0, s.length() - 1);

            if (s.equals(RestURL.REGISTER_SUCCESS)) {
                Toast.makeText(RegistrationPasswordActivity.this, RegistrationComplete, Toast.LENGTH_SHORT).show();
                Log.d("Riemann", reg_phone);
                Log.d("Riemann", reg_password);
                Intent intent = new Intent(RegistrationPasswordActivity.this, MainActivity.class);
                /*
                    회원가입 성공 시 로그인 계정 정보를 같이 넘겨서 자동으로 로그인되게 만들어야됨.
                 */
                intent.putExtra(MainActivity.INTENT_USER_PHONE, reg_phone);
                intent.putExtra(MainActivity.INTENT_USER_PASSWORD, reg_password);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(RegistrationPasswordActivity.this, RegistrationFail, Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onCancelled() {
            urlTask = null;
        }

        @Override
        protected String doInBackground(Void... params) {

            String urlString = RestURL.ADDUSER_URL + "phone=" + reg_phone + "&password=" + reg_password;

            StringBuilder output = new StringBuilder();

            try {
                URL url = new URL(urlString);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setRequestMethod("GET");

                int resCode = httpURLConnection.getResponseCode();
                if (resCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        output.append(line);
                        output.append('\n');
                    }
                }

                return output.toString();

            } catch (Exception e) {
                Log.d("Registration Fail", "URL exception");
            }

            return RestURL.NULL_STRING + "\0";
        }
    }
}
