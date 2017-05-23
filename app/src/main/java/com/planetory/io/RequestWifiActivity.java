package com.planetory.io;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class RequestWifiActivity extends AppCompatActivity {

    private String wifiaplist;
    private String user_phone;
    private String store_name;
    private RequestWifiURL requestWifiURL = null;

    static final String INTENT_USER_PHONE = "user_phone";
    static final String INTENT_WIFI_LIST = "wifiaplist";
    static final String INTENT_STORE_NAME = "store_name";

    ConstraintLayout constraintLayout;
    TextView store_info;
    ImageButton back_button;
    Button requestBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wifiaplist = getIntent().getExtras().getString(INTENT_WIFI_LIST);
        user_phone = getIntent().getExtras().getString(INTENT_USER_PHONE);
        store_name = getIntent().getExtras().getString(INTENT_STORE_NAME);
        setContentView(R.layout.activity_request_wifi);

        constraintLayout = (ConstraintLayout) findViewById(R.id.activity_request_wifi_layout);

        back_button = (ImageButton) findViewById(R.id.activity_back_btn);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        store_info = (TextView) findViewById(R.id.store_info);
        store_info.setText(store_name);

        requestBtn = (Button) findViewById(R.id.requestButton);
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestWifiURL = new RequestWifiURL(wifiaplist, store_name, user_phone);
                requestWifiURL.execute();
            }
        });
    }

    private class RequestWifiURL extends AsyncTask<Void, Void, String> {

        private String requestwifilist;
        private String requeststore;
        private String requestphone;

        public RequestWifiURL(String requestwifilist, String requeststore, String requestphone) {
            this.requestwifilist = requestwifilist;
            this.requeststore = requeststore;
            this.requestphone = requestphone;
        }

        @Override
        protected void onPostExecute(String s) {
            requestWifiURL = null;
            s = s.substring(0, s.length() - 1);

            if (s.equals("REQUEST")) {
                LayoutInflater inflater = LayoutInflater.from(RequestWifiActivity.this);
                View dView = inflater.inflate(R.layout.dialog_store_apply, constraintLayout, false);
                TextView txtMessage = (TextView) dView.findViewById(R.id.dialog_store_info);
                txtMessage.setText(store_name);

                AlertDialog.Builder builder = new AlertDialog.Builder(RequestWifiActivity.this);
                builder.setView(dView);
                builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(RequestWifiActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

                //다이얼로그를 표시하는 부분
                builder.create().show();


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
            String urlString = RestURL.REQUESTWIFI_URL + "phone=" + requestphone + "&store_name=" + requeststore + "&wifiaplist=" + requestwifilist;

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
