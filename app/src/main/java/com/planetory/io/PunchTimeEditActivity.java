package com.planetory.io;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class PunchTimeEditActivity extends AppCompatActivity {

    private String user_phone;

    ImageButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user_phone = getIntent().getExtras().getString("specific_phone");
        setContentView(R.layout.activity_punch_time_edit);

        ImageButton btn_back = (ImageButton) findViewById(R.id.activity_punch_time_edit_back_btn);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fab = (ImageButton) findViewById(R.id.activity_punch_time_edit_next_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
