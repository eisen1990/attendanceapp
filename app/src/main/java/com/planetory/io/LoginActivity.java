package com.planetory.io;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    /*
        첫 로드 화면 뒤에 로그인 버튼 누르면
        실행되는 Activity.
     */
    private URLTask urlTask = null;

    private String UnregisteredNumber;
    private String WrongPassword;
    private String ServerError;

    EditText TxtphoneNumber;
    EditText Txtpassword;
    ImageButton FabNext;

    View.OnClickListener fabListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String phone = TxtphoneNumber.getText().toString();
            String password = Txtpassword.getText().toString();

            urlTask = new URLTask(phone, password);
            urlTask.execute();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UnregisteredNumber = getString(R.string.activity_login_unregistered_number);
        WrongPassword = getString(R.string.activity_login_wrong_password);
        ServerError = getString(R.string.activity_login_server_error);

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

    private void fabSetting() {
        FabNext.setOnClickListener(fabListener);
        FabNext.setEnabled(false);
    }

    private void txtSetting() {
        TxtphoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() >= 8 && Txtpassword.getText().length() >= 1) {
                    FabNext.setEnabled(true);
                } else {
                    FabNext.setEnabled(false);
                }
            }
        });

        Txtpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() >= 1 && TxtphoneNumber.getText().length() >= 8) {
                    FabNext.setEnabled(true);
                } else {
                    FabNext.setEnabled(false);
                }
            }
        });
    }

    private class URLTask extends AsyncTask<Void, Void, String> {

        private String login_phone;
        private String login_password;

        public URLTask(String login_phone, String login_password) {
            this.login_phone = login_phone;
            this.login_password = login_password;
        }

        @Override
        protected void onPostExecute(String s) {
            urlTask = null;
            if (s == null) s = RestURL.NULL_STRING;
            Log.d("eisen", s);
            try {
                s = s.substring(0, s.length() - 1);
                Log.d("eisen1", s);

                if (s.equals(RestURL.LOGIN_SUCCESS)) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                /*
                    로그인 정보를 Main activity에 전달해야된다.
                 */
                    intent.putExtra(MainActivity.INTENT_USER_PHONE, login_phone);
                    intent.putExtra(MainActivity.INTENT_USER_PASSWORD, login_password);
                    startActivity(intent);
                    finish();
                } else if (s.equals(RestURL.LOGIN_WRONG_PASSWORD)) {
                    Log.d("eisen", "Password fail");
                    Txtpassword.setError(WrongPassword);
                    Snackbar.make(getCurrentFocus(), WrongPassword, Snackbar.LENGTH_LONG).show();
                } else if (s.equals(RestURL.LOGIN_WRONG_ID)) {
                    Log.d("eisen", "Unregistered user");
                    TxtphoneNumber.setError(UnregisteredNumber);
                    Snackbar.make(getCurrentFocus(), UnregisteredNumber, Snackbar.LENGTH_LONG).show();
                } else if (s.equals(RestURL.NULL_STRING)) {
                    Log.d("eisen", "Server Error");
                    Toast.makeText(LoginActivity.this, ServerError, Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("eisen", "post error");
                    Toast.makeText(LoginActivity.this, "unknown error", Toast.LENGTH_SHORT).show();
                }
//                Log.d("eisen", "Login 액티비티 오류");
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(LoginActivity.this, ServerError, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            urlTask = null;
        }

        @Override
        protected String doInBackground(Void... params) {
            String urlString = RestURL.LOGINUSER_URL + "phone=" + login_phone + "&password=" + login_password;
            Log.d("Login URL : ", urlString);
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
                        output.append("\n");
                    }
                }
                return output.toString();

            } catch (Exception e) {
                Log.d("Login Fail", "URL exception");
            }

            return RestURL.NULL_STRING + "\0";
        }
    }
}
