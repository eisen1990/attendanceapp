package com.planetory.io;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RequestWifiStoreViewAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private int layout;
    private ArrayList<RequestWifiStoreItem> requestWifiStoreItems;

    public RequestWifiStoreViewAdapter(Context context, int layout, ArrayList<RequestWifiStoreItem> requestWifiStoreItems) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
        this.requestWifiStoreItems = requestWifiStoreItems;
    }

    @Override
    public int getCount() {
        return requestWifiStoreItems.size();
    }

    @Override
    public Object getItem(int position) {
        return requestWifiStoreItems.get(position).getStore_name();
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

        RequestWifiStoreItem requestWifiStoreItem = requestWifiStoreItems.get(position);

        TextView store_name = (TextView) convertView.findViewById(R.id.store_name);
        TextView store_addr = (TextView) convertView.findViewById(R.id.store_addr);

        store_name.setText(requestWifiStoreItem.getStore_name());
        store_addr.setText(requestWifiStoreItem.getStore_address());
        return convertView;
    }
}
