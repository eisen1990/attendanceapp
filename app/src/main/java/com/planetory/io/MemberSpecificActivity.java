package com.planetory.io;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MemberSpecificActivity extends AppCompatActivity {

    MemberSchduleURLTask memberSchduleURLTask;

    private String user_phone;

    TextView spec_phone;
    TextView spec_store;
    TextView spec_punch;
    ImageButton time_edit_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user_phone = getIntent().getExtras().getString("specific_phone");
        setContentView(R.layout.activity_member_specific);

        buttonSetting();

        memberSchduleURLTask = new MemberSchduleURLTask(user_phone);
        memberSchduleURLTask.execute();
    }

    private void contentSetting(final String punchin, final String punchout, String store) {
        spec_phone = (TextView) findViewById(R.id.specific_member_phone);
        spec_phone.setText(user_phone);

        spec_punch = (TextView) findViewById(R.id.specific_member_punch);
        spec_punch.setText(punchin + "\n" + punchout);

        spec_store = (TextView) findViewById(R.id.specific_member_store);
        spec_store.setText(store);

        time_edit_button = (ImageButton) findViewById(R.id.edit_punchtime);
        time_edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MemberSpecificActivity.this, PunchTimeEditActivity.class);
                intent.putExtra("user_phone", user_phone);
                intent.putExtra("user_punchin", punchin);
                intent.putExtra("user_punchout", punchout);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void buttonSetting() {
        ImageButton btn_back = (ImageButton) findViewById(R.id.activity_member_specific_back_btn);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("result 굿? ", String.valueOf(resultCode));
        if(resultCode == 1) {
            //resultCode 1로 PunchTimeEditActivity로부터 받는다. 성공 코드
            memberSchduleURLTask = new MemberSchduleURLTask(user_phone);
            memberSchduleURLTask.execute();
        }
    }

    private class MemberSchduleURLTask extends AsyncTask<Void, Void, String> {

        private String spec_phone;

        MemberSchduleURLTask(String spec_phone) {
            this.spec_phone = spec_phone;
        }

        @Override
        protected void onPostExecute(String s) {

            String punchin = "";
            String punchout = "";
            String store = "";

            memberSchduleURLTask = null;
            if (s == null) {
                s = "specMember Fail";
            }

            if (s.indexOf('{') >= 0) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("List");

                    punchin = jsonArray.getJSONObject(0).getString("punchin");
                    punchout = jsonArray.getJSONObject(1).getString("punchout");
                    store = jsonArray.getJSONObject(2).getString("store");

                    Log.d("eisen", punchin);
                    Log.d("eisen", punchout);
                    Log.d("eisen", store);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                //JSON 타입이 아닐 경우 에러 메세지이다.
                Log.d("eisen", s);
            }
            Log.d("eisen", s);
            contentSetting(punchin, punchout, store);
        }

        @Override
        protected void onCancelled() {
            memberSchduleURLTask = null;
        }

        @Override
        protected String doInBackground(Void... params) {
            String urlString = RestURL.SPECMEMBER_URL + "phone=" + spec_phone;

            Log.d("eisen", urlString);

            StringBuilder output = new StringBuilder();

            try {
                URL url = new URL(urlString);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setRequestMethod("GET");
                int resCode = httpURLConnection.getResponseCode();
                if (resCode == httpURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        output.append(line + "\n");
                    }
                }
                return output.toString();

            } catch (Exception e) {
                Log.d("MainActivity", "URL exception");
            }

            return "URLerror";
        }
    }

}
