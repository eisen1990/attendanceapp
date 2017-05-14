package com.planetory.io;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MemberInfoViewAdapter extends BaseAdapter{

    private LayoutInflater inflater;
    private int layout;
    private ArrayList<MemberItem> memberItems;

    public MemberInfoViewAdapter(Context context, int layout, ArrayList<MemberItem> memberItems) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
        this.memberItems = memberItems;
    }

    @Override
    public int getCount() { return memberItems.size(); }

    @Override
    public Object getItem(int position) { return memberItems.get(position).getPhone(); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
        }

        MemberItem memberItem = memberItems.get(position);

        TextView phone = (TextView) convertView.findViewById(R.id.member_phone);
        TextView name = (TextView) convertView.findViewById(R.id.member_name);

        phone.setText(memberItem.getPhone());
        name.setText(memberItem.getName());

        return convertView;
    }
}
