package com.example.pastilleroapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends Activity {

    private ListView lvHistoryList;
    private TextView tvHistoryEmptyMessage;
    private List<String> historyList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        lvHistoryList = findViewById(R.id.lvHistoryList);
        tvHistoryEmptyMessage = findViewById(R.id.tvHistoryEmptyMessage);

        // Cargar historial desde almacenamiento (esto puede cambiar)
        historyList = ScheduleStorage.loadHistory(this); // Este método lo creamos ahora

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyList);
        lvHistoryList.setAdapter(adapter);

        if (historyList.isEmpty()) {
            tvHistoryEmptyMessage.setVisibility(View.VISIBLE);
            lvHistoryList.setVisibility(View.GONE);
        } else {
            tvHistoryEmptyMessage.setVisibility(View.GONE);
            lvHistoryList.setVisibility(View.VISIBLE);
        }
    }
}
