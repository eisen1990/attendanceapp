package com.planetory.io;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.ArrayList;

public class HistoryListViewAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private int layout;
    private ArrayList<HistoryItem> listData;

    public HistoryListViewAdapter(Context context, int layout, ArrayList<HistoryItem> listData) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
        this.listData = listData;

    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position).getDate();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*
            추후 Recycler View 패턴을 구현하여 List 목록이 많을 때 View 재사용 구현 예정
            ViewHolder 클래스를 참조할 것.
         */
        if(convertView == null) {
            convertView = inflater.inflate(layout,parent,false);
        }

        HistoryItem historyItem = listData.get(position);

        TextView historyDateView = (TextView) convertView.findViewById(R.id.work_day);
        historyDateView.setText(historyItem.getDate());

        TextView historyPunchInView = (TextView) convertView.findViewById(R.id.punch_in);
        historyPunchInView.setText(historyItem.getPunch_in());

        TextView historyPunchOutView = (TextView) convertView.findViewById(R.id.punch_out);
        historyPunchOutView.setText(historyItem.getPunch_out());

        return convertView;
    }
}
