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

public class MemberInfoActivity extends AppCompatActivity {

    private GetMemberURLTask getMemberURL = null;
    private ListView memberInfoListView;
    private MemberInfoViewAdapter memberInfoViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_info);
        memberInfoListView = (ListView) findViewById(R.id.member_list);
        getMemberURL = new GetMemberURLTask();
        getMemberURL.execute();
    }

    private class GetMemberURLTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            getMemberURL = null;

            ArrayList<MemberItem> memberItems = new ArrayList<>();
            if (s == null) {
                s = "getMembers Fail";
            }

            if (s.indexOf('{') >= 0) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("List");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONArray innerArray = jsonArray.getJSONArray(i);
                        String phone = innerArray.getJSONObject(0).getString("phone");
                        String name = innerArray.getJSONObject(1).getString("name");

                        MemberItem memberItem = new MemberItem(phone, name);
                        memberItems.add(memberItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (MemberItem result : memberItems) {
                    Log.d("result 1 : ", result.getPhone());
                    Log.d("result 2 : ", result.getName());
                }
                s = s.substring(0, s.length() - 1);
                Log.d("getMembers JSON", s);

                memberInfoViewAdapter = new MemberInfoViewAdapter(MemberInfoActivity.this, R.layout.member_item, memberItems);
                memberInfoListView.setAdapter(memberInfoViewAdapter);

                memberInfoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        Toast.makeText(MemberInfoActivity.this, (String) parent.getAdapter().getItem(position),Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MemberInfoActivity.this, MemberSpecificActivity.class);
                        intent.putExtra("specific_phone", (String) parent.getAdapter().getItem(position));
                        startActivity(intent);
                    }
                });
            } else {
                Log.d("eisen", s);
            }
            Log.d("eisen", s);
        }

        @Override
        protected void onCancelled() {
            getMemberURL = null;
        }

        @Override
        protected String doInBackground(Void... params) {
            String urlString = RestURL.GETMEMBERS_URL;

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
                Log.d("MemberInfoActivity", "URL exception");
            }

            return "URLerror";
        }
    }
}
