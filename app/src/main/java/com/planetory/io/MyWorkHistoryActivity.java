package com.planetory.io;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

public class MyWorkHistoryActivity extends AppCompatActivity {

    private ListView historyListView;
    private HistoryListViewAdapter historyListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_work_history);

        historyListView = (ListView) findViewById(R.id.work_history_list);

        ArrayList<HistoryItem> historyItems = new ArrayList<>();

        for(int i = 0 ; i < 100 ; i++) {
            historyItems.add(new HistoryItem(String.valueOf(i%12)+"월"+String.valueOf(i%30)+"일",
                    String.valueOf(i%12)+":"+String.valueOf(i%60),
                    String.valueOf(i%12+12)+":"+String.valueOf(i%60)));
        }

        historyListViewAdapter = new HistoryListViewAdapter(this, R.layout.history_item,historyItems);
        historyListView.setAdapter(historyListViewAdapter);


    }
}
