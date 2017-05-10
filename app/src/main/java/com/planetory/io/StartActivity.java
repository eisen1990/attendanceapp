package com.planetory.io;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {
    /*
        Load Activity 다음으로 로그인과 회원가입을 결정하는 Activity
     */

    private SpannableString STRING_INFO;
    private int PARSE_TERMS_START;
    private int PARSE_TERMS_END;
    private int PARSE_PERSONAL_START;
    private int PARSE_PERSONAL_END;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        STRING_INFO = new SpannableString(getString(R.string.activity_start_info));
        PARSE_TERMS_START = getResources().getInteger(R.integer.PARSE_TERMS_START);
        PARSE_TERMS_END = getResources().getInteger(R.integer.PARSE_TERMS_END);
        PARSE_PERSONAL_START = getResources().getInteger(R.integer.PARSE_PERSONAL_START);
        PARSE_PERSONAL_END = getResources().getInteger(R.integer.PARSE_PERSONAL_END);

        setInfoText();
        setButtons();
    }

    private void setInfoText(){
        TextView txt_info = (TextView) findViewById(R.id.activity_start_txt_info);

        ClickableSpan terms_span = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(getApplicationContext(), InfoTermsActivity.class);
                startActivity(intent);
            }
        };

        ClickableSpan personal_span = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(getApplicationContext(), InfoPersonalActivity.class);
                startActivity(intent);
            }
        };

        STRING_INFO.setSpan(terms_span, PARSE_TERMS_START, PARSE_TERMS_END, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        STRING_INFO.setSpan(personal_span, PARSE_PERSONAL_START, PARSE_PERSONAL_END, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        txt_info.setText(STRING_INFO);
        txt_info.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setButtons(){
        Button btn_login = (Button) findViewById(R.id.activity_start_btn_login);
        Button btn_register = (Button) findViewById(R.id.activity_start_btn_signup);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
