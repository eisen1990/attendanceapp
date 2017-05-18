package com.planetory.io;

import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PunchTimeEditActivity extends AppCompatActivity {

    private String user_phone;
    private String user_punchin;
    private String user_punchout;

    private String Edit_error;
    private String Edit_complete;

    private EditPunch editPunch = null;

    ImageButton fab;
    TextView punchin_picker;
    TextView punchout_picker;

    private String setPunchin;
    private String setPunchout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user_phone = getIntent().getExtras().getString("user_phone");
        user_punchin = getIntent().getExtras().getString("user_punchin");
        user_punchout = getIntent().getExtras().getString("user_punchout");
        Edit_error = getString(R.string.activity_punch_edit_error);
        Edit_complete = getString(R.string.activity_punch_edit_complete);

        setContentView(R.layout.activity_punch_time_edit);

        ImageButton btn_back = (ImageButton) findViewById(R.id.activity_punch_time_edit_back_btn);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        punchin_picker = (TextView) findViewById(R.id.punch_in_picker);
        punchin_picker.setOnClickListener(timePickerClick);
        punchin_picker.setText(convertInitDate(user_punchin));

        punchout_picker = (TextView) findViewById(R.id.punch_out_picker);
        punchout_picker.setOnClickListener(timePickerClick);
        punchout_picker.setText(convertInitDate(user_punchout));


        fab = (ImageButton) findViewById(R.id.activity_punch_time_edit_next_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPunch = new EditPunch(user_phone, setPunchin, setPunchout);
                editPunch.execute();

            }
        });
    }

    TextView.OnClickListener timePickerClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.punch_in_picker:
                    new TimePickerDialog(PunchTimeEditActivity.this, punchinPickerListener, 0, 0, false).show();
                    break;
                case R.id.punch_out_picker:
                    new TimePickerDialog(PunchTimeEditActivity.this, punchoutPickerListener, 0, 0, false).show();
                    break;
            }

        }
    };

    private String convertInitDate(String date) {
        date = date.substring(0, date.length() -1);
        if(date.length() < 15) return "NULL";
        String result = "";
        String temp = date.substring(11, 13);
        int h = Integer.parseInt(temp);
        Log.d("time edit hour : ", String.valueOf(h));

        if (h < 12) result += "AM ";
        else {
            result += "PM ";
            if (h > 12) h -= 12;
        }

        result += String.valueOf(h);
        result += ":";

        temp = date.substring(14, 16);
        int m = Integer.parseInt(temp);
        Log.d("time edit minute : ", String.valueOf(m));

        if (m < 10) result += "0";
        result += String.valueOf(m);

        return result;
    }

    TimePickerDialog.OnTimeSetListener punchinPickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            punchin_picker.setText(convertTimePick(hourOfDay, minute));
            setPunchin = convertForDB(hourOfDay, minute);
            Log.d("listener set : ", setPunchin);
        }
    };

    TimePickerDialog.OnTimeSetListener punchoutPickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            punchout_picker.setText(convertTimePick(hourOfDay, minute));
            setPunchout = convertForDB(hourOfDay, minute);
        }
    };

    private String convertTimePick(int h, int m) {
        String result = "";

        if (h < 12) result += "AM ";
        else {
            result += "PM ";
            if (h > 12) h -= 12;
        }

        result += String.valueOf(h);
        result += ":";
        if (m < 10) result += "0";

        result += String.valueOf(m);

        return result;
    }

    private String convertForDB(int h, int m) {
        Calendar cal = Calendar.getInstance();


        String result = String.valueOf(cal.get(Calendar.YEAR)) + "-";
        if (cal.get(Calendar.MONTH) + 1 < 10) result += "0";
        result += String.valueOf(cal.get(Calendar.MONTH)) + "-";
        if (cal.get(Calendar.DAY_OF_MONTH) < 10) result += "0";
        result += String.valueOf(cal.get(Calendar.DAY_OF_MONTH)) + " ";

        if (h < 10) result += "0";
        result += String.valueOf(h) + ":";
        if (m < 10) result += "0";
        result += String.valueOf(m) + ":00";
        Log.d("convertForDB", result);
        return result;
    }

    private class EditPunch extends AsyncTask<Void, Void, String> {

        private String phone_db;
        private String punchin_db;
        private String punchout_db;

        EditPunch(String phone_db, String punchin_db, String punchout_db) {
            this.phone_db = phone_db;
            this.punchin_db = punchin_db;
            this.punchout_db = punchout_db;
        }

        @Override
        protected void onPostExecute(String s) {
            editPunch = null;
            s = s.substring(0, s.length() - 1);

            if (s.equals(RestURL.EDIT_ERROR)) {
                Toast.makeText(PunchTimeEditActivity.this, Edit_error, Toast.LENGTH_SHORT).show();
            } else if (s.equals(RestURL.EDIT_OK)) {
                Toast.makeText(PunchTimeEditActivity.this, Edit_complete, Toast.LENGTH_SHORT).show();
                //startActivityForResult(intent, 1)로 전달하였음
                //setResult는 resultCode 1 성공 코드로 지정.
                setResult(1);
                finish();
            } else if (s.equals(RestURL.EDIT_EXCEPTION)) {
                Toast.makeText(PunchTimeEditActivity.this, "DB exception", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("eisen", s);
            }
        }

        @Override
        protected void onCancelled() {
            editPunch = null;
        }

        @Override
        protected String doInBackground(Void... params) {

            String urlString;

            if (phone_db == null || punchin_db == null || punchout_db == null) {
                return RestURL.EDIT_ERROR;
            } else {
//                Log.d("setPunchin : ", punchin_db);
//                Log.d("setPunchout : ", punchout_db);
                urlString = RestURL.EDITPUNCH_URL + "phone=" + phone_db + "&punchin=" + punchin_db + "&punchout=" + punchout_db;
            }
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
                Log.d("PunchTimeEditActivity", "URL exception");
            }

            return RestURL.NULL_STRING + "\0";
        }
    }
}
