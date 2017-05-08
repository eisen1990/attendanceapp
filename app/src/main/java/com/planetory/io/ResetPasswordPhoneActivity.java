package com.planetory.io;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class ResetPasswordPhoneActivity extends AppCompatActivity {

    static final String PHONE_NUMBER = "phone_number";

    private String UnregisteredNumber;
    private String PhoneNumberInputError;
    private String NoUSIMState;

    EditText TxtPhoneNumber;
    ImageButton FabNext;

    View.OnClickListener fabListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String phoneNumberInput = TxtPhoneNumber.getText().toString();
            String getPhoneNumberInput = getPhoneNumber(ResetPasswordPhoneActivity.this);
            Log.d("Riemann", getPhoneNumberInput);

            if (!phoneNumberInput.equals(getPhoneNumberInput)) {
                /*
                    핸드폰 번호 불일치, Snackbar 띄우기
                 */
                TxtPhoneNumber.setError(PhoneNumberInputError);
                Snackbar.make(view, PhoneNumberInputError, Snackbar.LENGTH_LONG).show();
            } else if (!isRegistered(phoneNumberInput)) {
                /*
                    회원가입되어 있지 않은 번호.
                 */
                TxtPhoneNumber.setError(UnregisteredNumber);
                Snackbar.make(view, UnregisteredNumber, Snackbar.LENGTH_LONG).show();
            } else {
                /*
                    비밀번호 재설정 Activity로 넘어가는 Case
                    intent로 현재 Activity에서 구한 Phone Number도 같이 넘겨준다.
                 */
                Intent intent = new Intent(ResetPasswordPhoneActivity.this, ResetPasswordActivity.class);
                intent.putExtra(PHONE_NUMBER, phoneNumberInput);
                startActivity(intent);
                finish();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_phone);

        UnregisteredNumber = getString(R.string.activity_reset_pwd_phone_unregistered_number);
        PhoneNumberInputError = getString(R.string.activity_reset_pwd_phone_wrong_number);
        NoUSIMState = getString(R.string.activity_reset_pwd_phone_no_usim);

        //View 설정
        ImageButton btn_back = (ImageButton) findViewById(R.id.activity_reset_pwd_phone_btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FabNext = (ImageButton) findViewById(R.id.activity_reset_pwd_phone_btn_next);
        TxtPhoneNumber = (EditText) findViewById(R.id.activity_reset_pwd_phone_txt_phone);

        fabSetting();
        txtSetting();
    }

    private void fabSetting(){
        /*
        if(hasPhoneUSIM(ResetPasswordPhoneActivity.this)) {
            View v = this.getWindow().getDecorView();
            Snackbar.make(v, NoUSIMState, Snackbar.LENGTH_LONG).show();
            FabNext.setEnabled(false);
        }
        */
        FabNext.setOnClickListener(fabListener);
        FabNext.setEnabled(false);
    }

    private void txtSetting(){
        TxtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() >= 8){
                    FabNext.setEnabled(true);
                } else{
                    FabNext.setEnabled(false);
                }
            }
        });
        //phoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }


    public String getPhoneNumber(Context context) {
        String phoneNumber = "";
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try {
            if (telephonyManager.getLine1Number() != null) {
                phoneNumber = telephonyManager.getLine1Number();
            }
//            phoneNumber = phoneNumber.substring(phoneNumber.length() - 10, phoneNumber.length());
//            phoneNumber = "0" + phoneNumber;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return phoneNumber;
    }

    public boolean hasPhoneUSIM(Context context) {
        boolean flag = true;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if(telephonyManager.getSimState() != TelephonyManager.SIM_STATE_READY) {
            flag = false;
        }
        return flag;
    }


    public boolean isRegistered(String phoneNumber) {
        boolean flag = false;

        /*
            DB에서 검사해서 등록된 핸드폰 번호라면 true 리턴
         */
        flag = true;

        return flag;
    }
}
