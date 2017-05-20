package com.planetory.io;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AcceptWifiActivity extends AppCompatActivity {

    private AcceptWifiURL acceptWifiURL = null;
    private ListView wifiListView;
    private AcceptWifiViewAdapter acceptWifiViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_wifi);
        wifiListView = (ListView) findViewById(R.id.wifi_list);
        acceptWifiURL = new AcceptWifiURL();
        acceptWifiURL.execute("list");
    }

    private class AcceptWifiURL extends AsyncTask<String, Void, String> {

        private String bssid;

        public AcceptWifiURL() {
            this.bssid = null;
        }

        public AcceptWifiURL(String bssid) {
            this.bssid = bssid;
        }

        @Override
        protected void onPostExecute(String s) {
            acceptWifiURL = null;

            ArrayList<AcceptWifiItem> AcceptWifiItems = new ArrayList<>();
            if (s == null) {
                s = "getAcceptWifiList Fail";
            }

            s = s.substring(0, s.length() - 1);

            if (s.indexOf('{') >= 0) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("List");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONArray innerArray = jsonArray.getJSONArray(i);
                        String store = innerArray.getJSONObject(0).getString("store");
                        String wifiap = innerArray.getJSONObject(1).getString("wifiap");

                        AcceptWifiItem acceptWifiItem = new AcceptWifiItem(store, wifiap);
                        AcceptWifiItems.add(acceptWifiItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (AcceptWifiItem result : AcceptWifiItems) {
                    Log.d("result 1 : ", result.getStore());
                    Log.d("result 2 : ", result.getBSSID());
                }
                s = s.substring(0, s.length() - 1);
                Log.d("getWifiItems JSON", s);

                acceptWifiViewAdapter = new AcceptWifiViewAdapter(AcceptWifiActivity.this, R.layout.accept_wifi_item, AcceptWifiItems);
                wifiListView.setAdapter(acceptWifiViewAdapter);

                wifiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        Toast.makeText(MemberInfoActivity.this, (String) parent.getAdapter().getItem(position),Toast.LENGTH_SHORT).show();
                        acceptWifiURL = new AcceptWifiURL((String) parent.getAdapter().getItem(position));
                        acceptWifiURL.execute("ok");
                    }
                });
            } else if( s.equals("ACCEPT")) {
                Toast.makeText(AcceptWifiActivity.this, "Wifi 승인 완료되었습니다.", Toast.LENGTH_SHORT).show();
            }
            else {
                Log.d("eisen", s);
            }
            Log.d("eisen", s);
        }

        @Override
        protected void onCancelled() {
            acceptWifiURL = null;
        }

        @Override
        protected String doInBackground(String... params) {
            String urlString;
            if(params[0].equals("list"))
            urlString = RestURL.ACCEPTWIFI_URL;
            else urlString = RestURL.ACCEPTOKWIFI_URL + "bssid=" + bssid;
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
                Log.d("AcceptWifiActivity", "URL exception");
            }

            return "URLerror";
        }
    }
}
