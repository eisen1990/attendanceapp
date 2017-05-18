package com.planetory.io;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestWifiActivity extends AppCompatActivity {

    private String wifiaplist;
    private String user_phone;
    private RequestWifiURL requestWifiURL = null;

    Button requestBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wifiaplist = getIntent().getExtras().getString("wifiaplist");
        user_phone = getIntent().getExtras().getString("user_phone");
        setContentView(R.layout.activity_request_wifi);

        requestBtn = (Button) findViewById(R.id.requestButton);
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestWifiURL = new RequestWifiURL(wifiaplist, user_phone);
                requestWifiURL.execute();
            }
        });
    }

    private class RequestWifiURL extends AsyncTask<Void, Void, String> {

        private String requestwifilist;
        private String requestphone;

        public RequestWifiURL(String requestwifilist, String requestphone) {
            this.requestwifilist = requestwifilist;
            this.requestphone = requestphone;
        }

        @Override
        protected void onPostExecute(String s) {
            requestWifiURL = null;
            s = s.substring(0, s.length() - 1);

            if (s.equals("REQUEST")) {
                Toast.makeText(RequestWifiActivity.this, "Wifi 등록 신청 완료", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Log.d("request wifi", s);
                Toast.makeText(RequestWifiActivity.this, "Wifi 등록 실패", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            requestWifiURL = null;
        }

        @Override
        protected String doInBackground(Void... params) {
            String urlString = RestURL.REQUESTWIFI_URL + "phone=" + requestphone + "&wifiaplist=" + requestwifilist;

            Log.d("eisen", urlString);

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
                Log.d("RequestWifiActivity", "URL exception");
            }

            return RestURL.NULL_STRING + "\0";
        }
    }
}
