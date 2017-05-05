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

public class RegistrationActivity extends AppCompatActivity {

    static final String PHONE_NUMBER = "phone_number";

    private String AlreadyRegistrationMember;
    private String AlreadyRegistrationMemberMove;
    private String RegistrationFromCompanyNotYet;
    private String PhoneNumberInputError;
    private String NoUSIMState;

    EditText TxtPhoneNumber;
    ImageButton FabNext;

    View.OnClickListener fabListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String phoneNumberInput = TxtPhoneNumber.getText().toString();
            String getPhoneNumberInput = getPhoneNumber(RegistrationActivity.this);
            Log.d("핸드폰 번호 왜이뤱", phoneNumberInput);
            Log.d("뭐여이거", getPhoneNumberInput);

            if (!phoneNumberInput.equals(getPhoneNumberInput)) {
                /*
                    핸드폰 번호 불일치, Snackbar 띄우기
                 */
                TxtPhoneNumber.setError(PhoneNumberInputError);
                Snackbar.make(view, PhoneNumberInputError, Snackbar.LENGTH_LONG).show();
            } else if (!isAlreadyRegToCompany(phoneNumberInput)) {
                /*
                    핸드폰 번호가 회사에 등록되지 않은 계정
                    사업체에 사업장 관리자에게 등록 요청하는 Snackbar
                 */
                TxtPhoneNumber.setError(RegistrationFromCompanyNotYet);
                Snackbar.make(view, RegistrationFromCompanyNotYet, Snackbar.LENGTH_LONG).show();
            } else if (isAlreadyReg(phoneNumberInput)) {
                /*
                    이미 회원가입된 번호.
                    로그인 화면으로 이동시켜주는 Snackbar action
                 */
                TxtPhoneNumber.setError(AlreadyRegistrationMember);
                Snackbar.make(view, AlreadyRegistrationMember, Snackbar.LENGTH_LONG).setAction(AlreadyRegistrationMemberMove, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }).show();
            } else {
                /*
                    비밀번호 설정 Activity로 넘어가는 Case
                    intent로 현재 Activity에서 구한 Phone Number도 같이 넘겨준다.
                    이를 통해 다음 Activity에서 등록을 완료함.
                 */
                Intent intent = new Intent(RegistrationActivity.this, RegistrationPasswordActivity.class);
                intent.putExtra(PHONE_NUMBER, phoneNumberInput);
                startActivity(intent);
                finish();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        AlreadyRegistrationMember = getString(R.string.activity_registration_already_registered);
        AlreadyRegistrationMemberMove = getString(R.string.activity_registration_to_login);
        RegistrationFromCompanyNotYet = getString(R.string.activity_registration_unregistered_number);
        PhoneNumberInputError = getString(R.string.activity_registration_wrong_number);
        NoUSIMState = getString(R.string.activity_registration_no_usim);

        //View 설정
        ImageButton btn_back = (ImageButton) findViewById(R.id.activity_registration_btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FabNext = (ImageButton) findViewById(R.id.activity_registration_btn_next);
        TxtPhoneNumber = (EditText) findViewById(R.id.activity_registration_txt_phone);

        fabSetting();
        txtSetting();
    }

    private void fabSetting(){
        /*
        if(hasPhoneUSIM(RegistrationActivity.this)) {
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


    public boolean isAlreadyReg(String phoneNumber) {
        boolean flag = false;

        /*
            DB에서 검사해서 등록된 핸드폰 번호라면 true 리턴
         */
        //flag = true;

        return flag;
    }

    public boolean isAlreadyRegToCompany(String phoneNumber) {
        boolean flag = false;

        /*
            DB에서 검사해서 회사의 전화번호 리스트에 등록이 되었는지 확인.
         */
        flag = true;

        return flag;
    }

}
