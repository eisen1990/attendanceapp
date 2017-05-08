package com.planetory.io;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.EditText;
import android.widget.ImageButton;

public class ResetPasswordPhoneActivity extends AppCompatActivity {

    static final String PHONE_NUMBER = "phone_number";

    EditText TxtPhoneNumber;
    ImageButton FabNext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_phone);
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
}
