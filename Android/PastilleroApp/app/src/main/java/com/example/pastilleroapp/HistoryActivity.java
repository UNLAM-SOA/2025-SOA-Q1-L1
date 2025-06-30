package com.example.pastilleroapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private ListView lvHistoryList;
    private TextView tvHistoryEmptyMessage;
    private List<String> historyList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.history), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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