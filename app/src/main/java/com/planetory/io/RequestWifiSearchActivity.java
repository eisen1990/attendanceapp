package com.planetory.io;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RequestWifiSearchActivity extends AppCompatActivity {

    SearchStoreURL searchStoreURL;
    private ListView searchStoreView;
    private RequestWifiStoreViewAdapter requestWifiStoreViewAdapter;

    EditText search_text;
    ImageButton back_button;
    ImageButton search_button;
    TextView search_blank;

    static final String INTENT_USER_PHONE = "user_phone";
    static final String INTENT_WIFI_LIST = "wifiaplist";
    static final String INTENT_STORE_NAME = "store_name";
    static final String INTENT_STORE_ADDRESS = "store_addr";

    private String user_phone;
    private String wifilist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user_phone = getIntent().getExtras().getString(INTENT_USER_PHONE);
        wifilist = getIntent().getExtras().getString(INTENT_WIFI_LIST);
        setContentView(R.layout.activity_request_wifi_search);

        searchStoreView = (ListView) findViewById(R.id.store_list);
        searchStoreURL = new SearchStoreURL();
        searchStoreURL.execute("all");

        search_text = (EditText) findViewById(R.id.activity_edit);
        back_button = (ImageButton) findViewById(R.id.activity_back_btn);
        search_button = (ImageButton) findViewById(R.id.store_search_button);
        search_blank = (TextView) findViewById(R.id.store_blank);
        search_blank.setText(R.string.activity_request_store_blank);
        search_blank.setVisibility(View.GONE);

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = search_text.getText().toString();
                if (search == null || search.equals("")) {
                    search = "all";
                    Log.d("빈 박스", "인걸까");
                }
                searchStoreURL = new SearchStoreURL();
                searchStoreURL.execute(search);
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class SearchStoreURL extends AsyncTask<String, Void, String> {

        private String store_name;
        private String store_addr;

        public SearchStoreURL() {
            this.store_name = null;
            this.store_addr = null;
        }

        public SearchStoreURL(String store_name, String store_addr) {
            this.store_name = store_name;
            this.store_addr = store_addr;
        }

        @Override
        protected void onPostExecute(String s) {
            searchStoreURL = null;
            boolean hasData = false;

            ArrayList<RequestWifiStoreItem> requestWifiStoreItems = new ArrayList<>();
            if (s == null) s = "getStoreList Fail";

            Log.d("RequestWifiSearch", s);

            s = s.substring(0, s.length() - 1);
            if (s.indexOf('{') >= 0) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("List");
                    if (jsonArray.length() > 0) hasData = true;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONArray innerArray = jsonArray.getJSONArray(i);
                        String store_name = innerArray.getJSONObject(0).getString("store_name");
                        String store_addr = innerArray.getJSONObject(1).getString("store_addr");

                        RequestWifiStoreItem requestWifiStoreItem = new RequestWifiStoreItem(store_name, store_addr);
                        requestWifiStoreItems.add(requestWifiStoreItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (RequestWifiStoreItem result : requestWifiStoreItems) {
                    Log.d("result 1 : ", result.getStore_name());
                    Log.d("result 2 : ", result.getStore_address());
                }
                Log.d("getWifiItems JSON", s);
                if (hasData) {
                    requestWifiStoreViewAdapter = new RequestWifiStoreViewAdapter(RequestWifiSearchActivity.this, R.layout.search_store_item, requestWifiStoreItems);
                    searchStoreView.setAdapter(requestWifiStoreViewAdapter);

                    searchStoreView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(RequestWifiSearchActivity.this, RequestWifiActivity.class);
                            intent.putExtra(INTENT_STORE_NAME, (String) parent.getAdapter().getItem(position));
                            intent.putExtra(INTENT_WIFI_LIST, wifilist);
                            intent.putExtra(INTENT_USER_PHONE, user_phone);
                            startActivity(intent);
                        }
                    });
                    search_blank.setVisibility(View.GONE);
                } else {
                    search_blank.setVisibility(View.VISIBLE);
                    searchStoreView.setVisibility(View.GONE);
                }

            } else if (s.equals("searchStoreEXCEPTION")) {
                Log.d("RequestWifiSearch", "URL exception");
            }
        }

        @Override
        protected void onCancelled() {
            searchStoreURL = null;
        }

        @Override
        protected String doInBackground(String... params) {
            String urlString = RestURL.SEARCHSTORE_URL + "search=" + params[0];

            StringBuilder output = new StringBuilder();
            Log.d("RequestWifiSearch", urlString);
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
                Log.d("RequestWifiSearchActiv", "URL exception");
            }

            return "URLerror";
        }
    }
}
