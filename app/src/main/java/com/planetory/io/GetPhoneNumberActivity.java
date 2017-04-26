package com.planetory.io;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class GetPhoneNumberActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_phone_number);

        ImageButton btn_back = (ImageButton) findViewById(R.id.activity_get_number_btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        /*
            현재 Floating Action Button은 Main activity로 넘어가게만 해둠
         */

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.activity_get_number_fab_next);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GetPhoneNumberActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
