package com.planetory.io;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {
    /*
        Load Activity 다음으로 로그인과 회원가입을 결정하는 Activity
     */

    private static final SpannableString STRING_INFO = new SpannableString("회원가입을 하면 IO의 서비스 약관, 개인정보 취급방침에 동의하는 것으로 간주됩니다.");
    private static final int PARSE_TERMS_START = 13;
    private static final int PARSE_TERMS_END = 19;
    private static final int PARSE_PERSONAL_START = 21;
    private static final int PARSE_PERSONAL_END = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        setInfoText();
        setButtons();
    }

    private void setButtons(){
        ImageButton btn_login = (ImageButton) findViewById(R.id.activity_start_btn_login);
        ImageButton btn_register = (ImageButton) findViewById(R.id.activity_start_btn_register);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, GetPhoneNumberActivity.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
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
}
