package com.planetory.io;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user_phone = getIntent().getExtras().getString("specific_phone");

        setContentView(R.layout.activity_member_specific);

        memberSchduleURLTask = new MemberSchduleURLTask(user_phone);
        memberSchduleURLTask.execute();
    }

    private void buttonSetting(String punchin, String punchout, String store) {
        spec_phone = (TextView) findViewById(R.id.specific_member_phone);
        spec_phone.setText(user_phone);

        spec_punch = (TextView) findViewById(R.id.specific_member_punch);
        spec_punch.setText(punchin + "\n" + punchout);

        spec_store = (TextView) findViewById(R.id.specific_member_store);
        spec_store.setText(store);
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
            buttonSetting(punchin, punchout, store);
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
