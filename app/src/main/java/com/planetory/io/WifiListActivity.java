package com.planetory.io;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;

public class WifiListActivity extends AppCompatActivity {

    private ListView wifiListView;
    private WifiListViewAdapter wifiListViewAdapter;
    WifiControl wifiControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_list);

        wifiListView = (ListView) findViewById(R.id.wifi_list);

        ArrayList<WifiItem> wifiItems = new ArrayList<>();

        wifiControl = new WifiControl(this);
//        wifiItems = (ArrayList<WifiItem>) wifiControl.scanWifi().clone();
        wifiItems = wifiControl.scanWifi();
//        for (WifiItem wifiItem : wifiItems) Log.d("리스트 " + String.valueOf(wifiItems.indexOf(wifiItem)) + " : ", wifiItem.getBSSID());



        wifiListViewAdapter = new WifiListViewAdapter(this, R.layout.wifi_item, wifiItems);
        wifiListView.setAdapter(wifiListViewAdapter);
    }
}
