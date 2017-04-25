package com.planetory.io;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class WifiListViewAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private int layout;
    private ArrayList<WifiItem> wifiItems;

    public WifiListViewAdapter(Context context, int layout, ArrayList<WifiItem> wifiItems) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
        this.wifiItems = wifiItems;
        for (WifiItem wifiItem : this.wifiItems)
            Log.d("리스트 " + String.valueOf(this.wifiItems.indexOf(wifiItem)) + " : ", wifiItem.getBSSID());
    }

    @Override
    public int getCount() {
        return wifiItems.size();
    }

    @Override
    public Object getItem(int position) {
        return wifiItems.get(position).getBSSID();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
        }

        WifiItem wifiItem = wifiItems.get(position);

        TextView bssid = (TextView) convertView.findViewById(R.id.wifi_bssid);
        TextView ssid = (TextView) convertView.findViewById(R.id.wifi_ssid);
        TextView capa = (TextView) convertView.findViewById(R.id.wifi_capability);
        TextView freq = (TextView) convertView.findViewById(R.id.wifi_freq);

        bssid.setText(wifiItem.getBSSID());
        ssid.setText(wifiItem.getSSID());
        capa.setText(wifiItem.getCapabilities());
        freq.setText(wifiItem.getFreq());


        return convertView;
    }
}
