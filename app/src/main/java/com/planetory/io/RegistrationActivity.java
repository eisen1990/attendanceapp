package com.planetory.io;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class RegistrationActivity extends AppCompatActivity {

    private final String AlreadyRegistrationMember = "이미 회원 가입된 번호입니다. 로그인을 해보세요.";
    private final String AlreadyRegistrationMemberMove = "이동";
    private final String RegistrationFromCompanyNotYet = "io 사용 사업장에 등록되지 않은 번호입니다. 사업장 관리자에게 등록을 요청해 보세요";
    private final String PhoneNumberInputError = "현재 사용하고 계신 단말에서 실제 사용 중인 번호를 입력해 주세요";
    private final String NoUSIMState = "USIM이 없는 경우";

    EditText phoneNumberText;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        ImageButton btn_back = (ImageButton) findViewById(R.id.activity_registration_back_btn);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        phoneNumberText = (EditText) findViewById(R.id.activity_registration_phone_number);
//        phoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        fab = (FloatingActionButton) findViewById(R.id.activity_registration_fab_next);
        /*
        if(hasPhoneUSIM(RegistrationActivity.this)) {
            View v = this.getWindow().getDecorView();
            Snackbar.make(v, NoUSIMState, Snackbar.LENGTH_LONG).show();
            fab.setEnabled(false);
        }
        */

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumberInput = phoneNumberText.getText().toString();
                String getPhoneNumberInput = getPhoneNumber(RegistrationActivity.this);
                Log.d("핸드폰 번호 왜이뤱", phoneNumberInput);
                Log.d("뭐여이거", getPhoneNumberInput);

                if (phoneNumberInput.equals(getPhoneNumberInput)) {
                    /*
                        입력한 핸드폰 번호가 USIM에서 가지고온 번호와 일치하는 Case
                     */
                    if(isAlreadyRegToCompany(phoneNumberInput)) {
                        /*
                            핸드폰 번호가 회사에 등록이 되어있는 Case
                            이미 회원인 경우 Login Activity로 유도하는 Snackbar
                            비밀번호 설정 Activity로 넘어간다.
                         */
                        if(isAlreadyReg(phoneNumberInput)) {
                            Snackbar.make(view, AlreadyRegistrationMember , Snackbar.LENGTH_LONG).setAction(AlreadyRegistrationMemberMove, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    /*
                                        로그인 화면으로 이동시켜주는 Snackbar action
                                     */
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
                            intent.putExtra("phonenNumber",phoneNumberInput);
                            startActivity(intent);
                        }
                    } else {
                        /*
                            핸드폰 번호가 회사에 등록되지 않은 계정
                            사업체에 사업장 관리자에게 등록 요청하는 Snackbar
                         */
                        Snackbar.make(view, RegistrationFromCompanyNotYet, Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    /*
                        핸드폰 번호 불일치, Snackbar 띄우기
                     */
                    Snackbar.make(view, PhoneNumberInputError, Snackbar.LENGTH_LONG).show();
                }
            }
        });
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
        flag = true;

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
