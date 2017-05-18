package com.planetory.io;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class AcceptWifiViewAdapter extends BaseAdapter{

    private LayoutInflater inflater;
    private int layout;
    private ArrayList<AcceptWifiItem> acceptWifiItems;

    public AcceptWifiViewAdapter(Context context, int layout, ArrayList<AcceptWifiItem> acceptWifiItems) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
        this.acceptWifiItems = acceptWifiItems;
    }

    @Override
    public int getCount() {
        return acceptWifiItems.size();
    }

    @Override
    public Object getItem(int position) {
        return acceptWifiItems.get(position).getBSSID();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
        }

        AcceptWifiItem acceptWifiItem = acceptWifiItems.get(position);

        TextView store = (TextView) convertView.findViewById(R.id.wifi_store_item);
        TextView bssid = (TextView) convertView.findViewById(R.id.wifi_bssid_item);

        store.setText(acceptWifiItem.getStore());
        bssid.setText(acceptWifiItem.getBSSID());

        return convertView;
    }
}
